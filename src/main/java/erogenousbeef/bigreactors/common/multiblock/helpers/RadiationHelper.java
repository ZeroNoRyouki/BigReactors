package erogenousbeef.bigreactors.common.multiblock.helpers;

import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.IRadiationModerator;
import erogenousbeef.bigreactors.api.data.ReactorInteriorData;
import erogenousbeef.bigreactors.api.registry.ReactorInterior;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.data.RadiationData;
import erogenousbeef.bigreactors.common.data.RadiationPacket;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorControlRod;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import it.zerono.mods.zerocore.util.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

/**
 * Helper for reactor radiation game logic
 * @author Erogenous Beef
 */
public class RadiationHelper {

	// Game Balance Values
	public static final float fuelPerRadiationUnit = 0.0007f; // fuel units used per fission event
	public static final float rfPerRadiationUnit = 10f; // RF generated per fission event
	public static final float fissionEventsPerFuelUnit = 0.01f; // 1 fission event per 100 mB

	public static final ReactorInteriorData airData = new ReactorInteriorData(0.1f, 0.25f, 1.1f, IHeatEntity.conductivityAir);
	public static final ReactorInteriorData waterData = new ReactorInteriorData(0.33f, 0.5f, 1.33f, IHeatEntity.conductivityWater);

	private float fertility;
	
	public RadiationHelper() {
		fertility = 1f;
	}

	public RadiationData radiate(World world, FuelContainer fuelContainer, TileEntityReactorFuelRod source, FuelAssembly fuelAssembly,
								 float fuelHeat, float environmentHeat, int numControlRods) {

		TileEntityReactorControlRod controlRod = fuelAssembly.getControlRod();

		// No fuel? No radiation!
		if(fuelContainer.getFuelAmount() <= 0) { return null; }

		// Determine radiation amount & intensity, heat amount, determine fuel usage
		RadiationData data = new RadiationData();
		data.fuelAbsorbedRadiation = 0f;

		// Base value for radiation production penalties. 0-1, caps at about 3000C;
		double radiationPenaltyBase = Math.exp(-15*Math.exp(-0.0025*fuelHeat));

		// Raw amount - what's actually in the tanks
		// Effective amount - how 
		int baseFuelAmount = fuelContainer.getFuelAmount() + (fuelContainer.getWasteAmount() / 100);
		float fuelReactivity = fuelContainer.getFuelReactivity();
		
		// Intensity = how strong the radiation is, hardness = how energetic the radiation is (penetration)
		float rawRadIntensity = (float)baseFuelAmount * fissionEventsPerFuelUnit;
		
		// Scale up the "effective" intensity of radiation, to provide an incentive for bigger reactors in general.
		float scaledRadIntensity = (float) Math.pow((rawRadIntensity), fuelReactivity);

		// Scale up a second time based on scaled amount in each fuel rod. Provides an incentive for making reactors that aren't just pancakes.
		scaledRadIntensity = (float) Math.pow((scaledRadIntensity/numControlRods), fuelReactivity) * numControlRods;

		// Apply control rod moderation of radiation to the quantity of produced radiation. 100% insertion = 100% reduction.
		float controlRodModifier = (float)(100-controlRod.getControlRodInsertion()) / 100f;
		scaledRadIntensity = scaledRadIntensity * controlRodModifier;
		rawRadIntensity = rawRadIntensity * controlRodModifier;

		// Now nerf actual radiation production based on heat.
		float effectiveRadIntensity = scaledRadIntensity * (1f + (float)(-0.95f*Math.exp(-10f*Math.exp(-0.0012f*fuelHeat))));

		// Radiation hardness starts at 20% and asymptotically approaches 100% as heat rises.
		// This will make radiation harder and harder to capture.
		float radHardness = 0.2f + (float)(0.8 * radiationPenaltyBase);

		// Calculate based on propagation-to-self
		float rawFuelUsage = (fuelPerRadiationUnit * rawRadIntensity / getFertilityModifier()) * BigReactors.CONFIG.fuelUsageMultiplier; // Not a typo. Fuel usage is thus penalized at high heats.
		data.fuelRfChange = rfPerRadiationUnit * effectiveRadIntensity;
		data.environmentRfChange = 0f;

		// Propagate radiation to others
		BlockPos originCoord = source.getPos();
		BlockPos currentCoord;
		
		effectiveRadIntensity *= 0.25f; // We're going to do this four times, no need to repeat
		RadiationPacket radPacket = new RadiationPacket();

		for (EnumFacing dir : fuelAssembly.getRadiateDirections()) {

			radPacket.hardness = radHardness;
			radPacket.intensity = effectiveRadIntensity;
			int ttl = 4;
			currentCoord = originCoord;

			while(ttl > 0 && radPacket.intensity > 0.0001f) {
				ttl--;
				currentCoord = currentCoord.offset(dir);
				performIrradiation(world, data, radPacket, currentCoord);
			}
		}

		// Apply changes
		fertility += data.fuelAbsorbedRadiation;
		data.fuelAbsorbedRadiation = 0f;
		
		// Inform fuelContainer
		fuelContainer.onRadiationUsesFuel(rawFuelUsage);
		data.fuelUsage = rawFuelUsage;
		
		return data;
	}
	
