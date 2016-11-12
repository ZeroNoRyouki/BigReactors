package erogenousbeef.bigreactors.api.registry;

import erogenousbeef.bigreactors.api.data.CoilPartData;
import net.minecraftforge.fml.common.FMLLog;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TurbineCoil {
	private static Map<String, CoilPartData> _blocks = new HashMap<String, CoilPartData>();

	/**
	 * Register a block as permissible in a turbine's inductor coil.
	 *
	 * @param oreDictName Name of the block, as registered in the ore dictionary
	 * @param efficiency  Efficiency of the block. 1.0 == iron, 2.0 == gold, etc.
	 * @param bonus		  Energy bonus of the block, if any. Normally 1.0. This is an exponential term and should only be used for EXTREMELY rare blocks!
	 */
	public static void registerBlock(String oreDictName, float efficiency, float bonus, float extractionRate) {

		if (null == oreDictName || oreDictName.isEmpty()) {

			FMLLog.warning("Blocked an attempt to register a new block in the turbine's inductor coil");
			return;
		}

		if (_blocks.containsKey(oreDictName)) {

			final CoilPartData data = _blocks.get(oreDictName);

			FMLLog.warning("Overriding existing coil part data for oredict name <%s>, original values: eff %.2f / bonus %.2f, new values: eff %.2f / bonus %.2f",
					oreDictName, data.efficiency, data.bonus, efficiency, bonus);

			data.efficiency = efficiency;
			data.bonus = bonus;

		} else {

			_blocks.put(oreDictName, new CoilPartData(efficiency, bonus, extractionRate));
		}
	}

	/**
	 * Deregister a previously registered block from the turbine's inductor coil.
	 *
	 * @param oreDictName Name of the block, as registered in the ore dictionary
	 * @return The previously registered data for the block or null if the block was not registered
	 */
	@Nullable
	public static CoilPartData deregisterBlock(String oreDictName) {
		return null != oreDictName && !oreDictName.isEmpty() ? _blocks.remove(oreDictName) : null;
	}

	/**
	 * Retrieve the coil data for the given block
	 *
	 * @param oreDictName The ore-dictionary name of the block
	 * @return The coil data registered for the block or null if the block was not registered
	 */
	@Nullable
	public static CoilPartData getBlockData(String oreDictName) {
		return _blocks.get(oreDictName);
	}
}
