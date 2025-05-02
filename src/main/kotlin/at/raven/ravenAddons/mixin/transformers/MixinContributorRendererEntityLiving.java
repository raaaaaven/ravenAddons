package at.raven.ravenAddons.mixin.transformers;

import at.raven.ravenAddons.mixin.hooks.RendererLivingEntityHook;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RendererLivingEntity.class, priority = 500)
public class MixinContributorRendererEntityLiving {

    @ModifyVariable(
        method = "rotateCorpse",
        at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", ordinal = 0)
    )
    private String checkNameForUpsideDown(String displayName) {
        if (RendererLivingEntityHook.shouldBeUpsideDown(displayName))
            return "Grumm";
        return displayName;
    }}