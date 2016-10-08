package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePart;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.api.multiblock.rectangular.PartPosition;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockMultiblockCasing extends BlockTieredPart {

    public BlockMultiblockCasing(PartType type, String blockName) {
        super(type, blockName, Material.IRON);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        switch (this._type) {

            case ReactorCasing:
                return new TileEntityReactorPart();

            case TurbineHousing:
                return new TileEntityTurbinePart();

            default:
                throw new IllegalArgumentException("Unrecognized part");
        }
    }

    @Override
    public void registerRecipes() {

        if (PartType.ReactorCasing == this._type) {

            if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 4), "IGI", "GUG", "IGI",
                        'I', "ingotIron", 'G', "ingotGraphite", 'U', BrItems.reactorCasingCores.createItemStack(PartTier.Legacy, 1)));

            if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 4), "IGI", "GUG", "IGI",
                    'I', "ingotSteel", 'G', "ingotGraphite", 'U', BrItems.reactorCasingCores.createItemStack(PartTier.Basic, 1)));

        } else if (PartType.TurbineHousing == this._type) {

            if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 4), "IGI", "GUG", "IGI",
                        'I', "ingotIron", 'G', "ingotGraphite", 'U', BrItems.turbineHousingCores.createItemStack(PartTier.Legacy, 1)));

            if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 4), "IGI", "GUG", "IGI",
                        'I', "ingotSteel", 'G', "ingotGraphite", 'U', BrItems.turbineHousingCores.createItemStack(PartTier.Basic, 1)));
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {

        TileEntity te = world.getTileEntity(position);
        CasingType type = CasingType.Single;

        if (te instanceof RectangularMultiblockTileEntityBase) {

            RectangularMultiblockTileEntityBase mbTile = (RectangularMultiblockTileEntityBase)te;

            if (mbTile.isConnected() && mbTile.getMultiblockController().isAssembled())
                type = CasingType.from(mbTile.getPartPosition());
        }

        return state.withProperty(Properties.CASINGTYPE, type);
    }

    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.CASINGTYPE);
    }

    @Override
    protected IBlockState buildDefaultState(IBlockState state) {

        return super.buildDefaultState(state).withProperty(Properties.CASINGTYPE, CasingType.Single);
    }

    public enum CasingType implements IStringSerializable {

        Single,
        Wall,
        FrameEW,
        FrameSN,
        FrameUD,
        Corner;

        CasingType() {

            this._name = this.name().toLowerCase();
        }

        public static CasingType from(PartPosition position) {

            if (position.isFace())
                return CasingType.Wall;

            switch (position) {

                case FrameCorner:
                    return CasingType.Corner;

                case FrameEastWest:
                    return CasingType.FrameEW;

                case FrameSouthNorth:
                    return CasingType.FrameSN;

                case FrameUpDown:
                    return CasingType.FrameUD;

                default:
                    return CasingType.Single;
            }
        }

        @Override
        public String toString() {

            return this._name;
        }

        @Override
        public String getName() {

            return this._name;
        }

        private final String _name;
    }
}
