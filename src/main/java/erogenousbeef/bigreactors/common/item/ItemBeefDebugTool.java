package erogenousbeef.bigreactors.common.item;

import erogenousbeef.bigreactors.common.interfaces.IBeefDebuggableTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemBeefDebugTool extends ItemBase {

	public ItemBeefDebugTool(String itemName) {

		super(itemName);
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advancedTooltips) {
		super.addInformation(stack, player, infoList, advancedTooltips);
		infoList.add("Rightclick a block to show debug info");
		infoList.add("");
		infoList.add(TextFormatting.ITALIC + "Queries on server, by default.");
		infoList.add(TextFormatting.GREEN + "Shift:" + TextFormatting.GRAY + TextFormatting.ITALIC + " Query on client");
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
										   float hitX, float hitY, float hitZ, EnumHand hand) {

		if(player.isSneaking() != world.isRemote) {
			return EnumActionResult.PASS;
		}
		
		String clientOrServer = world.isRemote ? "CLIENT":"SERVER";

		TileEntity te = world.getTileEntity(pos);
		if(te instanceof IBeefDebuggableTile) {
			String result = ((IBeefDebuggableTile)te).getDebugInfo();
			if(result != null && !result.isEmpty()) {
				String[] results = result.split("\n");
				String initialMessage = String.format("[%s] Beef Debug Tool:", clientOrServer);
				player.addChatMessage(new TextComponentString(initialMessage));
				for(String r : results) {
					player.addChatMessage(new TextComponentString(r));
				}
				return EnumActionResult.SUCCESS;
			}
		}

		IBlockState state = world.getBlockState(pos);
		Block b = state.getBlock();
		if(b != null) {
			// TODO Commented until we redo multiblock debugging
			/*
			ItemStack blockStack = new ItemStack(b, 1, world.getBlockMetadata(x,y,z));
			String oreName = ItemHelper.oreProxy.getOreName(blockStack);
			player.addChatMessage(new TextComponentString(String.format("[%s] Canonical ore name for %s: %s", world.isRemote?"CLIENT":"SERVER", b.getUnlocalizedName(), oreName)));

			ArrayList<String> allOreNames = OreDictionaryArbiter.getAllOreNames(blockStack);
			if(allOreNames != null) {
				player.addChatMessage(new TextComponentString(String.format("[%s] All ore names (%d):", clientOrServer, allOreNames.size())));
				for(String on : allOreNames) {
					player.addChatMessage(new TextComponentString(on));
				}
			}
			else {
				player.addChatMessage(new TextComponentString("getAllOreNames returned null"));
			}
			*/
		}

		// Consume clicks by default
		return EnumActionResult.PASS;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return false;
	}

	/*
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		return false;
	}
	*/
}
