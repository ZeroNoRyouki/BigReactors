package erogenousbeef.bigreactors.init.flattening;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class MissingRegistryEntryHandler<T extends IForgeRegistryEntry<T>> {

    static <T extends IForgeRegistryEntry<T>> MissingRegistryEntryHandler<T> remap(
            @Nonnull final ResourceLocation key, @Nonnull final T replacement) {
        return new MissingRegistryEntryHandler<>(key, RegistryEvent.MissingMappings.Action.REMAP, replacement);
    }

    static <T extends IForgeRegistryEntry<T>> MissingRegistryEntryHandler<T> ignore(
            @Nonnull final ResourceLocation key) {
        return new MissingRegistryEntryHandler<>(key, RegistryEvent.MissingMappings.Action.IGNORE, null);
    }

    static <T extends IForgeRegistryEntry<T>> MissingRegistryEntryHandler<T> warn(
            @Nonnull final ResourceLocation key) {
        return new MissingRegistryEntryHandler<>(key, RegistryEvent.MissingMappings.Action.WARN, null);
    }

    static <T extends IForgeRegistryEntry<T>> MissingRegistryEntryHandler<T> fail(
            @Nonnull final ResourceLocation key) {
        return new MissingRegistryEntryHandler<>(key, RegistryEvent.MissingMappings.Action.FAIL, null);
    }

    @Nonnull
    ResourceLocation getKey() {
        return this._key;
    }

    @Nonnull
    RegistryEvent.MissingMappings.Action getRemapAction() {
        return this._remapAction;
    }

    @Nonnull
    T getReplacement() {
        return this._replacement;
    }

    int getOldId() {
        return this._oldId;
    }

    /**
     * If the mapping key is correct, apply the requested remap action and update the entry old Id
     * @param mapping   the missing mapping
     */
    void remap(@Nonnull final RegistryEvent.MissingMappings.Mapping<T> mapping) {

        if (this.getKey().equals(mapping.key)) {

            this._oldId = mapping.id;

            switch (this.getRemapAction()) {

                case FAIL:
                    mapping.fail();
                    break;

                case IGNORE:
                    mapping.ignore();
                    break;

                case REMAP:
                    mapping.remap(this.getReplacement());
                    break;

                case WARN:
                    mapping.warn();
                    break;
            }
        }
    }

    //region internals

    protected MissingRegistryEntryHandler(@Nonnull final ResourceLocation key,
                                          @Nonnull final RegistryEvent.MissingMappings.Action action,
                                          @Nullable final T replacement) {

        this._key = key;
        this._remapAction = action;
        this._replacement = replacement;
        this._oldId = -1;
    }

    private final ResourceLocation _key;
    private final RegistryEvent.MissingMappings.Action _remapAction;
    private final T _replacement;
    private int _oldId;
}
