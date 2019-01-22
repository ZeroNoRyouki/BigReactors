package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.compat.IdReference;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorRedNetPort;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = IdReference.MODID_MINEFACTORYRELOADED)
})
public class BlockReactorRedNetPort extends BlockMultiblockDevice /*implements IRedNetOmniNode*/ {

    public BlockReactorRedNetPort(String blockName) {
        super(PartType.ReactorRednetPort, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityReactorRedNetPort();
    }

    /**
     * Register all the recipes for this object
     *
     * @param registry the recipes registry
     */
    @Override
    public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {
        //TODO fix recipe!
        /*
        if (!CompatManager.isModLoaded(IdReference.MODID_MINEFACTORYRELOADED))
            return;

        if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
            RecipeHelper.addShapedOreDictRecipe(this.createItemStack(PartTier.Legacy, 1), "CRC", "RGR", "CRC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'R', "cableRedNet", 'G', "ingotGold");

        if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
            RecipeHelper.addShapedOreDictRecipe(this.createItemStack(PartTier.Basic, 1), "CRC", "RGR", "CRC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'R', "cableRedNet", 'G', "ingotGold");
                    */
    }

    // IConnectableRedNet
    /* TODO: re-add when Minefactory Reloaded is out for 1.12
    @Override
    @Optional.Method(modid = IdReference.MODID_MINEFACTORYRELOADED)
    public RedNetConnectionType getConnectionType(World world, BlockPos position, EnumFacing enumFacing) {

        final TileEntityReactorRedNetPort te = this.getPortTile(world, position);

        return null != te ? RedNetConnectionType.CableAll : RedNetConnectionType.None;
    }

    @Override
    @Optional.Method(modid = IdReference.MODID_MINEFACTORYRELOADED)
    public int[] getOutputValues(World world, BlockPos position, EnumFacing enumFacing) {

        final TileEntityReactorRedNetPort te = this.getPortTile(world, position);

        if (null != te) {

            return te.getOutputValues();

        } else {

            int[] values = new int[16];

            for(int i = 0; i < 16; i++)
                values[i] = 0;

            return values;
        }
    }

    // Never used. we're always in "all" mode.
    @Override
    @Optional.Method(modid = IdReference.MODID_MINEFACTORYRELOADED)
    public int getOutputValue(World world, BlockPos position, EnumFacing enumFacing, int subnet) {

        final TileEntityReactorRedNetPort te = this.getPortTile(world, position);

        if (null != te)
            te.getValueForChannel(subnet);

        return 0;
    }

    @Override
    @Optional.Method(modid = IdReference.MODID_MINEFACTORYRELOADED)
    public void onInputsChanged(World world, BlockPos position, EnumFacing enumFacing, int[] inputValues) {

        final TileEntityReactorRedNetPort te = this.getPortTile(world, position);

        if (null != te)
            te.onInputValuesChanged(inputValues);
    }

    // Never used, we're always in "all" mode.
    @Override
    @Optional.Method(modid = IdReference.MODID_MINEFACTORYRELOADED)
    public void onInputChanged(World world, BlockPos position, EnumFacing enumFacing, int inputValue) {
    }
    */

    @Nullable
    private TileEntityReactorRedNetPort getPortTile(@Nonnull final World world, @Nonnull final BlockPos position) {

        TileEntity te = world.getTileEntity(position);

        return te instanceof TileEntityReactorRedNetPort ? (TileEntityReactorRedNetPort)te : null;
    }
}