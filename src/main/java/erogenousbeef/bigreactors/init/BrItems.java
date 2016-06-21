package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.CommonProxy;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import erogenousbeef.bigreactors.common.item.ItemBeefDebugTool;
import net.minecraftforge.fml.common.registry.GameRegistry;
import zero.mods.zerocore.lib.MetalSize;

public final class BrItems {

    // Ingots
    public static final ItemBRMetal ingotMetals;
    public static final ItemBRMetal dustMetals;

    public static final ItemBeefDebugTool beefDebugTool;

    public static void initialize() {
    }

    static {

        CommonProxy proxy = BRLoader.proxy;

        // register items

        // - metal ingots and dusts
        ingotMetals = (ItemBRMetal) proxy.register(new ItemBRMetal("ingotMetals", MetalSize.Ingot));
        dustMetals = (ItemBRMetal) proxy.register(new ItemBRMetal("dustMetals", MetalSize.Dust));

        // - misc items
        beefDebugTool = (ItemBeefDebugTool)proxy.register(new ItemBeefDebugTool("beefDebugTool"));
    }
}
