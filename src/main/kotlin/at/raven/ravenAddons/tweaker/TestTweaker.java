package at.raven.ravenAddons.tweaker;

import java.io.File;
import java.util.List;
import kotlin.Suppress;
import net.hypixel.modapi.tweaker.HypixelModAPITweaker;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinTweaker;

public class TestTweaker implements ITweaker {
    @Override
    @SuppressWarnings("unchecked")
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
        tweakClasses.add(MixinTweaker.class.getName());
        tweakClasses.add(HypixelModAPITweaker.class.getName());
        tweakClasses.add(ModLoadingTweaker.class.getName());
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {

    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
