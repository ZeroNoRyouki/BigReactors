package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.CommonProxy;
import erogenousbeef.bigreactors.common.block.BlockBR;
import erogenousbeef.bigreactors.common.block.BlockBRGenericFluid;
import erogenousbeef.bigreactors.common.item.ItemBase;
import it.zerono.mods.zerocore.lib.IGameObject;
import it.zerono.mods.zerocore.lib.IModInitializationHandler;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InitHandler implements IModInitializationHandler {

    public static final InitHandler INSTANCE;

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

        BrFluids.initialize();
        BrItems.initialize();
        BrBlocks.initialize();
    }

    @Override
    public void onInit(FMLInitializationEvent event) {

        // Patch up vanilla being stupid - most mods already do this, so it's usually a no-op

        if (!OreDictionaryHelper.doesOreNameExist("blockSnow"))
            OreDictionary.registerOre("blockSnow", new ItemStack(Blocks.SNOW, 1));

        if (!OreDictionaryHelper.doesOreNameExist("blockIce"))
            OreDictionary.registerOre("blockIce", new ItemStack(Blocks.ICE, 1));

        // Register ore dict entries and recipes

        for (IGameObject obj: this._objects)
            obj.registerOreDictionaryEntries();

        for (IGameObject obj: this._objects)
            obj.registerRecipes();
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

        this._objects.clear();
        this._objects = null;
        this._proxy = null;
    }

    public void onMissinBlockMappings(RegistryEvent.MissingMappings<Block> event) {

        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getMappings())
            this._remapBlocks.remap(mapping);
    }

    public void onMissingItemMapping(RegistryEvent.MissingMappings<Item> event) {

        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings())
            this._remapItems.remap(mapping);
    }
    /*
    public void onMissingMapping(FMLMissingMappingsEvent event) {

        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {

            switch (mapping.type) {

                case ITEM:
                    this._remapItems.remap(mapping);
                    break;

                case BLOCK:
                    this._remapBlocks.remap(mapping);
                    break;
            }
        }
    }*/

    private InitHandler() {

        this._objects = new ArrayList<>();
        this._remapBlocks = new LowerCaseRemapper<>();
        this._remapItems = new LowerCaseRemapper<>();
        this._proxy = BigReactors.getProxy();
    }

    protected ItemBase register(ItemBase item) {

        this._objects.add(item);

        final ItemBase result = this._proxy.register(item);

        this.addRemapEntry(result);
        return result;
    }

    protected BlockBR register(BlockBR block) {

        this._objects.add(block);

        final BlockBR result =  this._proxy.register(block);

        this.addRemapEntry(result);
        return result;
    }

    protected BlockBRGenericFluid register(BlockBRGenericFluid block) {

        this._objects.add(block);

        final BlockBRGenericFluid result =  this._proxy.register(block);

        this.addRemapEntry(result);
        return result;
    }

    protected void register(Class<? extends TileEntity> tileEntityClass) {
        GameRegistry.registerTileEntity(tileEntityClass, BigReactors.MODID + tileEntityClass.getSimpleName());
    }

    private void addRemapEntry(final Block block) {

        this._remapBlocks.add(block);

        final Item itemBlock = Item.REGISTRY.getObject(block.getRegistryName());

        if (null != itemBlock)
            this.addRemapEntry(itemBlock);
    }

    private void addRemapEntry(final Item item) {
        this._remapItems.add(item);
    }

    private static class LowerCaseRemapper<T extends IForgeRegistryEntry<T>> {

        LowerCaseRemapper() {
            this._map = new HashMap<>();
        }

        public void add(final T entry) {
            this._map.put(entry.getRegistryName().getResourcePath(), entry);
        }

        void remap(final RegistryEvent.MissingMappings.Mapping<T> mapping) {

            String candidateName = mapping.key.getResourcePath().toLowerCase();

            if (this._map.containsKey(candidateName)) {

                T replacement = this._map.get(candidateName);

                mapping.remap(replacement);
            }
        }

        private Map<String, T> _map;
    }

    private List<IGameObject> _objects;
    private final LowerCaseRemapper<Block> _remapBlocks;
    private final LowerCaseRemapper<Item> _remapItems;
    private CommonProxy _proxy;

    static {
        INSTANCE = new InitHandler();
    }
}
