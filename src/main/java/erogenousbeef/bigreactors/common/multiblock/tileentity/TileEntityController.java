package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.client.gui.GuiReactorStatus;
import erogenousbeef.bigreactors.gui.container.ContainerReactorController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;

public class TileEntityController extends TileEntityReactorPart {

    public TileEntityController() {
    }

    @Override
    public boolean canOpenGui(World world, BlockPos posistion, IBlockState state) {

        MultiblockControllerBase controller = this.getMultiblockController();

        return null != controller && controller.isAssembled();
    }

    @Override
    public Object getServerGuiElement(int guiId, EntityPlayer player) {

        return this.isConnected() ? new ContainerReactorController(this, player) : null;
    }

    @Override
    public Object getClientGuiElement(int guiId, EntityPlayer player) {

        return this.isConnected() ? new GuiReactorStatus(new ContainerReactorController(this, player), this) : null;
    }
}
