package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.IPowerProvider;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPowerTapRedstoneFlux;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPowerTapTesla;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePowerTapRedstoneFlux;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePowerTapTesla;
import erogenousbeef.bigreactors.init.BrBlocks;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockMultiblockPowerTap extends BlockMultiblockDevice {

    public BlockMultiblockPowerTap(PartType type, String blockName, PowerSystem powerSystem) {

        super(type, blockName);
        this._powerSystem = powerSystem;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        final boolean isRF = PowerSystem.RedstoneFlux == this._powerSystem;

        switch (this._type) {

            case ReactorPowerTap:
                return isRF ? new TileEntityReactorPowerTapRedstoneFlux() : new TileEntityReactorPowerTapTesla();

            case TurbinePowerPort:
                return isRF ? new TileEntityTurbinePowerTapRedstoneFlux() : new TileEntityTurbinePowerTapTesla();

            default:
                throw new IllegalArgumentException("Invalid part type");
        }
    }

    @Override
    public void registerRecipes() {

        if (PartType.ReactorPowerTap == this._type) {

            if (!BigReactors.CONFIG.enableReactorPowerTapRecipe)
                return;

            if (PowerSystem.RedstoneFlux == this._powerSystem) {

                if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
                    GameRegistry.addRecipe(this.createItemStack(PartTier.Legacy, 1), "CRC", "R R", "CRC",
                            'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'R', Items.REDSTONE);

                if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
                    GameRegistry.addRecipe(this.createItemStack(PartTier.Basic, 1), "CRC", "R R", "CRC",
                            'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'R', Items.REDSTONE);

            } else if (PowerSystem.Tesla == this._powerSystem) {

                ItemStack lapis = new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage());

                if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
                    GameRegistry.addRecipe(this.createItemStack(PartTier.Legacy, 1), "CRC", "R R", "CRC",
                            'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'R', lapis);

                if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
                    GameRegistry.addRecipe(this.createItemStack(PartTier.Basic, 1), "CRC", "R R", "CRC",
                            'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'R', lapis);
            }

        } else if (PartType.TurbinePowerPort == this._type) {

            if (PowerSystem.RedstoneFlux == this._powerSystem) {

                if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
                    GameRegistry.addRecipe(this.createItemStack(PartTier.Legacy, 1), "HRH", "R R", "HRH",
                        'H', BrBlocks.turbineHousing.createItemStack(PartTier.Legacy, 1), 'R', Items.REDSTONE);

                if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
                    GameRegistry.addRecipe(this.createItemStack(PartTier.Basic, 1), "HRH", "R R", "HRH",
                        'H', BrBlocks.turbineHousing.createItemStack(PartTier.Basic, 1), 'R', Items.REDSTONE);

            } else if (PowerSystem.Tesla == this._powerSystem) {

                ItemStack lapis = new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage());

                if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
                    GameRegistry.addRecipe(this.createItemStack(PartTier.Legacy, 1), "HRH", "R R", "HRH",
                        'H', BrBlocks.turbineHousing.createItemStack(PartTier.Legacy, 1), 'R', lapis);

                if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
                    GameRegistry.addRecipe(this.createItemStack(PartTier.Basic, 1), "HRH", "R R", "HRH",
                        'H', BrBlocks.turbineHousing.createItemStack(PartTier.Basic, 1), 'R', lapis);
            }
        }
    }

    /**
     * Called when a tile entity on a side of this block changes is created or is destroyed.
     * @param world The world
     * @param position Block position in world
     * @param neighbor Block position of neighbor
     */
    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {

        TileEntity te = world.getTileEntity(position);

        // Signal power taps and other ports when their neighbors change, etc.
        if (te instanceof INeighborUpdatableEntity) {
            ((INeighborUpdatableEntity)te).onNeighborTileChange(world, position, neighbor);
        }
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.POWERTAPSTATE);
    }

    @Override
    protected IBlockState buildDefaultState(IBlockState state) {
        return super.buildDefaultState(state).withProperty(Properties.POWERTAPSTATE, PowerTapState.Disconnected);
    }

    @Override
    protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position, MultiblockTileEntityBase part) {

        boolean connected = (part instanceof IPowerProvider) && ((IPowerProvider)part).isProviderConnected();

        return super.buildActualState(state, world, position, part).withProperty(Properties.POWERTAPSTATE,
                connected ? PowerTapState.Connected : PowerTapState.Disconnected);
    }

    private PowerSystem _powerSystem;
}
