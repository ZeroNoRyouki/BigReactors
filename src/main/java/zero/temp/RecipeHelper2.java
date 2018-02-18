package zero.temp;

// TODO : move this to ZC to replace RecipeHelper

import it.zerono.mods.zerocore.util.CodeHelper;
import it.zerono.mods.zerocore.util.ItemHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class RecipeHelper2 {

    public static final String EMPTY_ROW3 = "   ";

    public static void addSmelting(@Nonnull final Block input, @Nonnull final ItemStack output, final float xp) {

        GameRegistry.addSmelting(input, output, xp);
    }

    public static void addSmelting(@Nonnull final Item input, @Nonnull final ItemStack output, final float xp) {
        GameRegistry.addSmelting(input, output, xp);
    }

    public static void addSmelting(@Nonnull final ItemStack input, @Nonnull final ItemStack output, final float xp) {
        GameRegistry.addSmelting(input, output, xp);
    }

    public static void addShaped(@Nonnull final IForgeRegistry<IRecipe> registry, @Nonnull final ItemStack result,
                                 Object... recipe) {

        boolean useOreDict = false;

        for (int i = 3; i < recipe.length; i++) {

            if (recipe[i] instanceof String) {
                useOreDict = true;
            }
        }

        final ResourceLocation name = RecipeHelper2.getNameForRecipe(registry, result);
        IRecipe newRecipe;

        if (useOreDict) {

            newRecipe = new ShapedOreRecipe(name, result, recipe);

        } else {

            final CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(recipe);

            newRecipe = new ShapedRecipes(result.getItem().getRegistryName().toString(),
                    primer.width, primer.height, primer.input, result);
        }

        newRecipe.setRegistryName(name);
        registry.register(newRecipe);
    }

    public static void addShapeless(@Nonnull final IForgeRegistry<IRecipe> registry, @Nonnull final ItemStack result,
                                    Object... recipe) {

        boolean useOreDict = false;

        for (int i = 0; i < recipe.length; i++) {

            if (recipe[i] instanceof String) {
                useOreDict = true;
            }
        }

        final ResourceLocation name = RecipeHelper2.getNameForRecipe(registry, result);
        IRecipe newRecipe;

        if (useOreDict) {

            newRecipe = new ShapelessOreRecipe(name, result, recipe);

        } else {

            final List<ItemStack> list = new ArrayList<>();

            for (Object object : recipe) {

                if (object instanceof ItemStack) {

                    list.add(ItemHelper.stackFrom((ItemStack)object));

                } else if (object instanceof Item) {

                    list.add(new ItemStack((Item)object));

                } else if (object instanceof Block) {

                    list.add(new ItemStack((Block)object));

                } else{

                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown ingredient type " + object.getClass().getName());
                }
            }

            newRecipe = new ShapelessRecipes(name.getResourceDomain(), result, RecipeHelper2.buildIngredientsList(recipe));
        }

        newRecipe.setRegistryName(name);
        registry.register(newRecipe);
    }

    private static ResourceLocation getNameForRecipe(@Nonnull final IForgeRegistry<IRecipe> registry, ItemStack output) {

        final String callingModId = CodeHelper.getModIdFromActiveModContainer();
        final ResourceLocation baseName = new ResourceLocation(callingModId, output.getItem().getRegistryName().getResourcePath());

        ResourceLocation recipeName = baseName;
        int index = 0;

        while (registry.containsKey(recipeName)) {
            recipeName = new ResourceLocation(callingModId, baseName.getResourcePath() + "_" + index++);
        }

        return recipeName;
    }

    private static NonNullList<Ingredient> buildIngredientsList(Object... input) {

        final NonNullList<Ingredient> list = NonNullList.create();

        for (Object obj : input) {

            if (obj instanceof Ingredient) {

                list.add((Ingredient) obj);

            } else {

                final Ingredient ingredient = CraftingHelper.getIngredient(obj);

                list.add(null == ingredient ? Ingredient.EMPTY : ingredient);
            }
        }

        return list;
    }
}