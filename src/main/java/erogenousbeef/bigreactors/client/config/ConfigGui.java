package erogenousbeef.bigreactors.client.config;

import erogenousbeef.bigreactors.common.BigReactors;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parent) {
        super(parent, BigReactors.CONFIG.getConfigElements(), BigReactors.MODID, false, false,
                GuiConfig.getAbridgedConfigPath(BigReactors.CONFIG.toString()));
    }
}
