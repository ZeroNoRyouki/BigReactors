package erogenousbeef.bigreactors.init.flattening;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import it.zerono.mods.zerocore.lib.init.IGameObjectMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.List;

public class GameObjectMapper<T extends IForgeRegistryEntry<T>> implements IGameObjectMapper<T> {
    
    public GameObjectMapper() {
        this._handlers = Lists.newArrayList();
    }

    public void remap(@Nonnull final ResourceLocation key, @Nonnull final T replacement) {
        this.addHandler(MissingRegistryEntryHandler.remap(key, replacement));
    }

    public void ignore(@Nonnull final ResourceLocation key) {
        this.addHandler(MissingRegistryEntryHandler.ignore(key));
    }

    public void warn(@Nonnull final ResourceLocation key) {
        this.addHandler(MissingRegistryEntryHandler.warn(key));
    }

    public void fail(@Nonnull final ResourceLocation key) {
        this.addHandler(MissingRegistryEntryHandler.fail(key));
    }

    //region IGameObjectMapper

    @Override
    public void linkObjectsMap(@Nonnull final ImmutableMap<String, T> map) {
    }

    @Override
    public void remap(@Nonnull final RegistryEvent.MissingMappings.Mapping<T> mapping) {
        this.getHandlers().forEach(handler -> handler.remap(mapping));
    }

    //region internals

    protected void addHandler(MissingRegistryEntryHandler<T> handler) {
        this._handlers.add(handler);
    }
    
    @Nonnull
    protected List<MissingRegistryEntryHandler<T>> getHandlers() {
        return this._handlers;
    }
    
    private final List<MissingRegistryEntryHandler<T>> _handlers;
}
