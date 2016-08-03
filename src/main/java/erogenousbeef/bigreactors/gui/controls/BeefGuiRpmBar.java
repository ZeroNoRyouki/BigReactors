package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class BeefGuiRpmBar extends BeefGuiTextureProgressBar implements
		IBeefTooltipControl {

	MultiblockTurbine turbine;
	String[] tooltip;
	
	public BeefGuiRpmBar(BeefGuiBase container, int x, int y, MultiblockTurbine turbine, String tooltipTitle, String[] extraTooltip) {
		super(container, x, y);
		tooltip = null;
		this.turbine = turbine;
		
		if(extraTooltip == null || extraTooltip.length <= 0) {
			tooltip = new String[2];
		}
		else {
			tooltip = new String[3 + extraTooltip.length];
			tooltip[2] = "";
			for(int i = 0; i < extraTooltip.length; i++) {
				tooltip[i+3] = extraTooltip[i];
			}
		}
		tooltip[0] = TextFormatting.AQUA + tooltipTitle;
		tooltip[1] = "";
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {

		if (null == s_bgTexture)
			s_bgTexture = BigReactors.createGuiResourceLocation("controls/RpmBar.png");

		return s_bgTexture;
	}
	
	@Override
	public String[] getTooltip() {
		if(turbine != null) {
			tooltip[1] = String.format("  %.0f RPM", turbine.getRotorSpeed());
		}
		else {
			tooltip[1] = "  0 RPM";
		}

		return tooltip;
	}

	@Override
	protected float getProgress() {
		if(turbine == null) { return 0f; }
		
		return Math.min(1f, turbine.getRotorSpeed() / (turbine.getMaxRotorSpeed()*1.1f)); // Give a little extra warning
	}

	private static ResourceLocation s_bgTexture;
}
