package erogenousbeef.bigreactors.gui.controls;

import cofh.api.energy.IEnergyProvider;
import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.util.ResourceLocation;

public class BeefGuiPowerBar extends BeefGuiTextureProgressBar implements
		IBeefTooltipControl {

	IEnergyProvider _entity;
	
	public BeefGuiPowerBar(BeefGuiBase container, int x, int y, IEnergyProvider entity) {
		super(container, x, y);
		_entity = entity;
	}
	
	@Override
	protected ResourceLocation getBackgroundTexture() {

		if (null == s_bgTexture)
			s_bgTexture = BigReactors.createGuiResourceLocation("controls/Energy.png");

		return s_bgTexture;
	}
	
	@Override
	protected float getProgress() {

		float progress = Math.min(1f, Math.max(0f, (float)_entity.getEnergyStored(null) / (float)_entity.getMaxEnergyStored(null)));
		return progress;
	}

	@Override
	public String[] getTooltip() {

		int energyStored = this._entity.getEnergyStored(null);
		int energyMax = this._entity.getMaxEnergyStored(null);
		float fullness = (float)energyStored / (float)energyMax * 100f;
		return new String[] { "Energy Buffer", 
				String.format("%d / %d RF", energyStored, energyMax),
				String.format("%2.1f%% full", fullness)
		};
	}

	private static ResourceLocation s_bgTexture;
}
