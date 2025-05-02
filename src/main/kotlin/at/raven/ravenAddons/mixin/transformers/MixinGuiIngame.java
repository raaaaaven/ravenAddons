package at.raven.ravenAddons.mixin.transformers;

import at.raven.ravenAddons.utils.TitleManager;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame {

    @Shadow
    protected int titlesTimer;
    @Unique
    private int ravenAddons$storedTitlesTimer = 0;

    @Inject(method = "renderGameOverlay", at = @At("HEAD"))
    private void beforeRenderGameOverlay(float partialTicks, CallbackInfo ci) {
        if (TitleManager.shouldHideVanillaTitle()) {
            ravenAddons$storedTitlesTimer = titlesTimer;
            titlesTimer = 0;
        }
    }

    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    private void afterRenderGameOverlay(float partialTicks, CallbackInfo ci) {
        if (ravenAddons$storedTitlesTimer != 0) {
            titlesTimer = ravenAddons$storedTitlesTimer;
            ravenAddons$storedTitlesTimer = 0;
        }
    }
}