	public void tick(boolean active) {
		float denominator = 20f;
		if(!active) { denominator *= 200f; } // Much slower decay when off
		
		// Fertility decay, at least 0.1 rad/t, otherwise halve it every 10 ticks
		fertility = Math.max(0f, fertility - Math.max(0.1f, fertility/denominator));
	}
	
	private void performIrradiation(World world, RadiationData data, RadiationPacket radiation, BlockPos position) {
		TileEntity te = world.getTileEntity(position);
		if(te instanceof IRadiationModerator) {
			((IRadiationModerator)te).moderateRadiation(data, radiation);
		}
		else if (world.isAirBlock(position)) {
			moderateByAir(data, radiation);
		}
		else {
			IBlockState blockState = world.getBlockState(position);
			Block block = blockState.getBlock();
			if(block != null) {

				if (block.isAir(blockState, world, position)) {

					moderateByAir(data, radiation);

				} else if(block instanceof IFluidBlock) {

					moderateByFluid(data, radiation, ((IFluidBlock)block).getFluid());

				} else {
					// Go by block
					moderateByBlock(data, radiation, blockState);
				}
			}
			else {
				// Weird-ass problem. Assume it's air.
				moderateByAir(data, radiation);
			}
			// Do it based on fluid?
		}
	}
	
	private void moderateByAir(RadiationData data, RadiationPacket radiation) {
		applyModerationFactors(data, radiation, airData);
	}
	
	private void moderateByBlock(RadiationData data, RadiationPacket radiation, IBlockState blockState) {

		ReactorInteriorData moderatorData = null;
		Block block = blockState.getBlock();

		if(block == Blocks.IRON_BLOCK) {
			moderatorData = ReactorInterior.getBlockData("blockIron");
		}
		else if(block == Blocks.GOLD_BLOCK) {
			moderatorData = ReactorInterior.getBlockData("blockGold");
		}
		else if(block == Blocks.DIAMOND_BLOCK) {
			moderatorData = ReactorInterior.getBlockData("blockDiamond");
		}
		else if(block == Blocks.EMERALD_BLOCK) {
			moderatorData = ReactorInterior.getBlockData("blockEmerald");
		}
		else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
			moderatorData = RadiationHelper.waterData;
		}
		else {
			// Check the ore dictionary.
			moderatorData = ReactorInterior.getBlockData(ItemHelper.createItemStack(blockState, 1));
		}
		
		if(moderatorData == null) {
			moderatorData = airData;
		}

		applyModerationFactors(data, radiation, moderatorData);
	}
	
	private void moderateByFluid(RadiationData data, RadiationPacket radiation, Fluid fluid) {
		
		float absorption, heatEfficiency, moderation;

		ReactorInteriorData moderatorData = ReactorInterior.getFluidData(fluid.getName());
		
		if(moderatorData == null) {
			moderatorData = waterData;
		}

		applyModerationFactors(data, radiation, moderatorData);
	}
	
	private static void applyModerationFactors(RadiationData data, RadiationPacket radiation, ReactorInteriorData moderatorData) {
		float radiationAbsorbed = radiation.intensity * moderatorData.absorption * (1f - radiation.hardness);
		radiation.intensity = Math.max(0f, radiation.intensity - radiationAbsorbed);
		radiation.hardness /= moderatorData.moderation;
		data.environmentRfChange += moderatorData.heatEfficiency * radiationAbsorbed * rfPerRadiationUnit;
	}
	
	// Data Access
	public float getFertility() { return fertility; }

	public float getFertilityModifier() {
		if(fertility <= 1f) { return 1f; }
		else {
			return (float)(Math.log10(fertility) + 1);
		}
	}

	public void setFertility(float newFertility) {
		if(Float.isNaN(newFertility) || Float.isInfinite(newFertility)) {
			fertility = 1f;
		}
		else if(newFertility < 0f) {
			fertility = 0f;
		}
		else {
			fertility = newFertility;
		}
	}

	// Save/Load
	public void readFromNBT(NBTTagCompound data) {
		if(data.hasKey("fertility")) {
			setFertility(data.getFloat("fertility"));
		}
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data.setFloat("fertility", fertility);
		return data;
	}
	
	public void merge(RadiationHelper other) {
		fertility = Math.max(fertility, other.fertility);
	}

}
