package gamax92.thistle.api;

import li.cil.oc.api.machine.Context;

public interface IThistleDevice {

	int lengthThistle();

	int readThistle(Context context, int address);

	void writeThistle(Context context, int address, int data);
}
