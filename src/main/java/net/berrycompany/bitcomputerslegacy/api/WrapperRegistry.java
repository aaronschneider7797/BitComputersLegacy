package net.berrycompany.bitcomputerslegacy.api;

import java.util.HashMap;
import java.util.Map;

import li.cil.oc.api.network.Environment;

public class WrapperRegistry {

	private static final WrapperRegistry INSTANCE = new WrapperRegistry();

	private final Map<Class<? extends Environment>, Class<? extends BitComputersLegacyWrapper>> registry = new HashMap<>();

	public static WrapperRegistry instance() {
		return INSTANCE;
	}

	@SuppressWarnings("unused")
	public static void registerWrapper(Class<? extends Environment> host, Class<? extends BitComputersLegacyWrapper> wrapper) {
		instance().registry.put(host, wrapper);
	}

	public static Class<? extends BitComputersLegacyWrapper> getWrapper(Class<? extends Environment> host) {
		return instance().registry.get(host);
	}
}
