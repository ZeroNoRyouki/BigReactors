package erogenousbeef.bigreactors.common.multiblock.interfaces;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IMultiblockGuiHandler {

	/**
	 * @param inventoryPlayer The inventory of the player opening the GUI.
	 * @return The Container object for use by the GUI. Null if there isn't any.
	 */
	public Object getContainer(InventoryPlayer inventoryPlayer);
	
	
	/**
	 * Return the BeefGuiBase which will display this block's GUI.
	 * @param inventoryPlayer The inventory of the player opening the GUI
	 * @return The BeefGuiBase object which will display this block's GUI, or null if none.
	 */
	@SideOnly(Side.CLIENT)
	public Object getGuiElement(InventoryPlayer inventoryPlayer);	
}
