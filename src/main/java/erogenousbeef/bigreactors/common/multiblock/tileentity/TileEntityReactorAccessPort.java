package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.api.data.SourceProductMapping;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.client.gui.GuiReactorAccessPort;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.ItemHandlerWrapper;
import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.gui.container.ContainerReactorAccessPort;
import erogenousbeef.bigreactors.init.BrItems;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.lib.item.TileEntityItemStackHandler;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import it.zerono.mods.zerocore.util.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class TileEntityReactorAccessPort extends TileEntityReactorPart implements INeighborUpdatableEntity, IInputOutputPort {

	public TileEntityReactorAccessPort() {

		this._direction = Direction.Input;
		this._adjacentInventory = null;
		this._fuelInventoryWrapper = this._wasteInventoryWrapper = null;
		this._fuelInventory = new TileEntityItemStackHandler(this, 1);
		this._wasteInventory = new TileEntityItemStackHandler(this, 1);
	}

	public ItemStackHandler getItemStackHandler(boolean fuel) {
		return fuel ? this._fuelInventory : this._wasteInventory;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability) {

			if (Direction.Input == this._direction) {

				if (null == this._fuelInventoryWrapper)
					this._fuelInventoryWrapper = new ItemHandlerWrapper(this._fuelInventory) {
						@Override
						public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
							return null != stack && Reactants.isFuel(stack) ? this._handler.insertItem(slot, stack, simulate) : stack;
						}
					};

				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this._fuelInventoryWrapper);

			} else {

				if (null == this._wasteInventoryWrapper)
					this._wasteInventoryWrapper = new ItemHandlerWrapper(this._wasteInventory) {
						@Override
						public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
							return stack;
						}
					};

				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this._wasteInventoryWrapper);
			}
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canOpenGui(World world, BlockPos posistion, IBlockState state) {
		return true;
	}

	/**
	 * Return the name of the reactant to which the item in the input slot
	 */
	public String getInputReactantType() {

		ItemStack inputItem = this._fuelInventory.getStackInSlot(0);
		SourceProductMapping mapping = null != inputItem ? Reactants.getSolidToReactant(inputItem) : null;

		return null != mapping ? mapping.getProduct() : null;
	}

	/**
	 * Returns the potential amount of reactant which can be produced from this port.
     */
	public int getInputReactantAmount() {

		ItemStack inputItem = this._fuelInventory.getStackInSlot(0);
		SourceProductMapping mapping = null != inputItem ? Reactants.getSolidToReactant(inputItem) : null;

		return null != mapping ? mapping.getProductAmount(inputItem.stackSize) : 0;
	}

	/**
	 * Consume items from the input slot.
	 * Returns the amount of reactant produced.
	 * @param reactantDesired The amount of reactant desired, in reactant units (mB)
	 * @return The amount of reactant actually produced, in reactant units (mB)
	 */
	public int consumeReactantItem(int reactantDesired) {

		// TODO consume partial amount of source fuel (say, a block) and put left over back in the inventory (say, ingots)

		ItemStack inputItem = this._fuelInventory.getStackInSlot(0);
		SourceProductMapping mapping = null != inputItem ? Reactants.getSolidToReactant(inputItem) : null;
		int sourceItemsToConsume = null != mapping ? Math.min(inputItem.stackSize, mapping.getSourceAmount(reactantDesired)) : 0;
		
		if (sourceItemsToConsume <= 0)
			return 0;

		this._fuelInventory.extractItem(0, sourceItemsToConsume, false);

		return mapping.getProductAmount(sourceItemsToConsume);
	}

	/**
	 * Try to emit a given amount of reactant as a solid item.
	 * Will either match the item type already present, or will select
	 * whatever type allows the most reactant to be ejected right now.
	 * @param reactantType Type of reactant to emit.
	 * @param amount
	 * @return
	 */
	public int emitReactant(String reactantType, int amount) {
		if(reactantType == null || amount <= 0) { return 0; }
		
		ItemStack outputItem = this._wasteInventory.getStackInSlot(0);
		int outputItemMaxSize = null != outputItem ? outputItem.getMaxStackSize() : 64;

		if (outputItem != null && outputItem.stackSize >= /*getInventoryStackLimit()*/outputItemMaxSize) {
			// Already full?
			return 0;
		}
		
		// If we have an output item, try to produce more of it, given its mapping
		if(outputItem != null) {
			// Find matching mapping
			SourceProductMapping mapping = Reactants.getSolidToReactant(outputItem);
			if(mapping == null || !reactantType.equals(mapping.getProduct())) {
				// Items are incompatible!
				return 0;
			}
			
			// We're using the original source item >> reactant mapping here
			// This means that source == item, and product == reactant
			int amtToProduce = mapping.getSourceAmount(amount);
			amtToProduce = Math.min(amtToProduce, outputItemMaxSize - outputItem.stackSize);
			if(amtToProduce <= 0) {	return 0; }
			
			// Do we actually produce any reactant at this reduced amount?
			int reactantToConsume = mapping.getProductAmount(amtToProduce);
			if(reactantToConsume <= 0) { return 0; }
			
			outputItem.stackSize += amtToProduce;
			onItemsReceived();
			
			return reactantToConsume;
		}

		// Ok, we have no items. We need to figure out candidate mappings.
		// Below here, we're using the reactant >> source mappings.
		// This means that source == reactant, and product == item.
		SourceProductMapping bestMapping = null;

		List<SourceProductMapping> mappings = Reactants.getReactantToSolids(reactantType);
		if(mappings != null) {
			int bestReactantAmount = 0;
			for(SourceProductMapping mapping: mappings) {
				// How much product can we produce?
				int potentialProducts = mapping.getProductAmount(amount);
				
				// And how much reactant will that consume?
				int potentialReactant = mapping.getSourceAmount(potentialProducts);

				if(bestMapping == null || bestReactantAmount < potentialReactant) {
					bestMapping = mapping;
					bestReactantAmount = potentialReactant;
				}
			}
		}

		if(bestMapping == null) {
			BRLog.warning("There are no mapped item types for reactant %s. Using cyanite instead.", reactantType);
			bestMapping = StandardReactants.cyaniteMapping;
		}

		int itemsToProduce = Math.min(bestMapping.getProductAmount(amount), outputItemMaxSize);
		if(itemsToProduce <= 0) {
			// Can't produce even one ingot? Ok then.
			return 0;
		}

		// And clamp again in case we could produce more than 64 items
		int reactantConsumed = bestMapping.getSourceAmount(itemsToProduce);
		itemsToProduce = bestMapping.getProductAmount(reactantConsumed);

		ItemStack newItem = OreDictionaryHelper.getOre(bestMapping.getProduct());
		if(newItem == null) {
			BRLog.warning("Could not find item for oredict entry %s, using cyanite instead.", bestMapping.getSource());
			newItem = BrItems.ingotMetals.createItemStack(MetalType.Cyanite, 1);
		}
		else {
			newItem = newItem.copy(); // Don't stomp the oredict
		}
		
		newItem.stackSize = itemsToProduce;
		this._wasteInventory.setStackInSlot(0, newItem);
		onItemsReceived();
		
		return reactantConsumed;
	}
	
	// Multiblock overrides
	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {

		super.onMachineAssembled(controller);
		this._adjacentInventory = null;
		this.checkForAdjacentInventories();
	}
	
	@Override
	public void onMachineBroken() {

		super.onMachineBroken();
		this._adjacentInventory = null;
	}

	@Override
	protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataFrom(data, syncReason);

		if (SyncReason.FullSync == syncReason) {

			this._direction = Direction.from(!data.hasKey("isInlet") || data.getBoolean("isInlet"));

			if (data.hasKey("invI"))
				this._fuelInventory.deserializeNBT((NBTTagCompound) data.getTag("invI"));

			if (data.hasKey("invO"))
				this._wasteInventory.deserializeNBT((NBTTagCompound) data.getTag("invO"));

		} else {

			if (data.hasKey("inlet"))
				this.setDirection(Direction.from(data.getBoolean("inlet")), true);
		}
	}

	@Override
	protected void syncDataTo(NBTTagCompound data, SyncReason syncReason) {

		super.syncDataTo(data, syncReason);

		if (SyncReason.FullSync == syncReason) {

			data.setBoolean("isInlet", Direction.Input == this._direction);
			data.setTag("invI", this._fuelInventory.serializeNBT());
			data.setTag("invO", this._wasteInventory.serializeNBT());

		} else {

			data.setBoolean("inlet", Direction.Input == this._direction);
		}
	}

	@Override
	public Object getServerGuiElement(int guiId, EntityPlayer player) {
		return new ContainerReactorAccessPort(this, player.inventory);
	}

	@Override
	public Object getClientGuiElement(int guiId, EntityPlayer player) {
		return new GuiReactorAccessPort(new ContainerReactorAccessPort(this, player.inventory), this);
	}

	/**
	 * Called when stuff has been placed in the access port
	 */
	public void onItemsReceived() {

		distributeItems();
		markChunkDirty();
	}


	@Override
	public Direction getDirection() {
		return this._direction;
	}

	@Override
	public void setDirection(Direction direction, boolean markForUpdate) {

		if (direction == this._direction)
			return;

		this._direction = direction;

		WorldHelper.notifyBlockUpdate(this.worldObj, this.getWorldPosition(), null, null);

		if (!this.worldObj.isRemote) {

			this.distributeItems();
			this.markDirty();
		}

		notifyNeighborsOfTileChange();

	}

	@Override
	public void toggleDirection(boolean markForUpdate) {
		this.setDirection(this._direction.opposite(), markForUpdate);
	}

	protected void distributeItems() {

		if (worldObj.isRemote || this._adjacentInventory == null || this.getDirection().isInput())
			return;

		this._wasteInventory.setStackInSlot(0, ItemHandlerHelper.insertItem(this._adjacentInventory,
				this._wasteInventory.getStackInSlot(0), false));
		this.markChunkDirty();
	}
	
	protected void checkForAdjacentInventories() {

		EnumFacing facing = this.getOutwardFacing();
		IItemHandler candidateInventory = null;

		if (null != facing) {

			TileEntity te = this.worldObj.getTileEntity(this.getWorldPosition().offset(facing));

			if (null != te && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()))
				candidateInventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
		}

		if (this._adjacentInventory != candidateInventory) {

			this._adjacentInventory = candidateInventory;

			if (null != this._adjacentInventory)
				this.distributeItems();
		}
	}

	// INeighborUpdateableEntity
	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {
		checkForAdjacentInventories();
	}

	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {

		EnumFacing facing = this.getOutwardFacing();

		// is the changed block the one we are facing?
		if (null != facing && neighbor.equals(position.offset(facing)))
			this.checkForAdjacentInventories();
	}

	protected TileEntityItemStackHandler _fuelInventory;
	protected TileEntityItemStackHandler _wasteInventory;
	protected IItemHandler _fuelInventoryWrapper;
	protected IItemHandler _wasteInventoryWrapper;
	protected IItemHandler _adjacentInventory;
	protected IInputOutputPort.Direction _direction;
}
