package erogenousbeef.bigreactors.gui.container;

import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorAccessPort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerReactorAccessPort extends Container {

	protected TileEntityReactorAccessPort _port;
	protected IItemHandler _fuelHandler;
	protected IItemHandler _wasteHandler;
	/*
	private static final int SLOT_INPUT = 0;
	private static final int SLOT_OUTPUT = 1;
	*/
	public ContainerReactorAccessPort(TileEntityReactorAccessPort port, InventoryPlayer inv) {

		this._port = port;
		//this._fuelHandler = this._wasteHandler = null;
		this._fuelHandler = this._port.getItemStackHandler(true);
		this._wasteHandler = this._port.getItemStackHandler(false);
		this.addSlots();
		this.addPlayerInventory(inv);
	}

	protected void addSlots() {
		/*
		//IItemHandler handler = this._itemHandler = this._port.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		this._fuelHandler = this._port.getItemStackHandler(true);
		this._wasteHandler = this._port.getItemStackHandler(false);
		*/
		/*
		this.addSlotToContainer(new SlotReactorFuel(this._fuelHandler, 0, 44, 18, true));
		this.addSlotToContainer(new SlotRemoveOnly(this._wasteHandler, 0, 116, 18));
		*/

		this.addSlotToContainer(new SlotItemHandler(this._fuelHandler, 0, 44, 18) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return null != stack && Reactants.isFuel(stack);
			}
		});

		this.addSlotToContainer(new SlotItemHandler(this._wasteHandler, 0, 116, 18) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return null != stack && Reactants.isWaste(stack);
			}
		});
	}
	
	protected int getPlayerInventoryVerticalOffset()
	{
		return 74;
	}

	protected void addPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, getPlayerInventoryVerticalOffset() + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, getPlayerInventoryVerticalOffset() + 58));
		}
	}	
	
	@Override
	public boolean canInteractWith(EntityPlayer player)	{
		return _port.isUseableByPlayer(player);
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack stack = null;
		Slot slotObject = inventorySlots.get(slot);
		int numSlots = 2;

		if(slotObject != null && slotObject.getHasStack())
		{
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			if(slot < numSlots)
			{
				if(!mergeItemStack(stackInSlot, numSlots, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if(!mergeItemStack(stackInSlot, 0, numSlots, false))
			{
				return null;
			}

			if(stackInSlot.stackSize == 0)
			{
				slotObject.putStack(null);
			}
			else
			{
				slotObject.onSlotChanged();
			}

			if(stackInSlot.stackSize == stack.stackSize)
			{
				return null;
			}

			slotObject.onPickupFromSlot(player, stackInSlot);
		}

		return stack;
	}

	/**
	 * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
	 * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
	 * implementation do not check if the item is valid for the slot
	 */
	// Override this so we can check if stuff is valid for the slot
	// Stolen directly from powercrystals
	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotRange, boolean reverse)
	{
		boolean successful = false;
		int slotIndex = slotStart;
		//int maxStack = Math.min(stack.getMaxStackSize(), _port.getInventoryStackLimit());
		int maxStack = stack.getMaxStackSize();

		if(reverse)
		{
			slotIndex = slotRange - 1;
		}

		Slot slot;
		ItemStack existingStack;

		if(stack.isStackable())
		{
			while(stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart))
			{
				slot = this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();

				if(slot.isItemValid(stack) && existingStack != null && existingStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack))
				{
					int existingSize = existingStack.stackSize + stack.stackSize;

					if(existingSize <= maxStack)
					{
						stack.stackSize = 0;
						existingStack.stackSize = existingSize;
						slot.onSlotChanged();
						successful = true;
					}
					else if (existingStack.stackSize < maxStack)
					{
						stack.stackSize -= maxStack - existingStack.stackSize;
						existingStack.stackSize = maxStack;
						slot.onSlotChanged();
						successful = true;
					}
				}

				if(reverse)
				{
					--slotIndex;
				}
				else
				{
					++slotIndex;
				}
			}
		}

		if(stack.stackSize > 0)
		{
			if(reverse)
			{
				slotIndex = slotRange - 1;
			}
			else
			{
				slotIndex = slotStart;
			}

			while(!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart)
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				existingStack = slot.getStack();

				if(slot.isItemValid(stack) && existingStack == null)
				{
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.stackSize = 0;
					successful = true;
					break;
				}

				if(reverse)
				{
					--slotIndex;
				}
				else
				{
					++slotIndex;
				}
			}
		}

		return successful;
	}
}
