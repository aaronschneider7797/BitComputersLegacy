package net.berrycompany.bitcomputerslegacy.api;

import li.cil.oc.api.machine.Context;

public interface IBitComputersLegacyDevice {

	int length();

	int read(Context context, int address);

	void write(Context context, int address, int data);
}
