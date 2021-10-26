package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.MarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	@Deprecated
	@Inject(method = "createAttributes", at = @At(value = "RETURN"))
	private static void biomancy_onRegisterAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("ATTRIBUTE INJECTION"), "adding attack distance attribute to player...");
		cir.getReturnValue().add(ModAttributes.getAttackDistanceModifier());
	}

	@Deprecated
	@Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;distanceToSqr(Lnet/minecraft/entity/Entity;)D"))
	protected double biomancy_transformSweepDistSq(PlayerEntity playerEntity, Entity entityIn) {
		double distSq = playerEntity.distanceToSqr(entityIn);
		double maxDist = ModAttributes.getCombinedReachDistance(playerEntity);
		if (distSq < maxDist * maxDist) {
			return distSq < 9d ? distSq : 8.99d; //hack to allow sweep attacks with attack distance greater than 3/4 ?
		}
		return distSq;
	}

	//replaced by AttackHandler.onLivingHurt(LivingHurtEvent)
//	@Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getDamageBonus(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/CreatureAttribute;)F"))
//	protected float biomancy_transformExtraDamageModifier(ItemStack stack, CreatureAttribute creatureAttribute, Entity targetEntity) {
//		if (!stack.isEmpty()) {
//			float modifier = 0f;
//			if (AttunedDamageEnchantment.isAttuned(stack))
//				modifier = ModEnchantments.ATTUNED_BANE.get().getAttackDamageModifier(stack, (PlayerEntity) (Object) this, targetEntity);
//			if (stack.getItem() == ModItems.FLESHBORN_GUAN_DAO.get())
//				modifier += FleshbornGuanDaoItem.getAttackDamageModifier(stack, (PlayerEntity) (Object) this, targetEntity);
//
//			return EnchantmentHelper.getDamageBonus(stack, creatureAttribute) + modifier;
//		}
//		else {
//			return EnchantmentHelper.getDamageBonus(stack, creatureAttribute);
//		}
//	}

}
