package net.berrycompany.bitcomputers.api;

import li.cil.oc.api.machine.Context;

public interface IBitComputersDevice {

	int length();

	int read(Context context, int address);

	void write(Context context, int address, int data);
}
