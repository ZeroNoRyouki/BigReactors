package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.client.gui.GuiTurbineController;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.gui.container.ContainerSlotless;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityTurbineController extends TileEntityTurbinePart {

    @Override
    public boolean canOpenGui(World world, BlockPos posistion, IBlockState state) {

        MultiblockTurbine turbine = this.getTurbine();

        return null != turbine && turbine.isAssembled();
    }

    @Override
    public Object getServerGuiElement(int guiId, EntityPlayer player) {
        return this.isConnected() ? new ContainerSlotless(getTurbine(), player) : null;
    }

    @Override
    public Object getClientGuiElement(int guiId, EntityPlayer player) {
        return this.isConnected() ? new GuiTurbineController((Container)this.getServerGuiElement(guiId, player), this) : null;
    }

    @Override
    public void onMachineActivated() {
        // Re-render controller as active state has changed
        if (WorldHelper.calledByLogicalClient(this.worldObj))
            WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
    }

    @Override
    public void onMachineDeactivated() {
        // Re-render controller as active state has changed
        if (WorldHelper.calledByLogicalClient(this.worldObj))
            WorldHelper.notifyBlockUpdate(this.worldObj, this.getPos(), null, null);
    }
}
