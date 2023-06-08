package net.berrycompany.bitcomputerslegacy.api;

import li.cil.oc.api.network.Environment;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BitComputersLegacyWrapper implements IBitComputersLegacyDevice {

	private final Environment host;

	public BitComputersLegacyWrapper(Environment host) {
		this.host = host;
	}

	public Environment host() {
		return host;
	}

	public void load(NBTTagCompound nbt) {
	}

	public void save(NBTTagCompound nbt) {
	}
}
