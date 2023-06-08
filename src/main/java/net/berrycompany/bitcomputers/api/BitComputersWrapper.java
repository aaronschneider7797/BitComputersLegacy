package net.berrycompany.bitcomputers.api;

import li.cil.oc.api.network.Environment;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BitComputersWrapper implements IBitComputersDevice {

	private final Environment host;

	public BitComputersWrapper(Environment host) {
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
