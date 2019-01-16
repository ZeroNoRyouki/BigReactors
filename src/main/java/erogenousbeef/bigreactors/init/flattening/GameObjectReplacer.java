package erogenousbeef.bigreactors.init.flattening;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import it.zerono.mods.zerocore.lib.init.IGameObjectMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.List;

public class GameObjectReplacer<T extends IForgeRegistryEntry<T>> implements IGameObjectMapper<T> {
    
    public GameObjectReplacer() {
        this._remappers = Lists.newArrayList();
    }

    public void addReplacement(@Nonnull final ResourceLocation oldObjectKey, @Nonnull final T replacement) {
        this.addRemapper(new RegistryEntryRemapper<>(oldObjectKey, replacement));
    }

    //region IGameObjectMapper

    @Override
    public void linkObjectsMap(@Nonnull final ImmutableMap<String, T> map) {
    }

    @Override
    public void remap(@Nonnull final RegistryEvent.MissingMappings.Mapping<T> mapping) {

        for (final RegistryEntryRemapper<T> remapper : this.getRemappers()) {
            remapper.remap(mapping);
        }
    }

    //region internals

    protected void addRemapper(RegistryEntryRemapper<T> remapper) {
        this._remappers.add(remapper);
    }
    
    @Nonnull
    protected List<RegistryEntryRemapper<T>> getRemappers() {
        return this._remappers;
    }
    
    private final List<RegistryEntryRemapper<T>> _remappers;
}