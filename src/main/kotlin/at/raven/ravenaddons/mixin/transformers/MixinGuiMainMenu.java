package at.raven.ravenaddons.mixin.transformers;

import at.raven.ravenaddons.event.GameLoadEvent;
import at.raven.ravenaddons.utils.EventUtils;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {
    @Inject(method = "initGui", at = @At("HEAD"))
    public void onInitGui(CallbackInfo ci) {
        Event event = new GameLoadEvent();
        EventUtils.INSTANCE.post(event);
    }
}
