package erogenousbeef.bigreactors.common.recipe.factory;

import com.google.gson.JsonObject;
import erogenousbeef.bigreactors.common.BigReactors;
import it.zerono.mods.zerocore.util.OreDictionaryHelper;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class ConfigCondition implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {

        final String config = JsonUtils.getString(json, "config");
        boolean result;

        switch (config) {

            default:
                result = false;
                break;

            case "registerGraphiteCoalCraftingRecipes":
                result = BigReactors.CONFIG.registerGraphiteCoalCraftingRecipes;
                break;

            case "registerGraphiteCharcoalCraftingRecipes":
                result = BigReactors.CONFIG.registerGraphiteCharcoalCraftingRecipes;
                break;

            case "enableCyaniteFromYelloriumRecipe":
                result = BigReactors.CONFIG.enableCyaniteFromYelloriumRecipe;
                break;

            case "enableReactorPowerTapRecipe":
                result = BigReactors.CONFIG.enableReactorPowerTapRecipe;
                break;

            case "useNormalGlass":
                result = !shouldUseHardenedGlass() && !shouldUseReinforcedGlass();
                break;

            case "useHardenedGlass":
                result = shouldUseHardenedGlass();
                break;

            case "useGlassReinforced":
                result = shouldUseReinforcedGlass();
                break;
        }

        return () -> result;
    }

    private static boolean shouldUseReinforcedGlass() {
        return BigReactors.CONFIG.requireObsidianGlass && OreDictionaryHelper.doesOreNameExist("glassReinforced");
    }

    private static boolean shouldUseHardenedGlass() {
        return BigReactors.CONFIG.requireObsidianGlass && OreDictionaryHelper.doesOreNameExist("blockGlassHardened");
    }
}
