package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.interfaces.IMultipleFluidHandler;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class BeefGuiFluidBar extends BeefGuiIconProgressBar implements
		IBeefTooltipControl {

	IMultipleFluidHandler _entity;
	int tankIdx;
	
	public BeefGuiFluidBar(BeefGuiBase container, int x, int y,
			IMultipleFluidHandler entity, int tankIdx) {
		super(container, x, y);
		
		this._entity = entity;
		this.tankIdx = tankIdx;
	}

	@Override
	protected ResourceLocation getProgressBarIcon() {

		FluidTankInfo[] tanks = this._entity.getTankInfo();
		FluidStack stack = null != tanks && tankIdx < tanks.length ? tanks[tankIdx].fluid : null;
		Fluid fluid = null != stack ? stack.getFluid() : null;

		return null != fluid ? fluid.getStill(stack) : null;
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {

		if (null == s_bgTexture)
			s_bgTexture = BigReactors.createGuiResourceLocation("controls/FluidTank.png");

		return s_bgTexture;
	}

	@Override
	protected float getProgress() {

		FluidTankInfo[] tanks = this._entity.getTankInfo();
		if(tanks != null && tankIdx < tanks.length) {
			FluidStack tankFluid = tanks[tankIdx].fluid;
			if(tankFluid != null) {
				return (float)tankFluid.amount / (float)tanks[tankIdx].capacity;
			}
		}
		return 0.0f;
	}
	
	@Override
	public String[] getTooltip() {
		if(!visible) { return null; }

		FluidTankInfo[] tanks = this._entity.getTankInfo();
		if(tanks != null && tankIdx < tanks.length) {
			FluidStack tankFluid = tanks[tankIdx].fluid;
			if(tankFluid != null) {
				String fluidName = tankFluid.getFluid().getLocalizedName(tankFluid);
				if(tankFluid.getFluid() == FluidRegistry.WATER) {
					fluidName = "Water";
				}
				else if(tankFluid.getFluid() == FluidRegistry.LAVA) {
					fluidName = "Lava";
				}

				return new String[] { fluidName, String.format("%d / %d mB", tankFluid.amount, tanks[tankIdx].capacity) };
			}
			else {
				return new String[] { "Empty", String.format("0 / %d mB", tanks[tankIdx].capacity) };
			}
		}
		return null;
	}

	@Override
	protected ResourceLocation getResourceLocation() {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
	
	@Override
	protected boolean drawGradationMarks() { return true; }

	private static ResourceLocation s_bgTexture;
}
