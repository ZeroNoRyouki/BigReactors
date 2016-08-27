package erogenousbeef.bigreactors.common.multiblock.helpers;

import erogenousbeef.bigreactors.api.data.ReactantData;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FuelAssembly {

    public FuelAssembly(final TileEntityReactorControlRod controlRod) {

        this._wasteColor = this._fuelColor = 0;

        if (null == controlRod || !this.build(controlRod))
            throw new IllegalStateException("Invalid fuel assembly");
    }

    public TileEntityReactorControlRod getControlRod() {
        return this._controlRod;
    }

    public EnumFacing.Axis getAxis() {
        return this._cachedOutwardFacing.getAxis();
    }

    public EnumFacing[] getRadiateDirections() {

        switch (this.getAxis()) {

            case X:
                return RADIATE_DIRECTIONS_X_AXIS;

            default:
            case Y:
                return RADIATE_DIRECTIONS_Y_AXIS;

            case Z:
                return RADIATE_DIRECTIONS_Z_AXIS;
        }
    }

    public int getFueldRodsCount() {
        return this._fuelRods.length;
    }

    public FuelRodData getFuelRodData(int rodIndex) {
        return rodIndex >= 0 && rodIndex < this._fuelRods.length ? this._fuelRods[rodIndex] : null;
    }

    @SideOnly(Side.CLIENT)
    public int getFuelColor() { return this._fuelColor; }

    @SideOnly(Side.CLIENT)
    public int getWasteColor() { return this._wasteColor; }

    public float getFuelQuota() {
        return this._totalFuelQuota;
    }

    public float getWasteQuota() {
        return this._totalWasteQuota;
    }

    public float getFuelRodFuelQuota() {
        return this._totalFuelQuota / this.getFueldRodsCount();
    }

    public float getFuelRodWasteQuota() {
        return this._totalWasteQuota / this.getFueldRodsCount();
    }

    public void updateReactants(FuelContainer data) {

        int color;

        color = this.getReactantColor(data.getFuelType());
        this._fuelColor = -1 == color ? BigReactors.defaultFluidColorFuel : color | 0xFF000000;

        color = this.getReactantColor(data.getWasteType());
        this._wasteColor = -1 == color ? BigReactors.defaultFluidColorWaste : color | 0xFF000000;
    }

    private int getReactantColor(String reactantName) {

        if (null == reactantName || reactantName.isEmpty())
            return -1;

        ReactantData reactant = Reactants.getReactant(reactantName);

        return null == reactant ? -1 : reactant.getColor();
    }

    public void updateQuota(FuelContainer data, int fuelRodsTotalCount) {

        float fuel = data.getFuelAmount();
        float waste = data.getWasteAmount();
        int fuelRoundCount = this.getFueldRodsCount();

        // how much fuel/waste is stored in this assembly?

        this._totalFuelQuota = fuel / fuelRodsTotalCount * fuelRoundCount;
        this._totalWasteQuota = waste / fuelRodsTotalCount * fuelRoundCount;

        // split fuel/waste between fuel rods

        if (EnumFacing.Axis.Y == this.getAxis()) {

            // vertical column, fluids pool to the bottom (waste first)

            final float rodCapacity = MultiblockReactor.FuelCapacityPerFuelRod;
            float remainingWaste = this._totalWasteQuota;
            float remainingFuel = this._totalFuelQuota;
            float fuelAmount, wasteAmount;

            for (int i = 0; i < fuelRoundCount; ++i) {

                if (remainingWaste > 0.0f) {

                    wasteAmount = Math.min(remainingWaste, rodCapacity);
                    remainingWaste -= wasteAmount;

                } else
                    wasteAmount = 0.0f;

                if (remainingFuel > 0.0f) {

                    fuelAmount = Math.min(remainingFuel, rodCapacity - wasteAmount);
                    remainingFuel -= fuelAmount;

                } else
                    fuelAmount = 0.0f;

                this._fuelRods[i].update(fuelAmount, wasteAmount);
            }

        } else {

            // horizontal column, fluids distribute equally

            float fuelPerRod = this._totalFuelQuota / fuelRoundCount;
            float wastePerRod = this._totalWasteQuota / fuelRoundCount;

            for (int i = 0; i < fuelRoundCount; ++i)
                this._fuelRods[i].update(fuelPerRod, wastePerRod);
        }
    }

    private boolean build(final TileEntityReactorControlRod controlRod) {

        MultiblockReactor reactor = controlRod.getReactorController();

        this._controlRod = controlRod;
        this._cachedOutwardFacing = controlRod.getOutwardFacing();

        if (null == reactor || null == this._cachedOutwardFacing)
            return false;

        BlockPos minCoord = reactor.getMinimumCoord();
        BlockPos maxCoord = reactor.getMaximumCoord();
        BlockPos lookupPosition = controlRod.getWorldPosition();
        EnumFacing lookupDirection = this._cachedOutwardFacing.getOpposite();
        int fuelRodsCount = 0;

        switch (this.getAxis()) {

            case X:
                fuelRodsCount = maxCoord.getX() - minCoord.getX() - 1;
                break;

            case Y:
                fuelRodsCount = maxCoord.getY() - minCoord.getY() - 1;
                lookupPosition = new BlockPos(lookupPosition.getX(), minCoord.getY(), lookupPosition.getZ());
                lookupDirection = EnumFacing.UP;
                break;

            case Z:
                fuelRodsCount = maxCoord.getZ() - minCoord.getZ() - 1;
                break;
        }

        fuelRodsCount = Math.abs(fuelRodsCount);

        // capture all fuel rods in this assembly

        World world = controlRod.getWorld();
        TileEntity te;

        this._fuelRods = new FuelRodData[fuelRodsCount];

        for (int i = 0; i < fuelRodsCount; ++i) {

            lookupPosition = lookupPosition.offset(lookupDirection);
            te = world.getTileEntity(lookupPosition);

            if (!(te instanceof TileEntityReactorFuelRod))
                return false;

            this._fuelRods[i] = new FuelRodData((TileEntityReactorFuelRod)te);
            ((TileEntityReactorFuelRod)te).linkToAssembly(this);
        }

        return true;
    }

    public class FuelRodData {

        public final TileEntityReactorFuelRod Rod;

        public FuelRodData(TileEntityReactorFuelRod rod) {

            this.Rod = rod;
            this._wasteAmount = this._fuelAmount = 0.0f;
        }

        public float getFuelAmount() {
            return this._fuelAmount;
        }

        public float getWasteAmount() {
            return this._wasteAmount;
        }

        public void update(float fuelAmount, float wasteAmount) {

            this._fuelAmount = fuelAmount;
            this._wasteAmount = wasteAmount;
        }

        private float _fuelAmount;
        private float _wasteAmount;
    }

    private TileEntityReactorControlRod _controlRod;
    private FuelRodData[] _fuelRods;
    private EnumFacing _cachedOutwardFacing;
    private float _totalFuelQuota;
    private float _totalWasteQuota;
    private int _fuelColor;
    private int _wasteColor;

    private static final EnumFacing[] RADIATE_DIRECTIONS_X_AXIS = {EnumFacing.UP, EnumFacing.NORTH, EnumFacing.DOWN, EnumFacing.SOUTH};
    private static final EnumFacing[] RADIATE_DIRECTIONS_Y_AXIS = EnumFacing.HORIZONTALS;
    private static final EnumFacing[] RADIATE_DIRECTIONS_Z_AXIS = {EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.UP};
}
