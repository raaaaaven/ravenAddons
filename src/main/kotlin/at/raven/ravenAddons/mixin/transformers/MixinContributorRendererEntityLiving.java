package at.raven.ravenAddons.mixin.transformers;

import at.raven.ravenAddons.mixin.hooks.RendererLivingEntityHook;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RendererLivingEntity.class, priority = 500)
public class MixinContributorRendererEntityLiving<T extends EntityLivingBase> {

    @ModifyVariable(
        method = "rotateCorpse",
        at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.BEFORE)
    )
    private String checkNameForUpsideDown(String displayName) {
        if (RendererLivingEntityHook.shouldBeUpsideDown(displayName))
            return "Grumm";
        return displayName;
    }}