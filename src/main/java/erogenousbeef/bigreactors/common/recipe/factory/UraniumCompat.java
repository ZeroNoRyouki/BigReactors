package erogenousbeef.bigreactors.common.recipe.factory;

import com.google.gson.JsonObject;
import erogenousbeef.bigreactors.common.BigReactors;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nonnull;

public class UraniumCompat {

    public static class YelloriumIngot implements IIngredientFactory {
        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return new OreIngredient(BigReactors.CONFIG.registerYelloriumAsUranium ? "ingotUranium" : "ingotYellorium");
        }
    }

    public static class BlutoniumIngot implements IIngredientFactory {
        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return new OreIngredient(BigReactors.CONFIG.registerYelloriumAsUranium ? "ingotPlutonium" : "ingotBlutonium");
        }
    }
}
