package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.util.ResourceLocation;

public class BeefGuiHeatBar extends BeefGuiTextureProgressBar implements
		IBeefTooltipControl {

	private float heatMax = 2000f;
	private float heat;
	
	private String[] tooltip;

	public BeefGuiHeatBar(BeefGuiBase container, int x, int y, String tooltipTitle, String[] tooltipExtra) {
		super(container, x, y);
		heat = 0f;
		
		if(tooltipExtra == null) {
			tooltip = new String[3];
		}
		else {
			tooltip = new String[3 + tooltipExtra.length];
			for(int i = 0; i < tooltipExtra.length; i++) {
				tooltip[i+3] = tooltipExtra[i];
			}
		}

		tooltip[0] = tooltipTitle;
		tooltip[1] = "";
		tooltip[2] = "";
	}

	public void setHeat(float newHeat) {
		heat = newHeat;
		tooltip[1] = String.format("  %.0f C", newHeat);
	}
	
	@Override
	protected ResourceLocation getBackgroundTexture() {

		if (null == s_bgTexture)
			s_bgTexture = BigReactors.createGuiResourceLocation("controls/HeatBar.png");

		return s_bgTexture;
	}
	
	@Override
	protected float getProgress() {
		return Math.min(1, Math.max(0, heat / heatMax));
	}

	@Override
	public String[] getTooltip() {
		return tooltip;
	}

	private static ResourceLocation s_bgTexture;
}
