package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import zero.mods.zerocore.lib.client.ICustomModelsProvider;

import java.util.ArrayList;
import java.util.List;

public class BlockTieredPart extends BlockPart implements ICustomModelsProvider {

    public BlockTieredPart(PartType type, String blockName, Material material) {
        super(type, blockName, material);
    }

    @Override
    public void onPostRegister() {
        GameRegistry.register(new ItemBlockPartTier(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPostClientRegister() {

        Item item = Item.getItemFromBlock(this);
        ResourceLocation location = this.getRegistryName();
        IBlockState defaultState = this.getDefaultState();
        StringBuilder sb = new StringBuilder(32);
        //String mapFormat = "";
        boolean first = true;

        for (IProperty<?> prop : defaultState.getProperties().keySet()) {

            String name = prop.getName();

            if (!first)
                //mapFormat += ",";
                sb.append(',');

            if ("tier".equals(name))
                //mapFormat += "tier=%s";
                sb.append("tier=%s");
            else {
                //mapFormat += name + "=" + defaultState.getValue(prop).toString();
                sb.append(name);
                sb.append('=');
                sb.append(defaultState.getValue(prop));
            }

            first = false;
        }

        String mapFormat = sb.toString();

        for (PartTier tier : PartTier.VALUES)
            ModelLoader.setCustomModelResourceLocation(item, tier.toMeta(),
                    new ModelResourceLocation(location, String.format(mapFormat, tier.getName())));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(Properties.TIER).toMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(Properties.TIER, PartTier.fromMeta(meta));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(Properties.TIER).toMeta();
    }

    public ItemStack createItemStack(PartTier tier, int amount) {
        return new ItemStack(this, amount, tier.toMeta());
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

        int length;

        if (null == this._subBlocks) {

            PartTier[] tiers = PartTier.VALUES;

            length = tiers.length;
            this._subBlocks = new ItemStack[length];

            for (int i = 0; i < length; ++i)
                this._subBlocks[i] = new ItemStack(item, 1, tiers[i].toMeta());
        }

        length = this._subBlocks.length;

        for (int i = 0; i < length; ++i)
            list.add(this._subBlocks[i]);
    }

    @Override
    public List<Pair<Integer, ModelResourceLocation>> getMetadataToModelMappings() {

        List<Pair<Integer, ModelResourceLocation>> mappings = new ArrayList();

        IBlockState defaultState = this.getDefaultState();
        String mapFormat = "";
        boolean first = true;

        for (IProperty<?> prop : defaultState.getProperties().keySet()) {

            String name = prop.getName();

            if (!first)
                mapFormat += ",";

            if ("tier".equals(name)) {

                mapFormat += "tier=%s";

            } else {

                mapFormat += name + "=" + defaultState.getValue(prop).toString();
            }

            first = false;
        }

        ResourceLocation loc = this.getRegistryName();

        for (PartTier tier : PartTier.VALUES)
            mappings.add(new ImmutablePair(Integer.valueOf(tier.toMeta()),
                    new ModelResourceLocation(loc, String.format(mapFormat, tier.getName()))));

        return mappings;
    }

    @Override
    protected void buildBlockState(BlockStateContainer.Builder builder) {

        super.buildBlockState(builder);
        builder.add(Properties.TIER);
    }

    @Override
    protected IBlockState buildDefaultState(IBlockState state) {
        return super.buildDefaultState(state).withProperty(Properties.TIER, PartTier.Standard);
    }

    private ItemStack[] _subBlocks;
}
