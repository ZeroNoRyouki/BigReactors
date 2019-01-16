package erogenousbeef.bigreactors.init.flattening;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

class BlockEntryRemapper extends RegistryEntryRemapper<Block> {

    public BlockEntryRemapper(@Nonnull final ResourceLocation oldObjectKey, final int oldMetadata,
                              @Nonnull final Block replacement,
                              @Nullable final BiFunction<Block, NBTTagCompound, IBlockState> blockStateGenerator,
                              @Nullable final Function<NBTTagCompound, Boolean> teDataConverter) {

        super(oldObjectKey, replacement);
        this._oldMetadata = oldMetadata;
        this._blockStateGenerator = blockStateGenerator;
        this._teDataConverter = teDataConverter;
    }

    public int getOldMetadata() {
        return this._oldMetadata;
    }

    public @Nonnull IBlockState getNewBlockState(@Nullable final NBTTagCompound tileEntityData) {

        final IBlockState state = null != this._blockStateGenerator ?
                this._blockStateGenerator.apply(this.getReplacement(), tileEntityData) : null;

        return null != state ? state : this.getReplacement().getDefaultState();
    }

    public boolean convertTileEntityData(@Nullable final NBTTagCompound tileEntityData) {
        return null != this._teDataConverter ? this._teDataConverter.apply(tileEntityData) : true;
    }

    final int _oldMetadata;
    final BiFunction<Block, NBTTagCompound, IBlockState> _blockStateGenerator;
    final Function<NBTTagCompound, Boolean> _teDataConverter;
}