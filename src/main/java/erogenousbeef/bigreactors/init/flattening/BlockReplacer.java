package erogenousbeef.bigreactors.init.flattening;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.zerono.mods.zerocore.lib.init.IGameObjectMapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.GameData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BlockReplacer implements IFixableData, IGameObjectMapper<Block> {

    public BlockReplacer(int dataVersion) {

        this._dataVersion = dataVersion;
        this._replacements = Maps.newHashMap();
    }

    /**
     * Add a replacement for the block identified by the provided key
     * @param oldBlockId    the registration Id of the block to replace
     * @param oldMetadata   the metadata of the block variant to replace
     * @param replacement   the block that should replace the old one
     */
    public void addReplacement(@Nonnull final ResourceLocation oldBlockId, final int oldMetadata,
                               @Nonnull final Block replacement) {
        this.addReplacement(oldBlockId, oldMetadata, replacement, null, null);
    }

    /**
     * Add a replacement for the block identified by the provided key
     * @param oldBlockId            the registration Id of the block to replace
     * @param oldMetadata           the metadata of the block variant to replace
     * @param replacement           the block that should replace the old one
     * @param blockStateGenerator   an optional function to generate the blockstate for the new block. If null, the default blockstate of the replacement block will be used
     * @param teDataConverter       an optional function to modify the data for the old block tile entity. Return false to remove it instead
     */
    public void addReplacement(@Nonnull final ResourceLocation oldBlockId, final int oldMetadata,
                               @Nonnull final Block replacement,
                               @Nullable final BiFunction<Block, NBTTagCompound, IBlockState> blockStateGenerator,
                               @Nullable final Function<NBTTagCompound, Boolean> teDataConverter) {

        final MissingBlockEntry entry = this._replacements.computeIfAbsent(oldBlockId,
                k -> new MissingBlockEntry(oldBlockId));

        entry.addVariant(oldMetadata, replacement, blockStateGenerator, teDataConverter);
    }

    //region IGameObjectMapper

    @Override
    public void linkObjectsMap(@Nonnull ImmutableMap<String, Block> map) {
    }

    /**
     * Process a missing mapping
     * The mapping will be sent to all registered IGameObjectRemappers for the particular type of object until one
     * of them remap the object
     *
     * @param mapping the object to remap
     */
    @Override
    public void remap(@Nonnull RegistryEvent.MissingMappings.Mapping<Block> mapping) {

        this.invalidateCache();
        this._replacements.values().forEach(handler -> handler.remap(mapping));
    }

    //region IFixableData

    @Override
    public int getFixVersion() {
        return this._dataVersion;
    }

    @Override
    public NBTTagCompound fixTagCompound(final NBTTagCompound compound) {

        if (this._replacements.isEmpty()) {
            return compound;
        }

        final ObjectIntIdentityMap<IBlockState> blockStateIdMap = GameData.getBlockStateIDMap();

        final NBTTagCompound level = compound.getCompoundTag(CHUNK_TAG_LEVEL);
        final NBTTagList sections = level.getTagList(CHUNK_TAG_SECTIONS, Constants.NBT.TAG_COMPOUND);
        final ChunkPos chunkPos = new ChunkPos(level.getInteger(CHUNK_TAG_XPOS), level.getInteger(CHUNK_TAG_ZPOS));

        final NBTTagList tileEntities = level.getTagList(CHUNK_TAG_TILEENTITIES, Constants.NBT.TAG_COMPOUND);
        final Map<BlockPos, Pair<Integer, NBTTagCompound>> tileEntityMap = buildTileEntitiesDataMap(tileEntities);
        final IntList tileEntityToRemove = new IntArrayList();

        for (int sectionIdx = 0; sectionIdx < sections.tagCount(); ++sectionIdx) {

            final NBTTagCompound section = sections.getCompoundTagAt(sectionIdx);

            final int sectionY = section.getByte(CHUNK_TAG_Y);
            final byte[] blockIds = section.getByteArray(CHUNK_TAG_BLOCKS);
            final NibbleArray metadataArray = new NibbleArray(section.getByteArray(CHUNK_TAG_DATA));
            final NibbleArray blockIdsExtension = section.hasKey(CHUNK_TAG_ADD, Constants.NBT.TAG_BYTE_ARRAY) ? new NibbleArray(section.getByteArray(CHUNK_TAG_ADD)) : new NibbleArray();
            boolean hasExtendedBlockIds = section.hasKey(CHUNK_TAG_ADD, Constants.NBT.TAG_BYTE_ARRAY);

            for (int blockIdx = 0; blockIdx < blockIds.length; ++blockIdx) {

                final int x = blockIdx & 15;
                final int y = blockIdx >> 8 & 15;
                final int z = blockIdx >> 4 & 15;
                final int blockIdExtension = blockIdsExtension.get(x, y, z);
                final int blockId = blockIdExtension << 8 | (blockIds[blockIdx] & 255);
                final int metadata = metadataArray.get(x, y, z);

                final VariantReplacement variantReplacement = this.getVariantReplacement(blockId, metadata);

                if (variantReplacement != null) {

                    final BlockPos blockPos = chunkPos.getBlock(x, sectionY << 4 | y, z);
                    final Pair<Integer, NBTTagCompound> tileEntityPair = tileEntityMap.get(blockPos);
                    final NBTTagCompound tileEntityNBT = tileEntityPair != null ? tileEntityPair.getValue() : null;
                    final IBlockState newBlockState = variantReplacement.getNewBlockState(tileEntityNBT);

                    // compute the new block Id, block Id extension and metadata from the block state's Id

                    final int blockStateID = blockStateIdMap.get(newBlockState);
                    final byte newBlockID = (byte) (blockStateID >> 4 & 255);
                    final byte newBlockIDExtension = (byte) (blockStateID >> 12 & 15);
                    final byte newMetadata = (byte) (blockStateID & 15);

                    // update the section data

                    blockIds[blockIdx] = newBlockID;
                    metadataArray.set(x, y, z, newMetadata);

                    if (newBlockIDExtension != 0) {

                        hasExtendedBlockIds = true;
                        blockIdsExtension.set(x, y, z, newBlockIDExtension);
                    }

                    // process the tile entity data

                    if (null != tileEntityNBT) {

                        if (!variantReplacement.convertTileEntityData(tileEntityNBT)) {
                            // the converter want to remove the tile entity data
                            tileEntityToRemove.add(tileEntityPair.getKey());
                        }
                    }
                }
            }

            // update the block Id and metadata in the section

            section.setByteArray(CHUNK_TAG_BLOCKS, blockIds);
            section.setByteArray(CHUNK_TAG_DATA, metadataArray.getData());

            // update the block Id extensions in the section, if present

            if (hasExtendedBlockIds) {
                section.setByteArray(CHUNK_TAG_ADD, blockIdsExtension.getData());
            }
        }

        // remove the requested TileEntities data, highest indexes first

        tileEntityToRemove.sort(Comparator.reverseOrder());

        for (final int tileEntityIdx : tileEntityToRemove) {
            tileEntities.removeTag(tileEntityIdx);
        }

        return compound;
    }

    //region internals

    private static Map<BlockPos, Pair<Integer, NBTTagCompound>> buildTileEntitiesDataMap(@Nonnull final NBTTagList tileEntities) {

        final Map<BlockPos, Pair<Integer, NBTTagCompound>> map = Maps.newHashMap();

        for (int idx = 0; idx < tileEntities.tagCount(); ++idx) {

            final NBTTagCompound nbt = tileEntities.getCompoundTagAt(idx);

            if (!nbt.hasNoTags()) {
                map.put(new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z")), Pair.of(idx, nbt));
            }
        }

        return map;
    }

    @Nullable
    protected VariantReplacement getVariantReplacement(final int oldBlockId, final int oldMetadata) {

        if (null == this._variantsByBlockId) {
            this.rebuildCache();
        }

        if (this._variantsByBlockId.containsKey(oldBlockId)) {
            return this._variantsByBlockId.get(oldBlockId)[oldMetadata];
        }

        return null;
    }

    private void invalidateCache() {
        this._variantsByBlockId = null;
    }

    private void rebuildCache() {

        this._variantsByBlockId = Maps.newHashMap();

        for (MissingBlockEntry replacement : this._replacements.values()) {
            this._variantsByBlockId.put(replacement.getOldId(), replacement.getVariants());
        }
    }

    private static class VariantReplacement {

        VariantReplacement(final int oldMetadata, @Nonnull final Block replacement,
                           @Nullable final BiFunction<Block, NBTTagCompound, IBlockState> blockStateGenerator,
                           @Nullable final Function<NBTTagCompound, Boolean> teDataConverter) {

            this._oldMetadata = oldMetadata;
            this._replacement = replacement;
            this._blockStateGenerator = blockStateGenerator;
            this._teDataConverter = teDataConverter;
        }

        public int getOldMetadata() {
            return this._oldMetadata;
        }

        public Block getReplacement() {
            return this._replacement;
        }

        public @Nonnull IBlockState getNewBlockState(@Nullable final NBTTagCompound tileEntityData) {

            final IBlockState state = null != this._blockStateGenerator ?
                    this._blockStateGenerator.apply(this.getReplacement(), tileEntityData) : null;

            return null != state ? state : this.getReplacement().getDefaultState();
        }

        /**
         *
         * @param tileEntityData
         * @return  true if the nbt tag should be kept, false if it should be removed
         */
        public boolean convertTileEntityData(@Nullable final NBTTagCompound tileEntityData) {
            return null != this._teDataConverter ? this._teDataConverter.apply(tileEntityData) : true;
        }

        //region internals

        final int _oldMetadata;
        final Block _replacement;
        final BiFunction<Block, NBTTagCompound, IBlockState> _blockStateGenerator;
        final Function<NBTTagCompound, Boolean> _teDataConverter;
    }

    private static class MissingBlockEntry extends MissingRegistryEntryHandler<Block> {

        MissingBlockEntry(@Nonnull final ResourceLocation oldObjectKey) {

            super(oldObjectKey, RegistryEvent.MissingMappings.Action.IGNORE, null);
            this._variants = new VariantReplacement[16];
        }

        void addVariant(final int oldMetadata, @Nonnull final Block replacement,
                          @Nullable final BiFunction<Block, NBTTagCompound, IBlockState> blockStateGenerator,
                          @Nullable final Function<NBTTagCompound, Boolean> teDataConverter) {
            this._variants[oldMetadata] = new VariantReplacement(oldMetadata, replacement,
                    blockStateGenerator, teDataConverter);
        }

        @Nonnull
        public VariantReplacement[] getVariants() {
            return this._variants;
        }

        //region internals

        private final VariantReplacement[] _variants;
    }

    private static final String CHUNK_TAG_LEVEL = "Level";
    private static final String CHUNK_TAG_SECTIONS = "Sections";
    private static final String CHUNK_TAG_XPOS = "xPos";
    private static final String CHUNK_TAG_ZPOS = "zPos";
    private static final String CHUNK_TAG_TILEENTITIES = "TileEntities";
    private static final String CHUNK_TAG_Y = "Y";
    private static final String CHUNK_TAG_BLOCKS = "Blocks";
    private static final String CHUNK_TAG_DATA = "Data";
    private static final String CHUNK_TAG_ADD = "Add";

    private final int _dataVersion;
    private Map<ResourceLocation, MissingBlockEntry> _replacements;
    private Map<Integer, VariantReplacement[]> _variantsByBlockId;
}
