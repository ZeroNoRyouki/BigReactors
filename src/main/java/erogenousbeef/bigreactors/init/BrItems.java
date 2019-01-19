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

    @GameRegistry.ObjectHolder("ingotmetals")
    public static final ItemBRMetal ingotMetals = null;

    @GameRegistry.ObjectHolder("dustmetals")
    public static final ItemBRMetal dustMetals = null;

    /*
    @GameRegistry.ObjectHolder("minerals")
    public static final ItemMineral minerals = null;
    */
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