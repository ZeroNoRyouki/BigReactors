package erogenousbeef.bigreactors.init;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.item.ItemBRMetal;
import erogenousbeef.bigreactors.common.item.ItemMineral;
import erogenousbeef.bigreactors.common.item.ItemTieredComponent;
import erogenousbeef.bigreactors.common.item.ItemWrench;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(BigReactors.MODID)
public final class BrItems {

    // Ingots & dusts

    @GameRegistry.ObjectHolder("ingotyellorium")
    public static final ItemBRMetal ingotYellorium = null;

    @GameRegistry.ObjectHolder("ingotcyanite")
    public static final ItemBRMetal ingotCyanite = null;

    @GameRegistry.ObjectHolder("ingotgraphite")
    public static final ItemBRMetal ingotGraphite = null;

    @GameRegistry.ObjectHolder("ingotblutonium")
    public static final ItemBRMetal ingotBlutonium = null;

    @GameRegistry.ObjectHolder("ingotludicrite")
    public static final ItemBRMetal ingotLudicrite = null;

    @GameRegistry.ObjectHolder("ingotsteel")
    public static final ItemBRMetal ingotSteel = null;

    @GameRegistry.ObjectHolder("dustyellorium")
    public static final ItemBRMetal dustYellorium = null;

    @GameRegistry.ObjectHolder("dustcyanite")
    public static final ItemBRMetal dustCyanite = null;

    @GameRegistry.ObjectHolder("dustgraphite")
    public static final ItemBRMetal dustGraphite = null;

    @GameRegistry.ObjectHolder("dustblutonium")
    public static final ItemBRMetal dustBlutonium = null;

    @GameRegistry.ObjectHolder("dustludicrite")
    public static final ItemBRMetal dustLudicrite = null;

    @GameRegistry.ObjectHolder("duststeel")
    public static final ItemBRMetal dustSteel = null;

    // Minerals

    @GameRegistry.ObjectHolder("mineralanglesite")
    public static final ItemMineral mineralAnglesite = null;

    @GameRegistry.ObjectHolder("mineralbenitoite")
    public static final ItemMineral mineralBenitoite = null;

    // Reactor components

    @GameRegistry.ObjectHolder("reactorcasingcores")
    public static final ItemTieredComponent reactorCasingCores = null;

    // Turbine components

    @GameRegistry.ObjectHolder("turbinehousingcores")
    public static final ItemTieredComponent turbineHousingCores = null;

    // Miscellanea

    @GameRegistry.ObjectHolder("wrench")
    public static final ItemWrench wrench = null;
}
