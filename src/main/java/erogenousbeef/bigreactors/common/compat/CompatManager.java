package erogenousbeef.bigreactors.common.compat;

import com.sun.istack.internal.NotNull;
import erogenousbeef.bigreactors.common.BRLog;
import it.zerono.mods.zerocore.lib.IModInitializationHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CompatManager implements IModInitializationHandler {

    public static final CompatManager INSTANCE = new CompatManager();

    public static boolean isModLoaded(@NotNull String modId) {
        return Loader.isModLoaded(modId);
    }

    public static boolean isApiLoaded(@NotNull String api) {
        return ModAPIManager.INSTANCE.hasAPI(api);
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {

        this.loadHelpers();

        for (ModCompact helper: this._helpers)
            helper.onPreInit(event);
    }

    @Override
    public void onInit(FMLInitializationEvent event) {

        for (ModCompact helper: this._helpers)
            helper.onInit(event);
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

        for (ModCompact helper: this._helpers)
            helper.onPostInit(event);
    }

    private CompatManager() {

        this._helpers = null;
        this._modList = new HashMap<>();

        this._modList.put(IdReference.MODID_COMPUTERCRAFT, "ModComputerCraft");
        this._modList.put(IdReference.MODID_MEKANISM, "ModMekanism");
        this._modList.put(IdReference.MODID_THERMALEXPANSION, "ModThermalExpansion");
        this._modList.put(IdReference.MODID_MINEFACTORYRELOADED, "ModMineFactoryReloaded");
        this._modList.put(IdReference.MODID_APPLIEDENERGISTICS2, "ModAppliedEnergistics2");
    }

    private void loadHelpers() {

        List<ModCompact> loadedHelpers = new ArrayList<>();

        for (Map.Entry<String, String> entry : this._modList.entrySet()) {

            if (isModLoaded(entry.getKey())) {

                final ModCompact helper = createHelper(entry.getValue());

                if (null != helper)
                    loadedHelpers.add(helper);
            }
        }

        this._helpers = loadedHelpers.toArray(new ModCompact[0]);
    }

    @Nullable
    private static ModCompact createHelper(final String className) {

        try {

            Class<?> clazz = Class.forName("erogenousbeef.bigreactors.common.compat." + className);
            Constructor<?> constructor = clazz.getConstructor();

            return (ModCompact)constructor.newInstance();

        } catch (ClassNotFoundException e1) {

            BRLog.error("Can't load mod-compat helper class %s", className);

        } catch (NoSuchMethodException e2) {

            BRLog.error("No default constructor found in mod-compat helper class %s", className);

        } catch (InvocationTargetException e3) {

            BRLog.error("Can't invocate default constructor of mod-compat helper class %s", className);

        } catch (InstantiationException e4) {

            BRLog.error("Can't instantiate mod-compat helper class %s", className);

        } catch (IllegalAccessException e5) {

            BRLog.error("Can't access default constructor of mod-compat helper class %s", className);

        } catch (ClassCastException e6) {

            BRLog.error("Invalid mod-compat helper class %s", className);
        }

        return null;
    }

    private Map<String, String> _modList;
    private ModCompact[] _helpers;
}
