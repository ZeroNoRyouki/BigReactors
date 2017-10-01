package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.init.BrBlocks;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CreativeTabBR extends CreativeTabs {

	public CreativeTabBR(String label) {
		super(label);
	}

	public Item getTabIconItem() {
		return Item.getItemFromBlock(BrBlocks.brOre);
	}

	/**
	 * only shows items which have tabToDisplayOn == this
	 *
	 * @param list
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(List<ItemStack> list) {

		this.addToDisplayList(list, BrBlocks.brOre);
		this.addToDisplayList(list, BrItems.minerals);
		this.addToDisplayList(list, BrItems.ingotMetals);
		this.addToDisplayList(list, BrItems.dustMetals);
		this.addToDisplayList(list, BrBlocks.blockMetals);
		this.addToDisplayList(list, BrItems.wrench);

		// Reactor parts

		this.addToDisplayList(list, BrItems.reactorCasingCores);
		this.addToDisplayList(list, BrBlocks.reactorCasing);
		this.addToDisplayList(list, BrBlocks.reactorGlass);
		this.addToDisplayList(list, BrBlocks.reactorController);
		this.addToDisplayList(list, BrBlocks.reactorFuelRod);
		this.addToDisplayList(list, BrBlocks.reactorControlRod);
		this.addToDisplayList(list, BrBlocks.reactorPowerTapRF);
		this.addToDisplayList(list, BrBlocks.reactorPowerTapTesla);
		this.addToDisplayList(list, BrBlocks.reactorAccessPort);
		this.addToDisplayList(list, BrBlocks.reactorCoolantPort);
		this.addToDisplayList(list, BrBlocks.reactorCreativeCoolantPort);
		this.addToDisplayList(list, BrBlocks.reactorComputerPort);
		this.addToDisplayList(list, BrBlocks.reactorRedstonePort);
		this.addToDisplayList(list, BrBlocks.reactorRedNetPort);

		// Turbine parts

		this.addToDisplayList(list, BrItems.turbineHousingCores);
		this.addToDisplayList(list, BrBlocks.turbineHousing);
		this.addToDisplayList(list, BrBlocks.turbineGlass);
		this.addToDisplayList(list, BrBlocks.turbineController);
		this.addToDisplayList(list, BrBlocks.turbineBearing);
		this.addToDisplayList(list, BrBlocks.turbineRotorShaft);
		this.addToDisplayList(list, BrBlocks.turbineRotorBlade);
		this.addToDisplayList(list, BrBlocks.turbinePowerTapRF);
		this.addToDisplayList(list, BrBlocks.turbinePowerTapTesla);
		this.addToDisplayList(list, BrBlocks.turbineFluidPort);
		this.addToDisplayList(list, BrBlocks.turbineCreativeSteamGenerator);
		this.addToDisplayList(list, BrBlocks.turbineComputerPort);
	}

	@SideOnly(Side.CLIENT)
	private void addToDisplayList(@Nonnull List<ItemStack> list, @Nullable Block block) {

		if (null != block) {

			final ItemStack stack = new ItemStack(block);

			block.getSubBlocks(stack.getItem(), this, list);
		}
	}

	@SideOnly(Side.CLIENT)
	private void addToDisplayList(@Nonnull List<ItemStack> list, @Nullable Item item) {

		if (null != item)
			item.getSubItems(item, this, list);
	}
}