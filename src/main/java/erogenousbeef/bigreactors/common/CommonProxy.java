package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.common.block.BlockBR;
import erogenousbeef.bigreactors.common.block.BlockBRGenericFluid;
import erogenousbeef.bigreactors.common.compat.CompatManager;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.bigreactors.common.item.ItemBase;
import it.zerono.mods.zerocore.lib.IModInitializationHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CommonProxy implements IModInitializationHandler {

	public BlockBR register(BlockBR block) {

		ForgeRegistries.BLOCKS.register(block);
		block.setCreativeTab(BigReactors.TAB);
		block.onPostRegister();
		return block;
	}

	public BlockBRGenericFluid register(BlockBRGenericFluid block) {

		ForgeRegistries.BLOCKS.register(block);
		block.setCreativeTab(BigReactors.TAB);
		block.onPostRegister();
		return block;
	}

	public ItemBase register(ItemBase item) {

		ForgeRegistries.ITEMS.register(item);
		item.setCreativeTab(BigReactors.TAB);
		item.onPostRegister();
		return item;
	}

	public void register(Class<? extends TileEntity> tileEntityClass) {

		GameRegistry.registerTileEntity(tileEntityClass, BigReactors.MODID + tileEntityClass.getSimpleName());
	}

	@Override
	public void onPreInit(FMLPreInitializationEvent event) {
		CompatManager.INSTANCE.onPreInit(event);
	}

	@Override
	public void onInit(FMLInitializationEvent event) {
		CompatManager.INSTANCE.onInit(event);
	}

	@Override
	public void onPostInit(FMLPostInitializationEvent event) {

		if (BigReactors.CONFIG.autoAddUranium)
			Reactants.registerSolid("ingotUranium", StandardReactants.yellorium);

		CompatManager.INSTANCE.onPostInit(event);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void setIcons(TextureStitchEvent.Post event) {
	}

	public void temp_sendPlayerStatusMessage(@Nonnull final EntityPlayer player, @Nonnull final ITextComponent message) {

		if (player instanceof EntityPlayerMP)
			((EntityPlayerMP)player).connection.sendPacket(new SPacketChat(message, ChatType.GAME_INFO));
	}

}
