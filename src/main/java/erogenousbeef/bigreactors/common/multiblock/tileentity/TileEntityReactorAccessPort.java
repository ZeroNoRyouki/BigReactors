package erogenousbeef.bigreactors.common.multiblock.tileentity;

import java.util.List;

import erogenousbeef.bigreactors.common.MetalType;
import erogenousbeef.bigreactors.init.BrItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
//import cofh.lib.util.helpers.BlockHelper;
//import cofh.lib.util.helpers.ItemHelper;
import erogenousbeef.bigreactors.api.data.SourceProductMapping;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.client.gui.GuiReactorAccessPort;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.gui.container.ContainerReactorAccessPort;
import erogenousbeef.bigreactors.utils.AdjacentInventoryHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.lib.item.TileEntityItemStackHandler;
import zero.mods.zerocore.util.WorldHelper;

public class TileEntityReactorAccessPort extends TileEntityReactorPart implements INeighborUpdatableEntity {

	protected TileEntityItemStackHandler _inventories;

	protected boolean isInlet;
	protected AdjacentInventoryHelper adjacencyHelper;
	
	public static final int SLOT_INLET = 0;
	public static final int SLOT_OUTLET = 1;
	
	private static final int[] kInletExposed = {SLOT_INLET};
	private static final int[] kOutletExposed = {SLOT_OUTLET};
	
	public TileEntityReactorAccessPort() {

		super();
		this._inventories = new TileEntityItemStackHandler(this, 2);
		isInlet = true;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability ?
			true : super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability)
			return (T)this._inventories;

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canOpenGui(World world, BlockPos posistion, IBlockState state) {
		return true;
	}

	// Return the name of the reactant to which the item in the input slot
	public String getInputReactantType() {
		ItemStack inputItem = this._inventories.getStackInSlot(SLOT_INLET);
		if(inputItem == null) { return null; }
		SourceProductMapping mapping = Reactants.getSolidToReactant(inputItem);
		return mapping != null ? mapping.getProduct() : null;
	}

	// Returns the potential amount of reactant which can be produced from this port.
	public int getInputReactantAmount() {
		ItemStack inputItem = this._inventories.getStackInSlot(SLOT_INLET);
		if(inputItem == null) { return 0; }

		SourceProductMapping mapping = Reactants.getSolidToReactant(inputItem);
		return mapping != null ? mapping.getProductAmount(inputItem.stackSize) : 0;
	}

	/**
	 * Consume items from the input slot.
	 * Returns the amount of reactant produced.
	 * @param reactantDesired The amount of reactant desired, in reactant units (mB)
	 * @return The amount of reactant actually produced, in reactant units (mB)
	 */
	public int consumeReactantItem(int reactantDesired) {
		ItemStack inputItem = this._inventories.getStackInSlot(SLOT_INLET);
		if(inputItem == null) { return 0; }
		
		SourceProductMapping mapping = Reactants.getSolidToReactant(inputItem);
		if(mapping == null) { return 0; }
		
		int sourceItemsToConsume = Math.min(inputItem.stackSize, mapping.getSourceAmount(reactantDesired));
		
		if(sourceItemsToConsume <= 0) { return 0; }

		this._inventories.extractItem(SLOT_INLET, sourceItemsToConsume, false);

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
		
		ItemStack outputItem = this._inventories.getStackInSlot(SLOT_OUTLET);
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

		// TODO Commented temporarily to allow this thing to compile...
		ItemStack newItem = null;//ItemHelper.getOre(bestMapping.getProduct());
		if(newItem == null) {
			BRLog.warning("Could not find item for oredict entry %s, using cyanite instead.", bestMapping.getSource());
			newItem = BrItems.ingotMetals.createItemStack(MetalType.Cyanite, 1);
		}
		else {
			newItem = newItem.copy(); // Don't stomp the oredict
		}
		
		newItem.stackSize = itemsToProduce;
		//setInventorySlotContents(SLOT_OUTLET, newItem);
		this._inventories.setStackInSlot(SLOT_OUTLET, newItem);
		onItemsReceived();
		
		return reactantConsumed;
	}
	
	// Multiblock overrides
	@Override
	public void onMachineAssembled(MultiblockControllerBase controller) {
		super.onMachineAssembled(controller);

		// TODO Commented temporarily to allow this thing to compile...
		/*
		adjacencyHelper = new AdjacentInventoryHelper(this.getOutwardsDir());
		checkForAdjacentInventories();
		*/
	}
	
	@Override
	public void onMachineBroken() {
		super.onMachineBroken();
		adjacencyHelper = null;
	}

	// TileEntity overrides
	/*
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		_inventories = new ItemStack[getSizeInventory()];
		if(tag.hasKey("Items")) {
			NBTTagList tagList = tag.getTagList("Items", 10);
			for(int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound itemTag = (NBTTagCompound)tagList.getCompoundTagAt(i);
				int slot = itemTag.getByte("Slot") & 0xff;
				if(slot >= 0 && slot <= _inventories.length) {
					ItemStack itemStack = new ItemStack((Block)null,0,0);
					itemStack.readFromNBT(itemTag);
					_inventories[slot] = itemStack;
				}
			}
		}
		
		if(tag.hasKey("isInlet")) {
			this.isInlet = tag.getBoolean("isInlet");
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList tagList = new NBTTagList();
		
		for(int i = 0; i < _inventories.length; i++) {
			if((_inventories[i]) != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte)i);
				_inventories[i].writeToNBT(itemTag);
				tagList.appendTag(itemTag);
			}
		}
		
		if(tagList.tagCount() > 0) {
			tag.setTag("Items", tagList);
		}
		
		tag.setBoolean("isInlet", isInlet);
	}*/

