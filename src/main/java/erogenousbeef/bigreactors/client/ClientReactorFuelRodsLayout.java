package erogenousbeef.bigreactors.client;

import com.google.common.collect.Maps;
import erogenousbeef.bigreactors.api.data.ReactantData;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.helpers.FuelContainer;
import erogenousbeef.bigreactors.common.multiblock.helpers.ReactorFuelRodsLayout;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import it.zerono.mods.zerocore.lib.client.render.CachedRender;
import it.zerono.mods.zerocore.lib.math.Colour;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class ClientReactorFuelRodsLayout extends ReactorFuelRodsLayout {

    public ClientReactorFuelRodsLayout(@Nonnull final MultiblockReactor reactor) {

        super(reactor);

        this._reactantsChanged = false;
        this._totalFuelQuota = this._totalWasteQuota = 0;
        this._wasteColor = this._fuelColor = Colour.WHITE;
        this._rodsFuelData = new FuelData[this.getRodLength()];

        for (int i = 0; i < this._rodsFuelData.length; ++i) {
            this._rodsFuelData[i] = new FuelData();
        }

        this._renderCache = Maps.newHashMapWithExpectedSize(4);
    }

    @Nullable
    public FuelData getFuelData(final int fuelRodIndex) {
        return fuelRodIndex >= 0 && fuelRodIndex < this._rodsFuelData.length ? this._rodsFuelData[fuelRodIndex] : null;
    }

    public boolean isFuelDataChanged(final int fuelRodIndex) {

        final FuelData data = this.getFuelData(fuelRodIndex);

        return (null != data && data.isChanged()) || this._reactantsChanged;
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    public Colour getFuelColor() { return this._fuelColor; }

    @SideOnly(Side.CLIENT)
    @Nonnull
    public Colour getWasteColor() { return this._wasteColor; }

    public float getFuelQuota() {
        return this._totalFuelQuota;
    }

    public float getWasteQuota() {
        return this._totalWasteQuota;
    }

    public float getFuelRodFuelQuota() {
        return this._totalFuelQuota / this.getRodLength();
    }

    public float getFuelRodWasteQuota() {
        return this._totalWasteQuota / this.getRodLength();
    }

    @Override
    public void updateFuelData(@Nonnull final FuelContainer fuelData, final int fuelRodsTotalCount) {

        // fuel/waste colors

        Colour oldFuelColor = this._fuelColor;
        Colour oldWasteColor = this._wasteColor;

        this._fuelColor = getReactantColor(fuelData.getFuelType(), ReactantData.DEFAULT_FLUID_COLOR_FUEL);
        this._wasteColor = getReactantColor(fuelData.getWasteType(), ReactantData.DEFAULT_FLUID_COLOR_WASTE);
        this._reactantsChanged = (!this._fuelColor.equals(oldFuelColor)) || (!this._wasteColor.equals(oldWasteColor));

        if (this._reactantsChanged) {
            this._renderCache.clear();
        }

        // fuel/waste quota for each fuel rod

        final int fuelRodsCount = this.getRodLength();

        this._totalFuelQuota = fuelData.getFuelAmount() / fuelRodsTotalCount * fuelRodsCount;
        this._totalWasteQuota = fuelData.getWasteAmount() / fuelRodsTotalCount * fuelRodsCount;

        // split fuel/waste between fuel rods

        if (EnumFacing.Axis.Y == this.getAxis()) {

            // vertical column, fluids pool to the bottom (waste first)

            final float rodCapacity = MultiblockReactor.FuelCapacityPerFuelRod;
            float remainingWaste = this._totalWasteQuota;
            float remainingFuel = this._totalFuelQuota;
            float fuelAmount, wasteAmount;

            for (int i = 0; i < fuelRodsCount; ++i) {

                if (remainingWaste > 0.0f) {

                    wasteAmount = Math.min(remainingWaste, rodCapacity);
                    remainingWaste -= wasteAmount;

                } else {

                    wasteAmount = 0.0f;
                }

                if (remainingFuel > 0.0f) {

                    fuelAmount = Math.min(remainingFuel, rodCapacity - wasteAmount);
                    remainingFuel -= fuelAmount;

                } else {

                    fuelAmount = 0.0f;
                }

                if (this._rodsFuelData[i].update(fuelAmount, wasteAmount)) {

                    if (this._renderCache.containsKey(this._rodsFuelData[i].getFluidStatus())) {
                        this._renderCache.remove(this._rodsFuelData[i].getFluidStatus());
                    }
                }
            }

        } else {

            // horizontal column, fluids distribute equally

            final float fuelPerRod = this._totalFuelQuota / fuelRodsCount;
            final float wastePerRod = this._totalWasteQuota / fuelRodsCount;

            for (int i = 0; i < fuelRodsCount; ++i) {

                if (this._rodsFuelData[i].update(fuelPerRod, wastePerRod)) {

                    if (this._renderCache.containsKey(this._rodsFuelData[i].getFluidStatus())) {
                        this._renderCache.remove(this._rodsFuelData[i].getFluidStatus());
                    }
                }
            }
        }
    }

    @Override
    public void updateFuelRodsOcclusion(@Nonnull final Set<TileEntityReactorFuelRod> fuelRods) {

        final EnumFacing[] directions = this.getRadiateDirections();
        boolean occluded;

        for (final TileEntityReactorFuelRod fuelRod : fuelRods) {

            final BlockPos rodPosition = fuelRod.getWorldPosition();

            occluded = true;

            for (int i = 0; i < directions.length; ++i) {

                final BlockPos checkPosition = rodPosition.offset(directions[i]);

                if (fuelRod.getWorld().isAirBlock(checkPosition) ||
                    fuelRod.getWorld().getBlockState(checkPosition).getBlock().getBlockLayer() != BlockRenderLayer.SOLID) {

                    occluded = false;
                    break;
                }
            }

            fuelRod.setOccluded(occluded);
        }
    }

    @Nullable
    public CachedRender getCachedRender(@Nonnull final FuelRodFluidStatus rodStatus) {
        return this._renderCache.get(rodStatus);
    }

    public void setChachedRender(@Nonnull final FuelRodFluidStatus rodStatus, @Nonnull final CachedRender render) {
        this._renderCache.put(rodStatus, render);
    }

    private static Colour getReactantColor(@Nullable final String reactantName, final int defaultColor) {

        if (null == reactantName || reactantName.isEmpty()) {
            return Colour.WHITE;
        }

        final ReactantData reactant = Reactants.getReactant(reactantName);
        final int color = null == reactant ? defaultColor : reactant.getColor();

        return Colour.fromARGB(color | 0xFF000000);
    }

    public enum FuelRodFluidStatus {
        Empty,
        FuelOnly,
        WasteOnly,
        Mixed,
        FullFuelOnly,
        FullWasteOnly
    }

    public class FuelData {

        FuelData() {

            this._wasteAmount = this._fuelAmount = this._fuelHeight = this._wasteHeight = 0.0f;
            this._changed = false;
            this._fluidStatus = FuelRodFluidStatus.Empty;
        }

        public float getFuelAmount() {
            return this._fuelAmount;
        }

        public float getWasteAmount() {
            return this._wasteAmount;
        }

        public float getFuelHeight() {
            return this._fuelHeight;
        }

        public float getWasteHeight() {
            return this._wasteHeight;
        }

        public boolean isChanged() {
            return this._changed;
        }

        @Nonnull
        public FuelRodFluidStatus getFluidStatus() {
            return this._fluidStatus;
        }

        private boolean update(float fuelAmount, float wasteAmount) {

            this._changed = (this._fuelAmount != fuelAmount) || (this._wasteAmount != wasteAmount);
            this._fuelAmount = fuelAmount;
            this._wasteAmount = wasteAmount;
            this._fuelHeight = this._fuelAmount / MultiblockReactor.FuelCapacityPerFuelRod;
            this._wasteHeight = this._wasteAmount / MultiblockReactor.FuelCapacityPerFuelRod;

            if (0.0 == this._fuelAmount && 0.0 == this._wasteAmount) {

                this._fluidStatus = FuelRodFluidStatus.Empty;

            } else if (MultiblockReactor.FuelCapacityPerFuelRod == this._fuelAmount) {

                this._fluidStatus = FuelRodFluidStatus.FullFuelOnly;

            } else if (MultiblockReactor.FuelCapacityPerFuelRod == this._wasteAmount) {

                this._fluidStatus = FuelRodFluidStatus.FullWasteOnly;

            } else if (0.0 < this._fuelAmount && 0.0 == this._wasteAmount) {

                this._fluidStatus = FuelRodFluidStatus.FuelOnly;

            } else if (0.0 < this._wasteAmount && 0.0 == this._fuelAmount) {

                this._fluidStatus = FuelRodFluidStatus.WasteOnly;

            } else {

                this._fluidStatus = FuelRodFluidStatus.Mixed;
            }

            return this._changed;
        }

        private float _fuelAmount;
        private float _wasteAmount;
        private float _fuelHeight;
        private float _wasteHeight;
        private boolean _changed;
        private FuelRodFluidStatus _fluidStatus;
    }

    private FuelData[] _rodsFuelData;
    private float _totalFuelQuota;
    private float _totalWasteQuota;
    private Colour _fuelColor;
    private Colour _wasteColor;
    private boolean _reactantsChanged;

    private Map<FuelRodFluidStatus, CachedRender> _renderCache;
}