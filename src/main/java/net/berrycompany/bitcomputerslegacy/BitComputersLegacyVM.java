package net.berrycompany.bitcomputerslegacy;

import com.loomcom.symon.Cpu;

import li.cil.oc.api.machine.Context;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BitComputersLegacyVM {
	// The simulated machine
	public BitComputersLegacyMachine machine;

	// Allocated cycles per tick
	public int cyclesPerTick;

	public BitComputersLegacyVM(Context context) {
		super();
		MinecraftForge.EVENT_BUS.register(this);
		try {
			machine = new BitComputersLegacyMachine(context);
			if (context.node().network() == null) {
				// Loading from NBT
				return;
			}
			machine.getCpu().reset();
		} catch (Exception e) {
			throw new RuntimeException("Failed to setup BitComputers", e);
		}
	}

	void run() throws Exception {
		machine.getComponentSelector().checkDelay();
		Cpu mCPU = machine.getCpu();
		while (mCPU.getCycles() > 0) {
			mCPU.step();
		}
		machine.getGioDev().flush();
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		Context context = machine.getContext();
		if (!context.isRunning() && !context.isPaused()) {
			MinecraftForge.EVENT_BUS.unregister(this);
			return;
		}
		if (event.phase != TickEvent.Phase.START) {
			return;
		}
		Cpu mCPU = machine.getCpu();
		if (mCPU.getCycles() < cyclesPerTick) {
			mCPU.addCycles(cyclesPerTick);
		}
		machine.getRTC().onServerTick();
	}
}
