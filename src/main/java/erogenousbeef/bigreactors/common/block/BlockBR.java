package erogenousbeef.bigreactors.common.block;

import it.zerono.mods.zerocore.lib.IGameObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockBR extends Block implements IGameObject {

    public BlockBR(String blockName, Material material) {

        super(material);
        this.setDefaultState(this.buildDefaultState(this.blockState.getBaseState()));
        this.setRegistryName(blockName);
        this.setUnlocalizedName(this.getRegistryName().toString());
        this.setHardness(2.0f);
    }

    @Override
    public void onPostRegister() {
        GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPostClientRegister() {
    }

    @Override
    public void registerOreDictionaryEntries() {
    }

    @Override
    public void registerRecipes() {
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    public ItemStack createItemStack() {

        return this.createItemStack(1, 0);
    }

    public ItemStack createItemStack(int amount) {

        return this.createItemStack(amount, 0);
    }

    public ItemStack createItemStack(int amount, int meta) {

        return new ItemStack(this, amount, meta);
    }

    @Override
    protected BlockStateContainer createBlockState() {

        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);

        this.buildBlockState(builder);
        return builder.build();
    }

    protected void buildBlockState(BlockStateContainer.Builder builder) {
    }

    protected IBlockState buildDefaultState(IBlockState state) {
        return state;
    }
}
