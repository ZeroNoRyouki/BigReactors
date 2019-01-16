package erogenousbeef.bigreactors.init.flattening;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.event.RegistryEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BlockReplacer extends GameObjectReplacer<Block> implements IFixableData {

    public BlockReplacer(int dataVersion) {
        this._dataVersion = dataVersion;
    }

    /**
     * Add a replacement for the block identified by provided key
     * @param oldObjectKey          the registration key of the block to replace
     * @param replacement           the block that should replace the old one
     * @param blockStateGenerator   a function to generate the blockstate for the new block. If null, the default blockstate of the replacement block will be used
     * @param teDataConverter       an optional function to modify the data for the old block tile entity. Return false to remove it instead
     */
    public void addReplacement(@Nonnull final ResourceLocation oldObjectKey, final int oldMetadata,
                               @Nonnull final Block replacement,
                               @Nullable final BiFunction<Block, NBTTagCompound, IBlockState> blockStateGenerator,
                               @Nullable final Function<NBTTagCompound, Boolean> teDataConverter) {
        this.addRemapper(new BlockEntryRemapper(oldObjectKey, oldMetadata, replacement, blockStateGenerator, teDataConverter));
    }

    //region GameObjectReplacer

    @Override
    public void remap(@Nonnull final RegistryEvent.MissingMappings.Mapping<Block> mapping) {

        this.invalidateCache();
        super.remap(mapping);
    }

    //region IFixableData

    @Override
    public int getFixVersion() {
        return this._dataVersion;
    }

    @Override
    public NBTTagCompound fixTagCompound(final NBTTagCompound compound) {
        //TODO fix all the tags!
        return null;
    }

    //region internals

    @Nonnull
    protected List<BlockEntryRemapper> getBlockRemappers(final int oldBlockId) {

        if (null == this._remappersByBlockId) {
            this.rebuildCache();
        }

        return this._remappersByBlockId.getOrDefault(oldBlockId, Collections.emptyList());
    }

    @Nullable
    protected BlockEntryRemapper getRemapper(final int oldBlockId, final int oldMetadata) {

        for (final BlockEntryRemapper remapper : this.getBlockRemappers(oldBlockId)) {

            if (remapper.getOldMetadata() == oldMetadata) {
                return remapper;
            }
        }

        return null;
    }

    private void invalidateCache() {
        this._remappersByBlockId = null;
    }

    private void rebuildCache() {

        final List<RegistryEntryRemapper<Block>> entryRemappers = this.getRemappers();

        this._remappersByBlockId = Maps.newHashMap();

        for (RegistryEntryRemapper<Block> entryRemapper : entryRemappers) {

            if (entryRemapper instanceof BlockEntryRemapper) {
                this._remappersByBlockId.computeIfAbsent(entryRemapper.getOldId(), k -> Lists.newArrayList())
                        .add((BlockEntryRemapper)entryRemapper);
            }
        }
    }

    private final int _dataVersion;
    private Map<Integer, List<BlockEntryRemapper>> _remappersByBlockId;
}