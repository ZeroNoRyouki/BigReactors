package erogenousbeef.bigreactors.api.registry;

import erogenousbeef.bigreactors.api.data.ReactorInteriorData;
import it.zerono.mods.zerocore.util.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ReactorInterior {

	/**
	 * Register a block as a radiation moderator for the Reactor
	 * If the block is already registered, the provided values will replace the existing ones
	 *
	 * @param oreDictName The ore-dictionary name of the block
	 * @param absorption How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
	 * @param heatEfficiency How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
	 * @param moderation How well this material moderates radiation. This is a divisor; should not be below 1.
	 * @param heatConductivity How well this material conducts heat, in RF/t/m2.
	 */
	public static void registerBlock(String oreDictName, float absorption, float heatEfficiency, float moderation, float heatConductivity) {
		register(true, oreDictName, absorption, heatEfficiency, moderation, heatConductivity);
	}

	/**
	 * Deregister a previously registered radiation moderator block for the Reactor
	 *
	 * @param oreDictName The ore-dictionary name of the block
	 * @return The previously registered data for the block or null if the block was not registered
	 */
	@Nullable
	public static ReactorInteriorData deregisterBlock(String oreDictName) {
		return deregister(true, oreDictName);
	}

	/**
	 * Register a fluid as a radiation moderator for the Reactor
	 * If the fluid is already registered, the provided values will replace the existing ones
	 *
	 * @param fluidName The ore-dictionary name of the block
	 * @param absorption How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
	 * @param heatEfficiency How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
	 * @param moderation How well this material moderates radiation. This is a divisor; should not be below 1.
	 * @param heatConductivity How well this material conducts heat, in RF/t/m2.
	 */
	public static void registerFluid(String fluidName, float absorption, float heatEfficiency, float moderation, float heatConductivity) {
		register(false, fluidName, absorption, heatEfficiency, moderation, heatConductivity);
	}

	/**
	 * Deregister a previously registered radiation moderator fluid for the Reactor
	 *
	 * @param fluidName The registry name of the fluid
	 * @return The previously registered data for the fluid or null if the fluid was not registered
	 */
	@Nullable
	public static ReactorInteriorData deregisterFluid(String fluidName) {
		return deregister(false, fluidName);
	}

	/**
	 * Retrieve the radiation moderation data for the given block
	 *
	 * @param oreDictName The ore-dictionary name of the block
	 * @return The radiation moderation data registered for the block or null if the block was not registered
	 */
	@Nullable
	public static ReactorInteriorData getBlockData(String oreDictName) {
		return _reactorModeratorBlocks.get(oreDictName);
	}

	/**
	 * Retrieve the radiation moderation data for a block compatible to the given one
	 *
	 * @param stack an ItemStack containing the item block to check for
	 * @return The radiation moderation data registered for the block or null if the block was not registered
	 */	@Nullable
	public static ReactorInteriorData getBlockData(ItemStack stack) {

		final int[] ids = ItemHelper.stackIsValid(stack) ? OreDictionary.getOreIDs(stack) : null;
		int len;

		if (null == ids || 0 == (len = ids.length))
			return null;

		String name;

		for (int i = 0; i < len; ++i) {

			name = OreDictionary.getOreName(ids[i]);

			if (_reactorModeratorBlocks.containsKey(name))
				return _reactorModeratorBlocks.get(name);
		}

		return null;
	}

	@Nullable
	public static ReactorInteriorData getFluidData(String fluidName) {
		return _reactorModeratorFluids.get(fluidName);
	}



	private static void register(final boolean isBlock, final String name, final float absorption,
								 final float heatEfficiency, final float moderation, final float heatConductivity) {

		final Map<String, ReactorInteriorData> registry = isBlock ? _reactorModeratorBlocks : _reactorModeratorFluids;

		if (null == name || name.isEmpty()) {

			FMLLog.warning("Blocked an attempt to register a new radiation moderator with an empty name");
			return;
		}

		if (registry.containsKey(name)) {

			FMLLog.warning("Overriding existing radiation moderator data for <%s>", name);

			final ReactorInteriorData data = registry.get(name);

			data.absorption = absorption;
			data.heatEfficiency = heatEfficiency;
			data.moderation = moderation;

		} else {

			registry.put(name, new ReactorInteriorData(absorption, heatEfficiency, moderation, heatConductivity));
		}
	}

	@Nullable
	private static ReactorInteriorData deregister(final boolean isBlock, final String name) {

		final Map<String, ReactorInteriorData> registry = isBlock ? _reactorModeratorBlocks : _reactorModeratorFluids;

		return null != name && !name.isEmpty() ? registry.remove(name) : null;
	}

	private static Map<String, ReactorInteriorData> _reactorModeratorBlocks = new HashMap<String, ReactorInteriorData>();
	private static Map<String, ReactorInteriorData> _reactorModeratorFluids = new HashMap<String, ReactorInteriorData>();
}
