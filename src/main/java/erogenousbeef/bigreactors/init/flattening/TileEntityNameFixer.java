package erogenousbeef.bigreactors.init.flattening;

import com.google.common.collect.Maps;
import erogenousbeef.bigreactors.common.BigReactors;
import joptsimple.internal.Strings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;

import javax.annotation.Nonnull;
import java.util.Map;

public class TileEntityNameFixer implements IFixableData {

    public TileEntityNameFixer(final int dataVersion, @Nonnull final String filterPrefix) {

        this._dataVersion = dataVersion;
        this._filterPrefix = filterPrefix;
        this._remappings = Maps.newHashMap();
    }

    public void addReplacement(@Nonnull final String oldName, @Nonnull final ResourceLocation newId) {
        this._remappings.put(oldName, newId.toString());
    }

    //region IFixableData

    @Override
    public int getFixVersion() {
        return this._dataVersion;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {

        final String teName = compound.getString("id");

        if (teName.startsWith(this._filterPrefix)) {

            String newName = this._remappings.get(teName);

            if (!Strings.isNullOrEmpty(newName)) {

                compound.setString("id", newName);
                BigReactors.getLogger().debug("Remapped old TileEntity ID '{}' with '{}'", teName, newName);

            } else {
                BigReactors.getLogger().debug("No remapping found for TileEntity ID '{}'", teName);
            }
        }

        return compound;
    }

    //region internals

    private final int _dataVersion;
    private final String _filterPrefix;
    private final Map<String, String> _remappings;
}
