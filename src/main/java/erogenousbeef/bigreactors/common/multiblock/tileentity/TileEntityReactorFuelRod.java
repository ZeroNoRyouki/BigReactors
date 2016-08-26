package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.IRadiationModerator;
import erogenousbeef.bigreactors.api.data.ReactorInteriorData;
import erogenousbeef.bigreactors.api.registry.ReactorInterior;
import erogenousbeef.bigreactors.common.data.RadiationData;
import erogenousbeef.bigreactors.common.data.RadiationPacket;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.helpers.FuelAssembly;
import erogenousbeef.bigreactors.common.multiblock.helpers.RadiationHelper;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.util.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

public class TileEntityReactorFuelRod extends TileEntityReactorPartBase implements IRadiationModerator, IHeatEntity {

	public TileEntityReactorFuelRod() {
		super();
	}
	
	// IRadiationModerator
	@Override
	public void moderateRadiation(RadiationData data, RadiationPacket radiation) {
		if(!isConnected()) { return; }

		// Grab control rod insertion and reactor heat
		MultiblockReactor reactor = getReactorController();
		float heat = reactor.getFuelHeat();
		
		int maxY = reactor.getMaximumCoord().getY();
		BlockPos position = this.getPos();

		position = new BlockPos(position.getX(), maxY, position.getZ());

		TileEntity te = worldObj.getTileEntity(position);
		if(!(te instanceof TileEntityReactorControlRod)) {
			return;
		}

		// Scale control rod insertion 0..1
		float controlRodInsertion = Math.min(1f, Math.max(0f, ((float)((TileEntityReactorControlRod)te).getControlRodInsertion())/100f));
		
		// Fuel absorptiveness is determined by control rod + a heat modifier.
		// Starts at 1 and decays towards 0.05, reaching 0.6 at 1000 and just under 0.2 at 2000. Inflection point at about 500-600.
		// Harder radiation makes absorption more difficult.
		float baseAbsorption = (float)(1.0 - (0.95 * Math.exp(-10 * Math.exp(-0.0022 * heat)))) * (1f - (radiation.hardness / getFuelHardnessDivisor()));

		// Some fuels are better at absorbing radiation than others
		float scaledAbsorption = Math.min(1f, baseAbsorption * getFuelAbsorptionCoefficient());

		// Control rods increase total neutron absorption, but decrease the total neutrons which fertilize the fuel
		// Absorb up to 50% better with control rods inserted.
		float controlRodBonus = (1f - scaledAbsorption) * controlRodInsertion * 0.5f;
		float controlRodPenalty = scaledAbsorption * controlRodInsertion * 0.5f;
		
		float radiationAbsorbed = (scaledAbsorption + controlRodBonus) * radiation.intensity;
		float fertilityAbsorbed = (scaledAbsorption - controlRodPenalty) * radiation.intensity;
		
		float fuelModerationFactor = getFuelModerationFactor();
		fuelModerationFactor += fuelModerationFactor * controlRodInsertion + controlRodInsertion; // Full insertion doubles the moderation factor of the fuel as well as adding its own level
		
		radiation.intensity = Math.max(0f, radiation.intensity - radiationAbsorbed);
		radiation.hardness /= fuelModerationFactor;
		
		// Being irradiated both heats up the fuel and also enhances its fertility
		data.fuelRfChange += radiationAbsorbed * RadiationHelper.rfPerRadiationUnit;
		data.fuelAbsorbedRadiation += fertilityAbsorbed;
	}

	// 1, upwards. How well does this fuel moderate, but not stop, radiation? Anything under 1.5 is "poor", 2-2.5 is "good", above 4 is "excellent".
	private float getFuelModerationFactor() {
		return 1.5f;
	}

	// 0..1. How well does this fuel absorb radiation?
	private float getFuelAbsorptionCoefficient() {
		// TODO: Lookup type of fuel and get data from there
		return 0.5f;
	}
	
	// Goes up from 1. How tolerant is this fuel of hard radiation?
	private float getFuelHardnessDivisor() {
		return 1.0f;
	}
	
	// IHeatEntity
	@Override
	public float getThermalConductivity() {
		return IHeatEntity.conductivityCopper;
	}

	// RectangularMultiblockTileEntityBase


	@Override
	public boolean isGoodForFrame(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_fuelrod_position", this.getPos());
		return false;
	}

