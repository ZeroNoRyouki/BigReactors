package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

public class BlockReactorControlRod extends BlockMultiblockDevice {

    public BlockReactorControlRod(String blockName) {

        super(PartType.ReactorControlRod, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityReactorControlRod();
    }

    @Override
    public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {
        //TODO fix recipe!
        /*
        if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
            RecipeHelper.addShapedOreDictRecipe(this.createItemStack(PartTier.Legacy, 1), "CGC", "GRG", "CUC",
                'G', "ingotGraphite", 'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1),
                'R', Items.REDSTONE, 'U', BigReactors.CONFIG.recipeYelloriumIngotName);

        if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
            RecipeHelper.addShapedOreDictRecipe(this.createItemStack(PartTier.Basic, 1), "CGC", "GRG", "CUC",
                'G', "ingotGraphite", 'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1),
                'R', Items.REDSTONE, 'U', BigReactors.CONFIG.recipeYelloriumIngotName);
                */
    }
}
