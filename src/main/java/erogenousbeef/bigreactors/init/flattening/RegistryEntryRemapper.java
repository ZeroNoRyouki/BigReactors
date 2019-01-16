package erogenousbeef.bigreactors.init.flattening;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

class RegistryEntryRemapper<T extends IForgeRegistryEntry<T>> {

    public RegistryEntryRemapper(@Nonnull final ResourceLocation key, @Nonnull final T replacement) {

        this._key = key;
        this._replacement = replacement;
        this._oldId = -1;
    }

    @Nonnull
    public ResourceLocation getKey() {
        return this._key;
    }

    @Nonnull
    public T getReplacement() {
        return this._replacement;
    }

    public int getOldId() {
        return this._oldId;
    }

    /**
     * If the mapping key is correct, replace this entry in the registry
     * @param mapping   the missing mapping
     */
    public void remap(@Nonnull final RegistryEvent.MissingMappings.Mapping<T> mapping) {

        if (this.getKey().equals(mapping.key)) {

            this._oldId = mapping.id;
            mapping.remap(this.getReplacement());
        }
    }

    private final ResourceLocation _key;
    private final T _replacement;
    private int _oldId;
}