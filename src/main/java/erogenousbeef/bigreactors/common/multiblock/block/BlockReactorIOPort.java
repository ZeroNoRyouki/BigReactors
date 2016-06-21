package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorAccessPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorCoolantPort;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReactorIOPort extends BlockReactorPart {

    public BlockReactorIOPort(PartType type, String blockName) {

        super(type, blockName);
        this.setDefaultState(
                this.blockState.getBaseState().withProperty(PortDirection.PORTDIRECTION, PortDirection.Inlet)
        );
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        switch (this._type) {

            case ReactorAccessPort:
                return new TileEntityReactorAccessPort();

            case ReactorCoolantPort:
                return new TileEntityReactorCoolantPort();

            default:
                throw new IllegalArgumentException("Unrecognized part");
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

        /*
        // Do toggly fiddly things for access/coolant ports
		if(!world.isRemote && (isAccessPort(metadata) || isCoolantPort(metadata))) {
			if(StaticUtils.Inventory.isPlayerHoldingWrench(player)) {
				if(te instanceof TileEntityReactorCoolantPort) {
					TileEntityReactorCoolantPort cp = (TileEntityReactorCoolantPort)te;
					cp.setInlet(!cp.isInlet(), true);
					return true;
				}
				else if(te instanceof TileEntityReactorAccessPort) {
					TileEntityReactorAccessPort cp = (TileEntityReactorAccessPort)te;
					cp.setInlet(!cp.isInlet());
					return true;
				}
			}
			else if(isCoolantPort(metadata)) {
				return false;
			}
		}
         */
        return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(PortDirection.PORTDIRECTION);
    }
}
