package erogenousbeef.bigreactors.common.multiblock.tileentity.creative;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.helpers.CoolantContainer;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorCoolantPort;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityReactorCreativeCoolantPort extends TileEntityReactorCoolantPort implements ITickableMultiblockPart {

	public TileEntityReactorCreativeCoolantPort() {
		super();
	}

	@Override
	public void onMultiblockServerTick() {
		if(!isConnected()) { return; }
		
		MultiblockReactor reactor = getReactorController();

		if (this.getDirection().isInput()) {

			CoolantContainer cc = reactor.getCoolantContainer();
			if(cc.getCoolantAmount() < cc.getCapacity())
			{
				reactor.getCoolantContainer().addCoolant(new FluidStack(FluidRegistry.WATER, cc.getCapacity()));
			}
		}
		else {
			reactor.getCoolantContainer().emptyVapor();
		}
	}

	public void forceAddWater() {
		if(!isConnected()) { return; }
		
		MultiblockReactor reactor = getReactorController();
		reactor.getCoolantContainer().addCoolant(new FluidStack(FluidRegistry.WATER, 1000));
	}
}
