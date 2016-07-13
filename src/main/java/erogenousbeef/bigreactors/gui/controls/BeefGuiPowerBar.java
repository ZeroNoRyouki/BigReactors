package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.IPowerGenerator;
import erogenousbeef.bigreactors.common.multiblock.PowerSystem;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.util.ResourceLocation;

public class BeefGuiPowerBar extends BeefGuiTextureProgressBar implements IBeefTooltipControl {

	public BeefGuiPowerBar(BeefGuiBase container, int x, int y, IPowerGenerator provider) {

		super(container, x, y);
		this._provider = provider;
	}
	
	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BeefGuiPowerBar.s_barsTextures[this._provider.getPowerSystem().ordinal()];
	}
	
	@Override
	protected float getProgress() {

		double progress = Math.min(1d, Math.max(0d, (double)this._provider.getEnergyStored() / (double)this._provider.getEnergyCapacity()));

		return (float)progress;
	}

	@Override
	public String[] getTooltip() {

		long energyStored = this._provider.getEnergyStored();
		long energyMax = this._provider.getEnergyCapacity();
		/*
		float fullness = (float)energyStored / (float)energyMax * 100f;
		*/
		float fullness = this.getProgress() * 100f;

		return new String[] { "Energy Buffer", 
				String.format("%d / %d %s", energyStored, energyMax, this._provider.getPowerSystem().unitOfMeasure),
				String.format("%2.1f%% full", fullness)
		};
	}

	private IPowerGenerator _provider;

	private static ResourceLocation[] s_barsTextures;

	static {
		s_barsTextures = new ResourceLocation[2];
		s_barsTextures[PowerSystem.RedstoneFlux.ordinal()] = BigReactors.createGuiResourceLocation("controls/energyBarRF.png");
		s_barsTextures[PowerSystem.Tesla.ordinal()] = BigReactors.createGuiResourceLocation("controls/energyBarTesla.png");
	}
}
