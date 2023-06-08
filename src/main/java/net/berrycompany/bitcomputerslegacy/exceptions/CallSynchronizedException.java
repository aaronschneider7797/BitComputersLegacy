package net.berrycompany.bitcomputerslegacy.exceptions;

import li.cil.oc.api.machine.Context;

/*
 * Allows us to re invoke the desired method without resorting to repeating the
 * last instruction, which has side effects.
 */
public class CallSynchronizedException extends RuntimeException {

	private Cleanup cleanup;
	private final Object thing;
	private final String method;
	private final Object[] args;

	public CallSynchronizedException(Object thing, String method, Object[] args) {
		super();
		this.thing = thing;
		this.method = method;
		this.args = args;
	}

	public void setRunnable(Cleanup cleanup) {
		this.cleanup = cleanup;
	}

	public Cleanup getCleanup() {
		return cleanup;
	}

	public Object getThing() {
		return thing;
	}

	public String getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}

	public abstract static class Cleanup {
		public void run(Object[] results, Context context) {}

		public void error(Exception error) {}

		public void finish() {}
	}
}
