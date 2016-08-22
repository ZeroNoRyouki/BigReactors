package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.IPowerGenerator;
import erogenousbeef.bigreactors.common.multiblock.IPowerProvider;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

abstract class PowerTapHandler implements IPowerProvider {

    public PowerTapHandler(RectangularMultiblockTileEntityBase part) {

        assert null != part;
        this._part = part;
    }

    public abstract void checkForConnections(IBlockAccess world, BlockPos position);

    protected IPowerGenerator getPowerGenerator(EnumFacing facing) {

        if (!this._part.isConnected() || (null != facing && !this._part.getOutwardsDir().isSet(facing)))
            return null;

        MultiblockControllerBase controller = this._part.getMultiblockController();

        return controller instanceof IPowerGenerator && controller.isAssembled() ? (IPowerGenerator)controller : null;
    }

    protected RectangularMultiblockTileEntityBase _part;
}