	@Override
	protected void saveToNBT(NBTTagCompound data) {

		super.saveToNBT(data);
		data.setBoolean("isInlet", this.isInlet);
		data.setTag("inv", this._inventories.serializeNBT());
	}

	@Override
	protected void loadFromNBT(NBTTagCompound data) {

		super.loadFromNBT(data);

		this.isInlet = data.hasKey("isInlet") ? data.getBoolean("isInlet") : true;

		if (data.hasKey("inv"))
			this._inventories.deserializeNBT((NBTTagCompound)data.getTag("inv"));
	}

	// MultiblockTileEntityBase
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		
		packetData.setBoolean("inlet", isInlet);
	}
	
	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		
		if(packetData.hasKey("inlet")) {
			setInlet(packetData.getBoolean("inlet"));
		}
	}
	
	// IInventory

	// TODO fake imp!
	/*
	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}
	// end fake imp!



	@Override
	public int getSizeInventory() {
		return NUM_SLOTS;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return _inventories[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(_inventories[slot] != null)
		{
			if(_inventories[slot].stackSize <= amount)
			{
				ItemStack itemstack = _inventories[slot];
				_inventories[slot] = null;
				markDirty();
				return itemstack;
			}
			ItemStack newStack = _inventories[slot].splitStack(amount);
			if(_inventories[slot].stackSize == 0)
			{
				_inventories[slot] = null;
			}

            markDirty();
			return newStack;
		}
		else
		{
			return null;
		}
	}

	// TODO Commented temporarily to allow this thing to compile...

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		_inventories[slot] = itemstack;
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}

        markDirty();
	}
	*/

	// TODO Commented temporarily to allow this thing to compile...
	/*
	@Override
	public String getInventoryName() {
		return "Access Port";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		BlockPos position = this.getPos();
		if(worldObj.getTileEntity(position) != this)
		{
			return false;
		}
		return entityplayer.getDistanceSq((double)position.getX() + 0.5D, (double)position.getY() + 0.5D, (double)position.getZ() + 0.5D) <= 64D;
	}

	// TODO Commented temporarily to allow this thing to compile...

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		if(itemstack == null) { return true; }

		if(slot == SLOT_INLET) {
			return Reactants.isFuel(itemstack);
		}
		else if(slot == SLOT_OUTLET) {
			return Reactants.isWaste(itemstack);
		}
		else {
			return false;
		}
	}
	*/

	// ISidedInventory
	// TODO Commented temporarily to allow this thing to compile...
	/*
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(isInlet()) {
			return kInletExposed;
		}
		else {
			return kOutletExposed;
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return isItemValidForSlot(slot, itemstack);
	}
	*/

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

	public boolean isInlet() { return this.isInlet; }

	public void setInlet(boolean shouldBeInlet) {
		if(isInlet == shouldBeInlet) { return; }

		isInlet = shouldBeInlet;

		WorldHelper.notifyBlockUpdate(worldObj, this.getPos(), null, null);

		if(!worldObj.isRemote) {
			distributeItems();
			markChunkDirty();
		}

		notifyNeighborsOfTileChange();
	}

	public void toggleInlet() {

		this.setInlet(!this.isInlet);
	}
	
	protected void distributeItems() {
		if(worldObj.isRemote) { return; }
		if(adjacencyHelper == null) { return; }
		
		if(this.isInlet()) { return; }
		
		//_inventories[SLOT_OUTLET] = adjacencyHelper.distribute(_inventories[SLOT_OUTLET]);
		this._inventories.setStackInSlot(SLOT_OUTLET, adjacencyHelper.distribute(this._inventories.getStackInSlot(SLOT_OUTLET)));
		this.markChunkDirty();
	}
	
	protected void checkForAdjacentInventories() {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		ForgeDirection outDir = getOutwardsDir();

		if(adjacencyHelper == null && outDir != ForgeDirection.UNKNOWN) {
			adjacencyHelper = new AdjacentInventoryHelper(outDir);
		}

		if(adjacencyHelper != null && outDir != ForgeDirection.UNKNOWN) {
			TileEntity te = worldObj.getTileEntity(xCoord + outDir.offsetX, yCoord + outDir.offsetY, zCoord + outDir.offsetZ);
			if(adjacencyHelper.set(te)) {
				distributeItems();
			}
		}
		*/
	}

	// INeighborUpdateableEntity
	@Override
	public void onNeighborBlockChange(World world, BlockPos position, IBlockState stateAtPosition, Block neighborBlock) {
		checkForAdjacentInventories();
	}

	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos position, BlockPos neighbor) {
		// TODO Commented temporarily to allow this thing to compile...
		/*
		int side = BlockHelper.determineAdjacentSide(this, neighborX, neighborY, neighborZ);
		if(side == getOutwardsDir().ordinal()) {
			checkForAdjacentInventories();
		}
		*/
	}
}