	@Override
	public boolean isGoodForSides(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_fuelrod_position", this.getPos());
		return false;
	}

	@Override
	public boolean isGoodForTop(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_fuelrod_position", this.getPos());
		return false;
	}

	@Override
	public boolean isGoodForBottom(IMultiblockValidator validatorCallback) {

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_fuelrod_position", this.getPos());
		return false;
	}

	@Override
	public boolean isGoodForInterior(IMultiblockValidator validatorCallback) {
		/*
		// Check above and below. Above must be fuel rod or control rod.
		BlockPos position = this.getPos();

		TileEntity entityAbove = this.WORLD.getTileEntity(position.up());
		if(!(entityAbove instanceof TileEntityReactorFuelRod || entityAbove instanceof TileEntityReactorControlRod)) {
			validatorCallback.setLastError("multiblock.validation.reactor.invalid_fuelrod_column", position);
			return false;
		}

		// Below must be fuel rod or the base of the reactor.
		TileEntity entityBelow = this.WORLD.getTileEntity(position.down());
		if(entityBelow instanceof TileEntityReactorFuelRod) {
			return true;
		}
		else if(entityBelow instanceof RectangularMultiblockTileEntityBase) {
			return ((RectangularMultiblockTileEntityBase)entityBelow).isGoodForBottom(validatorCallback);
		}

		validatorCallback.setLastError("multiblock.validation.reactor.invalid_fuelrod_column", position);
		return false;
		*/
		return true;
	}

	@Override
	public void onMachineActivated() {
	}

	@Override
	public void onMachineDeactivated() {
	}

	// Reactor information retrieval methods
	
	/**
	 * Returns the rate of heat transfer from this block to the reactor environment, based on this block's surrounding blocks.
	 * Note that this method queries the world, so use it sparingly.
	 * 
	 * @return Heat transfer rate from fuel rod to reactor environment, in Centigrade per tick.
	 */
	public float getHeatTransferRate() {
		float heatTransferRate = 0f;
		TileEntity te;
		BlockPos position = this.getPos(), targetPosition;

		for(EnumFacing dir: EnumFacing.HORIZONTALS) {

			targetPosition = position.offset(dir);

			te = worldObj.getTileEntity(targetPosition);
			if(te instanceof TileEntityReactorFuelRod) {
				// We don't transfer to other fuel rods, due to heat pooling.
				continue;
			}
			else if(te instanceof IHeatEntity) {
				heatTransferRate += ((IHeatEntity)te).getThermalConductivity();
			}
			else if(worldObj.isAirBlock(targetPosition)) {
				heatTransferRate += IHeatEntity.conductivityAir;
			}
			else {
				heatTransferRate += getConductivityFromBlock(worldObj.getBlockState(targetPosition));
			}
		}

		return heatTransferRate;
	}
	
	private float getConductivityFromBlock(IBlockState blockState) {

		ReactorInteriorData interiorData;
		Block block = blockState.getBlock();

		if(block == Blocks.IRON_BLOCK) {
			interiorData = ReactorInterior.getBlockData("blockIron");
		}
		else if(block == Blocks.GOLD_BLOCK) {
			interiorData = ReactorInterior.getBlockData("blockGold");
		}
		else if(block == Blocks.DIAMOND_BLOCK) {
			interiorData = ReactorInterior.getBlockData("blockDiamond");
		}
		else if(block == Blocks.EMERALD_BLOCK) {
			interiorData = ReactorInterior.getBlockData("blockEmerald");
		}
		else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
			interiorData = RadiationHelper.waterData;
		}
		else {
			interiorData = ReactorInterior.getBlockData(ItemHelper.createItemStack(blockState, 1));

			if(interiorData == null && block instanceof IFluidBlock) {
				Fluid fluid = ((IFluidBlock)block).getFluid();
				if(fluid != null) {
					interiorData = ReactorInterior.getFluidData(fluid.getName());
				}
			}
		}
		
		if(interiorData == null) {
			interiorData = RadiationHelper.airData;
		}
		
		return interiorData.heatConductivity;
	}

	public void linkToAssembly(final FuelAssembly assembly) {
		this._assembly = assembly;
	}

	public FuelAssembly getFuelAssembly() {
		return this._assembly;
	}

	private FuelAssembly _assembly;
}
