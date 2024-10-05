package com.sammy.malum.common.item.curiosities.weapons.staff;

import com.sammy.malum.common.entity.bolt.AbstractBoltProjectileEntity;
import com.sammy.malum.common.entity.bolt.DrainingBoltEntity;
import com.sammy.malum.registry.client.ParticleRegistry;
import com.sammy.malum.registry.common.MobEffectRegistry;
import com.sammy.malum.registry.common.SoundRegistry;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.helpers.EntityHelper;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry;
import team.lodestar.lodestone.registry.common.tag.LodestoneDamageTypeTags;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.particle.world.behaviors.components.DirectionalBehaviorComponent;

import java.awt.*;

public class ErosionScepterItem extends AbstractStaffItem {

    public static final Color MALIGNANT_PURPLE = new Color(68, 11, 61);
    public static final Color MALIGNANT_BLACK = new Color(12, 4, 11);
    public static final ColorParticleData MALIGNANT_COLOR_DATA = ColorParticleData.create(MALIGNANT_PURPLE, MALIGNANT_BLACK).setEasing(Easing.BOUNCE_IN_OUT).setCoefficient(1.2f).build();

    public ErosionScepterItem(Tier tier, float magicDamage, Properties builderIn) {
        super(tier, 10, magicDamage, builderIn);
    }

    @Override
    public void hurtEvent(LivingHurtEvent event, LivingEntity attacker, LivingEntity target, ItemStack stack) {
        if (!(event.getSource().getDirectEntity() instanceof AbstractBoltProjectileEntity) && !event.getSource().is(LodestoneDamageTypeTags.IS_MAGIC)) {
            MobEffect silenced = MobEffectRegistry.SILENCED.get();
            MobEffectInstance effect = target.getEffect(silenced);
            if (effect == null) {
                target.addEffect(new MobEffectInstance(silenced, 300, 1, true, true, true));
            } else {
                EntityHelper.amplifyEffect(effect, target, 2, 9);
                EntityHelper.extendEffect(effect, target, 60, 600);
            }
            SoundHelper.playSound(target, SoundRegistry.DRAINING_MOTIF.get(), attacker.getSoundSource(), 1, 1.25f);
        }
        super.hurtEvent(event, attacker, target, stack);
    }

    @Override
    public int getCooldownDuration(Level level, LivingEntity livingEntity) {
        return 80;
    }

    @Override
    public int getProjectileCount(Level level, LivingEntity livingEntity, float pct) {
        return pct == 1f ? 2 : 0;
    }

    @Override
    public void fireProjectile(LivingEntity player, ItemStack stack, Level level, InteractionHand hand, float chargePercentage, int count) {
        int spawnDelay = count * 5;
        float pitchOffset = count * 1.5f;
        float velocity = 4f;
        float magicDamage = (float) player.getAttributes().getValue(LodestoneAttributeRegistry.MAGIC_DAMAGE.get()) * 0.3f;
        Vec3 pos = getProjectileSpawnPos(player, hand, 0.5f, 0.5f);
        for (int i = 0; i < 4; i++) {
            float xSpread = RandomHelper.randomBetween(level.random, -0.125f, 0.125f);
            float ySpread = RandomHelper.randomBetween(level.random, -0.025f, 0.025f);
            DrainingBoltEntity entity = new DrainingBoltEntity(level, pos.x, pos.y, pos.z);
            if (i > 1) {
                entity.setSilent(true);
            }
            entity.setData(player, magicDamage, spawnDelay);
            entity.setItem(stack);

            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), -pitchOffset, velocity, 0F);

            Vec3 projectileDirection = entity.getDeltaMovement();
            float yRot = ((float) (Mth.atan2(projectileDirection.x, projectileDirection.z) * (double) (180F / (float) Math.PI)));
            float yaw = (float) Math.toRadians(yRot);
            Vec3 left = new Vec3(-Math.cos(yaw), 0, Math.sin(yaw));
            Vec3 up = left.cross(projectileDirection);
            entity.setDeltaMovement(entity.getDeltaMovement().add(left.scale(xSpread)).add(up.scale(ySpread)));


            level.addFreshEntity(entity);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void spawnChargeParticles(Level pLevel, LivingEntity pLivingEntity, Vec3 pos, ItemStack pStack, float pct) {
        RandomSource random = pLevel.random;
        final SpinParticleData spinData = SpinParticleData.createRandomDirection(random, 0.25f, 0.5f).setSpinOffset(RandomHelper.randomBetween(random, 0f, 6.28f)).build();
        WorldParticleBuilder.create(ParticleRegistry.CIRCLE, new DirectionalBehaviorComponent(pLivingEntity.getLookAngle().normalize()))
                .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                .setTransparencyData(GenericParticleData.create(0.8f * pct, 0f).setEasing(Easing.SINE_IN_OUT, Easing.SINE_IN).build())
                .setSpinData(spinData)
                .setScaleData(GenericParticleData.create(0.3f * pct, 0).setEasing(Easing.SINE_IN_OUT).build())
                .setColorData(ColorParticleData.create(MALIGNANT_BLACK, MALIGNANT_BLACK).build())
                .setLifetime(5)
                .setLifeDelay(2)
                .setMotion(pLivingEntity.getLookAngle().normalize().scale(0.05f))
                .enableNoClip()
                .enableForcedSpawn()
                .setRenderType(LodestoneWorldParticleRenderType.LUMITRANSPARENT)
                .spawn(pLevel, pos.x, pos.y, pos.z);
    }
}
