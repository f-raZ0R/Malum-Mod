package com.sammy.malum.client.screen.codex.pages.recipe.vanilla;

import com.sammy.malum.*;
import com.sammy.malum.client.screen.codex.pages.*;
import com.sammy.malum.client.screen.codex.screens.*;
import net.minecraft.client.gui.*;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.*;

import java.util.*;

import static com.sammy.malum.client.screen.codex.ArcanaCodexHelper.*;

public class CraftingPage extends BookPage {
    private final ItemStack outputStack;
    private final List<ItemStack> inputStacks;

    public CraftingPage(ItemStack outputStack, List<ItemStack> inputStacks) {
        super(MalumMod.malumPath("textures/gui/book/pages/crafting_page.png"));
        this.outputStack = outputStack;
        this.inputStacks = inputStacks;
    }

    public CraftingPage(ItemStack outputStack, ItemStack... inputStacks) {
        this(outputStack, List.of(inputStacks));
    }

    public CraftingPage(Item outputItem, Item... inputItems) {
        this(outputItem.getDefaultInstance(), inputItems);
    }

    public CraftingPage(ItemStack outputStack, Item... inputItems) {
        this(outputStack, Arrays.stream(inputItems).map(Item::getDefaultInstance).toList());
    }

    @Override
    public void render(EntryScreen screen, GuiGraphics guiGraphics, int left, int top, int mouseX, int mouseY, float partialTicks, boolean isRepeat) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                if (inputStacks.size() > index) {
                    final ItemStack stack = inputStacks.get(index);
                    if (!stack.isEmpty()) {
                        int itemPosX = left + 41 + j * 22;
                        int itemPosY = top + 34 + i * 22;
                        renderItem(screen, guiGraphics, stack, itemPosX, itemPosY, mouseX, mouseY);
                    }
                }
            }
        }

        renderItem(screen, guiGraphics, outputStack, left + 63, top + 126, mouseX, mouseY);
    }

    public static CraftingPage fullPage(Item output, Item input) {
        return fullPage(output.getDefaultInstance(), input.getDefaultInstance());
    }

    public static CraftingPage fullPage(ItemStack output, ItemStack input) {
        return new CraftingPage(output, input, input, input, input, input, input, input, input, input);
    }

    public static CraftingPage scythePage(Item scythe, Item metal, Item reagent) {
        return scythePage(scythe.getDefaultInstance(), metal.getDefaultInstance(), reagent.getDefaultInstance());
    }

    public static CraftingPage scythePage(ItemStack scythe, ItemStack metal, ItemStack reagent) {
        ItemStack stick = Items.STICK.getDefaultInstance();
        ItemStack empty = Items.AIR.getDefaultInstance();
        return new CraftingPage(scythe, metal, metal, reagent, empty, stick, metal, stick, empty, empty);
    }

    public static CraftingPage broochPage(Item brooch, Item ingot, Item block) {
        return broochPage(brooch.getDefaultInstance(), Items.LEATHER.getDefaultInstance(), ingot.getDefaultInstance(), block.getDefaultInstance());
    }

    public static CraftingPage broochPage(ItemStack brooch, ItemStack material, ItemStack ingot, ItemStack block) {
        ItemStack empty = Items.AIR.getDefaultInstance();
        return new CraftingPage(brooch, empty, material, empty, material, ingot, material, empty, block, empty);
    }

    public static CraftingPage ringPage(Item ring, Item reagent) {
        return ringPage(ring.getDefaultInstance(), Items.LEATHER.getDefaultInstance(), reagent.getDefaultInstance());
    }

    public static CraftingPage ringPage(ItemStack ring, ItemStack material, ItemStack reagent) {
        ItemStack empty = Items.AIR.getDefaultInstance();
        return new CraftingPage(ring, reagent, material, empty, material, empty, material, empty, material, empty);
    }

    public static CraftingPage itemPedestalPage(Item pedestal, Item fullBlock, Item slab) {
        return itemPedestalPage(pedestal.getDefaultInstance(), fullBlock.getDefaultInstance(), slab.getDefaultInstance());
    }

    public static CraftingPage itemPedestalPage(ItemStack pedestal, ItemStack fullBlock, ItemStack slab) {
        ItemStack empty = Items.AIR.getDefaultInstance();
        return new CraftingPage(pedestal, slab, slab, slab, empty, fullBlock, empty, slab, slab, slab);
    }

    public static CraftingPage itemStandPage(Item stand, Item fullBlock, Item slab) {
        return itemStandPage(stand.getDefaultInstance(), fullBlock.getDefaultInstance(), slab.getDefaultInstance());
    }

    public static CraftingPage itemStandPage(ItemStack stand, ItemStack fullBlock, ItemStack slab) {
        ItemStack empty = Items.AIR.getDefaultInstance();
        return new CraftingPage(stand, empty, empty, empty, slab, slab, slab, fullBlock, fullBlock, fullBlock);
    }

    public static CraftingPage toolPage(Item tool, Item metal) {
        return toolPage(tool.getDefaultInstance(), metal.getDefaultInstance());
    }

    public static CraftingPage toolPage(ItemStack tool, ItemStack metal) {
        ItemStack stick = Items.STICK.getDefaultInstance();
        ItemStack empty = Items.AIR.getDefaultInstance();
        if (tool.getItem() instanceof SwordItem) {
            return new CraftingPage(tool, empty, metal, empty, empty, metal, empty, empty, stick, empty);
        }
        if (tool.getItem() instanceof AxeItem) {
            return new CraftingPage(tool, metal, metal, empty, metal, stick, empty, empty, stick, empty);
        }
        if (tool.getItem() instanceof HoeItem) {
            return new CraftingPage(tool, metal, metal, empty, empty, stick, empty, empty, stick, empty);
        }
        if (tool.getItem() instanceof ShovelItem) {
            return new CraftingPage(tool, empty, metal, empty, empty, stick, empty, empty, stick, empty);
        }
        if (tool.getItem() instanceof PickaxeItem) {
            return new CraftingPage(tool, metal, metal, metal, empty, stick, empty, empty, stick, empty);
        }
        return null;
    }

    public static CraftingPage knifePage(Item tool, Item metal) {
        return knifePage(tool.getDefaultInstance(), metal.getDefaultInstance());
    }

    public static CraftingPage knifePage(ItemStack tool, ItemStack metal) {
        ItemStack stick = Items.STICK.getDefaultInstance();
        ItemStack empty = Items.AIR.getDefaultInstance();
        return new CraftingPage(tool, empty, empty, empty, empty, metal, empty, stick, empty) {
            @Override
            public boolean isValid() {
                return ModList.get().isLoaded("farmersdelight");
            }
        };
    }
}