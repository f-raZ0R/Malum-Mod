package com.sammy.malum.common.item.curiosities.curios.runes.miracle;

import com.sammy.malum.common.item.curiosities.curios.runes.*;
import com.sammy.malum.registry.common.SpiritTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import team.lodestar.lodestone.systems.item.IEventResponderItem;

import java.util.function.Consumer;

public class RuneVolatileDistortionItem extends AbstractRuneCurioItem implements IEventResponderItem {

    public RuneVolatileDistortionItem(Properties builder) {
        super(builder, SpiritTypeRegistry.ELDRITCH_SPIRIT);
    }

    @Override
    public void addExtraTooltipLines(Consumer<Component> consumer) {
        consumer.accept(positiveEffect("erratic_damage"));
        consumer.accept(positiveEffect("crits"));
    }

    @Override
    public void hurtEvent(LivingHurtEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        final RandomSource random = attacker.getRandom();
        float multiplier = Mth.nextFloat(random, 0.9f, 1.2f);
        if (random.nextFloat() < 0.1f) {
            multiplier *= 2;
        }
        event.setAmount(event.getAmount() * multiplier);
    }
}
