package erogenousbeef.bigreactors.init.flattening;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.lib.init.IGameObjectMapper;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.event.RegistryEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class ItemReplacer implements IFixableData, IGameObjectMapper<Item> {

    @FunctionalInterface
    public interface IConverter {
        NBTTagCompound convert(@Nonnull final NBTTagCompound itemNBT);
    }

    public ItemReplacer(int dataVersion) {

        this._dataVersion = dataVersion;
        this._replacements = Maps.newHashMap();
    }

    public void addReplacement(@Nonnull final ResourceLocation oldItemId, @Nonnull final IConverter converter) {
        this._replacements.put(oldItemId.toString(), new MissingItemSingleConverter(oldItemId, converter));
    }

    public void addReplacement(@Nonnull final ResourceLocation oldItemId, final int oldMetadata,
                               @Nonnull final Item newItem) {
        this.addReplacement(oldItemId, oldMetadata, newItem, 0);
    }

    public void addReplacement(@Nonnull final ResourceLocation oldItemId, final int oldMetadata,
                               @Nonnull final Item newItem, final int newMetadata) {

        final MissingRegistryEntryHandler<Item> entry = this._replacements.computeIfAbsent(oldItemId.toString(),
                k -> new MissingItemMultiConverter(oldItemId));

        if (entry instanceof MissingItemMultiConverter) {

            ((MissingItemMultiConverter) entry).addVariant((short)oldMetadata, itemNBT -> {

                if (itemNBT.hasKey(ITEM_TAG_DAMAGE) && itemNBT.getShort(ITEM_TAG_DAMAGE) == oldMetadata) {

                    itemNBT.setString(ITEM_TAG_ID, newItem.getRegistryName().toString());
                    itemNBT.setShort(ITEM_TAG_DAMAGE, (short)newMetadata);
                }

                return itemNBT;
            });
        }
    }

    //region IGameObjectMapper

    /**
     * Link the currently registred objects to this remapper
     * This method is called after the objects are registered in the game
     *
     * @param map the registered objects
     */
    @Override
    public void linkObjectsMap(@Nonnull ImmutableMap<String, Item> map) {
    }

    /**
     * Process a missing mapping
     * The mapping will be sent to all registered IGameObjectRemappers for the particular type of object until one
     * of them remap the object
     *
     * @param mapping the object to remap
     */
    @Override
    public void remap(@Nonnull RegistryEvent.MissingMappings.Mapping<Item> mapping) {
        this._replacements.values().forEach(handler -> handler.remap(mapping));
    }

    //region IFixableData

    @Override
    public int getFixVersion() {
        return this._dataVersion;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {

        if (null != compound && compound.hasKey(ITEM_TAG_ID)) {

            final MissingRegistryEntryHandler<Item> entry = this._replacements.get(compound.getString(ITEM_TAG_ID));

            if (entry instanceof IConverter) {
                return ((IConverter)entry).convert(compound);
            }
        }

        return compound;
    }

    //region internals

    private static class MissingItemSingleConverter extends MissingRegistryEntryHandler<Item> implements IConverter {

        MissingItemSingleConverter(@Nonnull final ResourceLocation oldObjectKey, @Nonnull final IConverter converter) {

            super(oldObjectKey, RegistryEvent.MissingMappings.Action.IGNORE, null);
            this._converter = converter;
        }

        //region IConverter

        @Override
        public NBTTagCompound convert(@Nonnull NBTTagCompound itemNBT) {
            return this._converter.convert(itemNBT);
        }

        //region internals

        private final IConverter _converter;
    }

    private static class MissingItemMultiConverter extends MissingRegistryEntryHandler<Item> implements IConverter {

        MissingItemMultiConverter(@Nonnull final ResourceLocation oldObjectKey) {

            super(oldObjectKey, RegistryEvent.MissingMappings.Action.IGNORE, null);
            this._converters = Maps.newHashMap();
        }

        void addVariant(final short oldMetadata, @Nonnull final IConverter converter) {
            this._converters.put(oldMetadata, converter);
        }

        //region IConverter

        @Override
        public NBTTagCompound convert(@Nonnull NBTTagCompound itemNBT) {

            if (itemNBT.hasKey(ITEM_TAG_DAMAGE)) {

                final IConverter converter = this._converters.get(itemNBT.getShort(ITEM_TAG_DAMAGE));

                if (null != converter) {
                    return converter.convert(itemNBT);
                }
            }

            return itemNBT;
        }

        //region internals

        private final Map<Short, IConverter> _converters;
    }

    private static final String ITEM_TAG_ID = "id";
    private static final String ITEM_TAG_DAMAGE = "Damage";

    private final int _dataVersion;
    private Map<String, MissingRegistryEntryHandler<Item>> _replacements;
}
