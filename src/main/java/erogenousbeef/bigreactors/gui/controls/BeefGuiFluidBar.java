package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class BeefGuiFluidBar extends BeefGuiIconProgressBar implements IBeefTooltipControl {

	public BeefGuiFluidBar(BeefGuiBase container, int x, int y, IFluidHandler handlerInfo) {

		super(container, x, y);
		this._fluidInfo = handlerInfo;
	}

	@Override
	protected ResourceLocation getProgressBarIcon() {

		IFluidTankProperties properties = this.getTankProperties();
		FluidStack stack = null != properties ? properties.getContents() : null;
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

		IFluidTankProperties properties = this.getTankProperties();

		if (null != properties) {

			FluidStack stack = properties.getContents();

			if (null != stack)
				return (float)stack.amount / (float)properties.getCapacity();
		}

		return 0.0f;
	}
	
	@Override
	public String[] getTooltip() {

		if (!this.visible)
			return null;

		IFluidTankProperties properties = this.getTankProperties();

		if (null != properties) {

			FluidStack stack = properties.getContents();
			String fluidName;
			int amount, capacity = properties.getCapacity();

			if (null != stack) {

				fluidName = stack.getFluid().getLocalizedName(stack);
				amount = stack.amount;

			} else {

				amount = 0;
				fluidName = "Empty";
			}

			return new String[] { fluidName, String.format("%d / %d mB", amount, capacity) };
		}

		return null;
	}

	@Override
	protected ResourceLocation getResourceLocation() {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
	
	@Override
	protected boolean drawGradationMarks() { return true; }

	@Nullable
	private IFluidTankProperties getTankProperties() {

		IFluidTankProperties[] properties = this._fluidInfo.getTankProperties();

		return null != properties && properties.length > 0 ? properties[0] : null;
	}

	private IFluidHandler _fluidInfo;
	private static ResourceLocation s_bgTexture;
}