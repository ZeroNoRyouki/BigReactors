package erogenousbeef.bigreactors.client;

import erogenousbeef.bigreactors.client.renderer.RendererReactorFuelRod;
import erogenousbeef.bigreactors.client.renderer.RotorSpecialRenderer;
import erogenousbeef.bigreactors.common.CommonProxy;
import erogenousbeef.bigreactors.common.block.BlockBR;
import erogenousbeef.bigreactors.common.block.BlockBRGenericFluid;
import erogenousbeef.bigreactors.common.item.ItemBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineRotorBearing;
import erogenousbeef.bigreactors.gui.BeefGuiIconManager;
import erogenousbeef.bigreactors.init.BrFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		block.onPostClientRegister();
		return block;
	}

	@Override
	public BlockBRGenericFluid register(BlockBRGenericFluid block) {

		super.register(block);
		block.onPostClientRegister();
		return block;
	}

	@Override
	public ItemBase register(ItemBase item) {

		super.register(item);
		item.onPostClientRegister();
		return item;
	}

	@Override
	public void onInit(FMLInitializationEvent event) {

		super.onInit(event);

		MinecraftForge.EVENT_BUS.register(new BRRenderTickHandler());

		// register TESRs
		this.registerTESRs();
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {

		final TextureMap map = event.getMap();

		this.registerFluidTextures(map, BrFluids.fluidSteam);
		this.registerFluidTextures(map, BrFluids.fluidFuelColumn);

		GuiIcons.registerIcons(map);
		CommonBlockIcons.registerIcons(map);

		// Reset any controllers which have TESRs which cache displaylists with UV data in 'em
		// This is necessary in case a texture pack changes UV coordinates on us

        /* TODO track turbines locally
		Set<MultiblockControllerBase> controllers = MultiblockRegistry.getControllersFromWorld(FMLClientHandler.instance().getClient().theWorld);
		if(controllers != null) {
			for(MultiblockControllerBase controller: controllers) {
				if(controller instanceof MultiblockTurbine) {
					((MultiblockTurbine)controller).resetCachedRotors();
				}
			}
		}
		*/
	}

	private void registerTESRs() {

		// reactor fuel rods
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReactorFuelRod.class, new RendererReactorFuelRod());

		// turbine rotor
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurbineRotorBearing.class, new RotorSpecialRenderer());
	}

	private void registerFluidTextures(final TextureMap map, final Fluid fluid) {

		map.registerSprite(fluid.getStill());
		map.registerSprite(fluid.getFlowing());
	}
}
