package com.sammy.malum.common.item.food;

import com.sammy.malum.common.entity.thrown.*;
import net.minecraft.stats.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;

public class ThrownConcentratedGluttonyItem extends Item {
    public ThrownConcentratedGluttonyItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            var thrownGluttony = new ThrownConcentratedGluttony(pLevel, pPlayer);
            thrownGluttony.setItem(itemstack);
            thrownGluttony.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0F, 0.5F, 1.0F);
            pLevel.addFreshEntity(thrownGluttony);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }
}
