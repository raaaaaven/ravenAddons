package at.raven.ravenAddons.tweaker;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.spongepowered.asm.launch.MixinBootstrap;

public class ModLoadingTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        URL location = ModLoadingTweaker.class.getProtectionDomain().getCodeSource().getLocation();
        if (location == null) return;
        if (!"file".equals(location.getProtocol())) return;
        try {
            MixinBootstrap.getPlatform().addContainer(location.toURI());
            String file = new File(location.toURI()).getName();
            CoreModManager.getIgnoredMods().remove(file);
            CoreModManager.getReparseableCoremods().add(file);
        } catch (URISyntaxException e) {
            System.err.println("Failed to re-add itself as mod.");
            e.printStackTrace();
        }
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
