package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockReactorControlRod extends BlockMultiblockDevice {

    public BlockReactorControlRod(String blockName) {

        super(PartType.ReactorControlRod, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityReactorControlRod();
    }

    @Override
    public void registerRecipes() {

        if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
            GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "CGC", "GRG", "CUC",
                'G', "ingotGraphite", 'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1),
                'R', Items.REDSTONE, 'U', BigReactors.CONFIG.recipeYelloriumIngotName));

        if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
            GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "CGC", "GRG", "CUC",
                'G', "ingotGraphite", 'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1),
                'R', Items.REDSTONE, 'U', BigReactors.CONFIG.recipeYelloriumIngotName));
    }
}
