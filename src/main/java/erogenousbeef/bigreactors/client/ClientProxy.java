package erogenousbeef.bigreactors.client;

import erogenousbeef.bigreactors.common.block.BlockBR;
import erogenousbeef.bigreactors.common.item.ItemBase;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.CommonProxy;
import erogenousbeef.bigreactors.gui.BeefGuiIconManager;
import org.apache.commons.lang3.tuple.Pair;
import zero.mods.zerocore.api.multiblock.MultiblockClientTickHandler;
import zero.mods.zerocore.api.multiblock.MultiblockControllerBase;
import zero.mods.zerocore.api.multiblock.MultiblockRegistry;
import zero.mods.zerocore.lib.client.ICustomModelsProvider;

import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	public static BeefGuiIconManager GuiIcons;
	public static CommonBlockIconManager CommonBlockIcons;

	public static long lastRenderTime = Minecraft.getSystemTime();
	
	public ClientProxy() {
		GuiIcons = new BeefGuiIconManager();
		CommonBlockIcons = new CommonBlockIconManager();
	}

	@Override
	public BlockBR register(BlockBR block) {

		super.register(block);

		if (block instanceof ICustomModelsProvider) {

			ICustomModelsProvider provider = (ICustomModelsProvider)block;
			ResourceLocation location = provider.getCustomResourceLocation();
			List<Pair<Integer, String>> mappings = ((ICustomModelsProvider)block).getMetadataToModelMappings();

			if (null == location)
				location = block.getRegistryName();

			for (Pair<Integer, String> mapping : mappings) {

				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
						mapping.getLeft().intValue(),
						new ModelResourceLocation(location, mapping.getRight()));
			}
		} else
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
					new ModelResourceLocation(block.getRegistryName(), "inventory"));

		return block;
	}

	@Override
	public ItemBase register(ItemBase item) {

		super.register(item);

		if (item instanceof ICustomModelsProvider) {

			ICustomModelsProvider provider = (ICustomModelsProvider)item;
			ResourceLocation location = provider.getCustomResourceLocation();
			List<Pair<Integer, String>> mappings = ((ICustomModelsProvider)item).getMetadataToModelMappings();

			if (null == location) {

				location = item.getRegistryName();
				location = new ResourceLocation(location.getResourceDomain(), "items/" + location.getResourcePath());
			}

			for (Pair<Integer, String> mapping : mappings) {

				ModelLoader.setCustomModelResourceLocation(item,
						mapping.getLeft().intValue(),
						new ModelResourceLocation(location, mapping.getRight()));
			}
		}

		return item;
	}

	@Override
	public void preInit() {}

	@Override
	public void init()
	{
		super.init();

		MinecraftForge.EVENT_BUS.register(new MultiblockClientTickHandler());
		MinecraftForge.EVENT_BUS.register(new BRRenderTickHandler());

		// TODO Commented temporarily to allow this thing to compile...
		/*
		BlockFuelRod.renderId = RenderingRegistry.getNextAvailableRenderId();
		ISimpleBlockRenderingHandler fuelRodISBRH = new SimpleRendererFuelRod();
		RenderingRegistry.registerBlockHandler(BlockFuelRod.renderId, fuelRodISBRH);
		
		BlockTurbineRotorPart.renderId = RenderingRegistry.getNextAvailableRenderId();
		ISimpleBlockRenderingHandler rotorISBRH = new RotorSimpleRenderer();
		RenderingRegistry.registerBlockHandler(BlockTurbineRotorPart.renderId, rotorISBRH);	

		if(BigReactors.blockTurbinePart != null) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurbineRotorBearing.class, new RotorSpecialRenderer());
		}*/
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		TextureMap map = event.getMap();

		BigReactors.registerNonBlockFluidIcons(map);
		GuiIcons.registerIcons(map);
		CommonBlockIcons.registerIcons(map);

		// Reset any controllers which have TESRs which cache displaylists with UV data in 'em
		// This is necessary in case a texture pack changes UV coordinates on us

		Set<MultiblockControllerBase> controllers = MultiblockRegistry.getControllersFromWorld(FMLClientHandler.instance().getClient().theWorld);
		if(controllers != null) {
			for(MultiblockControllerBase controller: controllers) {
				if(controller instanceof MultiblockTurbine) {
					((MultiblockTurbine)controller).resetCachedRotors();
				}
			}
		}
	}
}
