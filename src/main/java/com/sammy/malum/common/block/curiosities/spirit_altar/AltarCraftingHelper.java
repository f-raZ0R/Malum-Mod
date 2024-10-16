package com.sammy.malum.common.block.curiosities.spirit_altar;

import com.sammy.malum.common.block.storage.IMalumSpecialItemAccessPoint;
import com.sammy.malum.common.recipe.SpiritInfusionRecipe;
import com.sammy.malum.core.systems.recipe.SpiritWithCount;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.helpers.BlockHelper;
import team.lodestar.lodestone.systems.recipe.IngredientWithCount;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sammy.malum.common.block.curiosities.spirit_altar.SpiritAltarBlockEntity.*;

public class AltarCraftingHelper {
	public record Ranking(int inputItemCount, int spiritStackCount, int spiritItemCount, int ingredientStackCount, int ingredientItemCount) implements Comparable<Ranking> {
		@Override
		public int compareTo(@NotNull AltarCraftingHelper.Ranking o) {
			int comparison = inputItemCount - o.inputItemCount;
			if (comparison != 0) return comparison;

			comparison = spiritStackCount - o.spiritStackCount;
			if (comparison != 0) return comparison;

			comparison = spiritItemCount - o.spiritItemCount;
			if (comparison != 0) return comparison;

			comparison = ingredientStackCount - o.ingredientStackCount;
			if (comparison != 0) return comparison;

			return ingredientItemCount - o.ingredientItemCount;
		}
	}

	public record Extraction(int numberOfStacks, int numberOfItems) {}

	/**
	 * Attempts to simulate pulling all of a recipe's ingredients from an inventory.
	 *
	 * Returns null if not everything could be found.
	 * Otherwise, returns the number of stacks and the total count of items.
	 */
	public static Extraction simulateExtraction(IItemHandler inventory, List<IngredientWithCount> ingredients) {
		IItemHandler frozen = frozenCopy(inventory);
		int numberOfItems = 0;
		int numberOfIngredients = 0;

		for (IngredientWithCount ingredient : ingredients) {
			NonNullList<ItemStack> extracted = extractIngredient(frozen, ingredient.ingredient, ingredient.count, false);

			int numExtracted = extracted.stream().mapToInt(ItemStack::getCount).sum();
			if (numExtracted != ingredient.count)
				return null;

			numberOfIngredients++;
			numberOfItems += numExtracted;
		}

		return new Extraction(numberOfIngredients, numberOfItems);
	}

	public static IngredientWithCount getFirstMissingIngredient(IItemHandler inventory, List<IngredientWithCount> ingredients) {
		IItemHandler frozen = frozenCopy(inventory);

		for (IngredientWithCount ingredient : ingredients) {
			NonNullList<ItemStack> extracted = extractIngredient(frozen, ingredient.ingredient, ingredient.count, false);
			int numExtracted = extracted.stream().mapToInt(ItemStack::getCount).sum();
			if (numExtracted != ingredient.count)
				return new IngredientWithCount(ingredient.ingredient, ingredient.count - numExtracted);
		}

		return null;
	}

	private static List<IngredientWithCount> convertSpiritsToIngredients(List<SpiritWithCount> ingredients) {
		return ingredients.stream()
			.map(ingredient -> new IngredientWithCount(Ingredient.of(ingredient.getItem()), ingredient.count))
			.collect(Collectors.toList());
	}

	/**
	 * Attempts to extract an ingredient from an inventory.
	 */
	public static NonNullList<ItemStack> extractIngredient(IItemHandler inventory, Predicate<ItemStack> ingredient, int count, boolean simulate) {
		int leftToExtract = count;
		NonNullList<ItemStack> extracted = NonNullList.create();
		int[] toExtract = new int[inventory.getSlots()];
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.extractItem(i, leftToExtract, true);
			if (ingredient.test(stack)) {
				extracted.add(stack);
				toExtract[i] = stack.getCount();
				leftToExtract -= stack.getCount();

				if (leftToExtract <= 0) {
					if (!simulate) {
						extracted.clear();
						for (int slot = 0; slot < toExtract.length; slot++) {
							extracted.add(inventory.extractItem(slot, toExtract[slot], false));
						}
					}
					return extracted;
				}
			}
		}

		return extracted;
	}

	public static IngredientWithCount getNextIngredientToTake(SpiritInfusionRecipe recipe, IItemHandlerModifiable consumedItems) {
		IItemHandler frozen = frozenCopy(consumedItems);

		return getFirstMissingIngredient(frozen, recipe.extraItems);
	}

	/**
	 * Creates a copy of every item the inventory will allow us to extract.
	 */
	private static IItemHandlerModifiable frozenCopy(IItemHandler inventory) {
		ItemStackHandler handler = new ItemStackHandler(inventory.getSlots());
		for (int i = 0; i < inventory.getSlots(); i++) {
			handler.setStackInSlot(i, inventory.extractItem(i, 64, true));
		}
		return handler;
	}

	/**
	 * Returns a recipe ranking for a given spirit infusion recipe, or null if the recipe does not match.
	 */
	public static Ranking rankRecipe(SpiritInfusionRecipe recipe, ItemStack inputItem, IItemHandlerModifiable spiritContainer, IItemHandlerModifiable pedestalItems, IItemHandlerModifiable consumedItems) {
		if (!recipe.doesInputMatch(inputItem))
			return null;

		int inputCount = recipe.input.count;

		Extraction spiritRanking = simulateExtraction(spiritContainer, convertSpiritsToIngredients(recipe.spirits));
		if (spiritRanking == null)
			return null;

		Extraction itemRanking = simulateExtraction(new CombinedInvWrapper(pedestalItems, consumedItems), recipe.extraItems);
		if (itemRanking == null)
			return null;

		return new Ranking(inputCount, spiritRanking.numberOfStacks, spiritRanking.numberOfItems, itemRanking.numberOfStacks, itemRanking.numberOfItems);
	}

	public static IItemHandlerModifiable createPedestalInventoryCapture(List<IMalumSpecialItemAccessPoint> pedestals) {
		return new CombinedInvWrapper(pedestals.stream().map(IMalumSpecialItemAccessPoint::getSuppliedInventory).toArray(IItemHandlerModifiable[]::new));
	}

	public static List<IMalumSpecialItemAccessPoint> capturePedestals(Level level, BlockPos pos) {
		return capturePedestals(level, pos, HORIZONTAL_RANGE, VERTICAL_RANGE, HORIZONTAL_RANGE);
	}
	public static List<IMalumSpecialItemAccessPoint> capturePedestals(Level level, BlockPos pos, int xRange, int yRange, int zRange) {
		return BlockHelper.getBlockEntities(IMalumSpecialItemAccessPoint.class, level, pos, xRange, yRange, zRange).stream()
			.sorted(Comparator.comparingDouble(it -> -pos.distSqr(it.getAccessPointBlockPos())))
			.collect(Collectors.toList());
	}
}
