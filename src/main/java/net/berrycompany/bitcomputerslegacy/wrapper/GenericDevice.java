package net.berrycompany.bitcomputerslegacy.wrapper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.berrycompany.bitcomputerslegacy.BitComputersLegacy;
import net.berrycompany.bitcomputerslegacy.BitComputersLegacyConfig;
import net.berrycompany.bitcomputerslegacy.util.TSFHelper;
import net.berrycompany.bitcomputerslegacy.util.UUIDHelper;
import org.apache.commons.lang3.ArrayUtils;

import net.berrycompany.bitcomputerslegacy.BitComputersLegacyArchitecture;
import net.berrycompany.bitcomputerslegacy.api.BitComputersLegacyWrapper;
import net.berrycompany.bitcomputerslegacy.exceptions.CallSynchronizedException;
import net.berrycompany.bitcomputerslegacy.exceptions.CallSynchronizedException.Cleanup;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.Value;
import li.cil.oc.api.network.Environment;
import net.minecraft.nbt.NBTTagCompound;
import li.cil.oc.api.network.Component;

public class GenericDevice extends BitComputersLegacyWrapper {

	private final Queue<Byte> inputbuf = new LinkedList<>();
	private final Queue<Byte> outputbuf = new LinkedList<>();
	private byte[] uuid;
	private String name;
	private int status = 0;
	private int flag = 0x00;

	private final Cleanup cleanup = new Cleanup() {
		@Override
		public void run(Object[] results, Context context) {
			status = 0;
			if (BitComputersLegacyConfig.debugComponentCalls)
				BitComputersLegacy.log.info("[Generic] (" + host().node().address() + ") Results: " + Arrays.deepToString(results));
			if (results != null) {
				TSFHelper.writeArray(outputbuf, results, context, flag);
			}
		}
		@Override
		public void error(Exception e) {
			if (BitComputersLegacyConfig.debugComponentCalls)
				BitComputersLegacy.log.info("[Generic] (" + host().node().address() + ") Error: ", e);
			if (e instanceof IllegalArgumentException) {
				status = 2;
			} else {
				status = 1;
				String message = e.getMessage();
				if (message != null) {
					TSFHelper.writeString(outputbuf, message);
				}
			}
		}
		@Override
		public void finish() {
			TSFHelper.writeEnd(outputbuf);
			inputbuf.clear();
		}
	};

	static final int GENERIC_STATUS_REG = 0;
	static final int GENERIC_IO_REG = 1;
	static final int GENERIC_FLAG_REG = 2;
	static final int GENERIC_UUID_REG = 4;
	static final int GENERIC_NAME_REG = 20;
	static final int GENERIC_SLOT_REG = 52;
	static final int GENERIC_RESERVED = 53;

	public GenericDevice(Environment host) {
		super(host);
		name = ((Component) host.node()).name();
		uuid = UUIDHelper.encodeUUID(host.node().address());
	}

	@Override
	public int length() {
		return GENERIC_RESERVED;
	}

	@Override
	public int read(Context context, int address) {
		if (address == GENERIC_STATUS_REG) {
			return this.status;
		} else if (address == GENERIC_IO_REG) {
			Byte data = outputbuf.poll();
			return data != null ? data : 0;
		} else if (address == GENERIC_FLAG_REG) {
			return flag;
		} else if (address >= GENERIC_UUID_REG && address < GENERIC_NAME_REG) {
			return uuid[address - GENERIC_UUID_REG];
		} else if (address >= GENERIC_NAME_REG && address < GENERIC_SLOT_REG) {
			int namepos = address - GENERIC_NAME_REG;
			if (namepos < name.length())
				return name.charAt(namepos);
			else
				return 0;
		} else if (address == GENERIC_SLOT_REG) {
			return ((Machine) context).host().componentSlot(host().node().address());
		} else {
			return 0;
		}
	}

	@Override
	public void write(Context context, int address, int data) {
		Object[] tsfdata;
		switch (address) {
		case GENERIC_STATUS_REG:
			outputbuf.clear();
			switch (data) {
			case 0: // invoke
				tsfdata = TSFHelper.readArray(inputbuf, context, flag);
				if (BitComputersLegacyConfig.debugComponentCalls)
					BitComputersLegacy.log.info("[Generic] (" + host().node().address() + ") Invoke: " + Arrays.deepToString(tsfdata));
				if (tsfdata == null || tsfdata.length < 1) {
					status = 3;
					break;
				}
				try {
					Object[] results;
					BitComputersLegacyArchitecture bitComputers = ((BitComputersLegacyArchitecture) ((Machine) context).architecture());
					if (tsfdata[0] instanceof String) {
						Object[] args = Arrays.copyOfRange(tsfdata, 1, tsfdata.length);
						results = bitComputers.invoke(host().node().address(), (String) tsfdata[0], args);
					} else if (tsfdata.length >= 2 && tsfdata[0] instanceof Value && tsfdata[1] instanceof String) {
						Object[] args = Arrays.copyOfRange(tsfdata, 2, tsfdata.length);
						results = bitComputers.invoke((Value) tsfdata[0], (String) tsfdata[1], args);
					} else {
						status = 3;
						break;
					}
					cleanup.run(results, context);
				} catch (CallSynchronizedException e) {
					e.setRunnable(cleanup);
					throw e;
				} catch (Exception e) {
					cleanup.error(e);
				}
				break;
			case 1: // device info
				status = 1;
				if (host() instanceof DeviceInfo) {
					Map<String, String> info = ((DeviceInfo) host()).getDeviceInfo();
					if (info != null) {
						status = 0;
						for (Map.Entry<String, String> entry : info.entrySet()) {
							TSFHelper.writeString(outputbuf, entry.getKey());
							TSFHelper.writeString(outputbuf, entry.getValue());
						}
					}
				}
				break;
			case 2: // methods
				status = 0;
				Map<String, Callback> methods = ((Machine) context).methods(host());
				for (String method: methods.keySet()) {
					status++;
					TSFHelper.writeString(outputbuf, method);
				}
				break;
			case 3: // documentation
				status = 3;
				break;
			default:
				status = 0xFF;
			}
			cleanup.finish();
			break;
		case GENERIC_IO_REG:
			inputbuf.add((byte) data);
			break;
		case GENERIC_FLAG_REG:
			flag = data;
			break;
		}
	}

	@Override
	public void load(NBTTagCompound nbt) {
		this.status = nbt.getInteger("status");
		this.flag = nbt.getInteger("flag");
		byte[] uuid = nbt.getByteArray("uuid");
		if (uuid.length == 16)
			this.uuid = uuid;
		if (nbt.hasKey("name"))
			this.name = nbt.getString("name");
		inputbuf.clear();
		inputbuf.addAll(Arrays.asList(ArrayUtils.toObject(nbt.getByteArray("input"))));
		outputbuf.clear();
		outputbuf.addAll(Arrays.asList(ArrayUtils.toObject(nbt.getByteArray("output"))));
	}

	@Override
	public void save(NBTTagCompound nbt) {
		nbt.setInteger("status", this.status);
		nbt.setInteger("flag", this.flag);
		nbt.setByteArray("uuid", this.uuid);
		if (this.name != null)
			nbt.setString("name", this.name);
		nbt.setByteArray("input", ArrayUtils.toPrimitive(inputbuf.toArray(new Byte[0])));
		nbt.setByteArray("output", ArrayUtils.toPrimitive(outputbuf.toArray(new Byte[0])));
	}
}
