package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.interfaces.IReactorFuelInfo;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class BeefGuiFuelMixBar extends BeefGuiVerticalProgressBar implements
		IBeefTooltipControl {

	IReactorFuelInfo entity;

	protected double fuelLeftU = 0.25;
	protected double fuelRightU = 0.4999;
	protected double wasteLeftU = 0.5;
	protected double wasteRightU = 0.7499;
	
	public BeefGuiFuelMixBar(BeefGuiBase container, int x, int y, IReactorFuelInfo entity) {
		super(container, x, y);
		this.entity = entity;
	}

	@Override
	protected double getBackgroundLeftU() { return 0; }
	
	@Override
	protected double getBackgroundRightU() { return 0.2499; }

	@Override
	protected ResourceLocation getBackgroundTexture() {

		if (null == s_bgTexture)
			s_bgTexture = BigReactors.createGuiResourceLocation("controls/FuelMixBar.png");

		return s_bgTexture;
	}

	private final static double maxV = 63.0/64.0;
	private final static double minV = 1.0/64.0;

	@Override
	protected void drawProgressBar(Tessellator tessellator,
			TextureManager renderEngine, int barMinX, int barMaxX, int barMinY,
			int barMaxY, int zLevel) {

		VertexBuffer vertexbuffer = tessellator.getBuffer();
		int barMaxHeight = this.height - 1;
		int barHeight = Math.max(1, Math.round(getProgress() * barMaxHeight));

		double fullness = (double)(entity.getFuelAmount() + entity.getWasteAmount()) / (double)entity.getCapacity();
		double fuelProportion = (double)entity.getFuelAmount() / (double)(entity.getFuelAmount() + entity.getWasteAmount());
		double wasteProportion = (double)entity.getWasteAmount() / (double)(entity.getFuelAmount() + entity.getWasteAmount());

		renderEngine.bindTexture(controlResource);
		if(fuelProportion > 0) {
			double fuelMinV = 1.0 - fullness*maxV;
			double fuelMaxV = maxV;

			vertexbuffer.begin(GL11.GL_QUADS, vertexbuffer.getVertexFormat());
			vertexbuffer.pos(this.absoluteX, this.absoluteY + this.height - 1, zLevel).tex(fuelLeftU, fuelMaxV).endVertex();
			vertexbuffer.pos(this.absoluteX + this.width, this.absoluteY + this.height - 1, zLevel).tex(fuelRightU, fuelMaxV).endVertex();
			vertexbuffer.pos(this.absoluteX + this.width, this.absoluteY + this.height - barHeight, zLevel).tex(fuelRightU, fuelMinV).endVertex();
			vertexbuffer.pos(this.absoluteX, this.absoluteY + this.height - barHeight, zLevel).tex(fuelLeftU, fuelMinV).endVertex();
			tessellator.draw();
		}
		
		if(wasteProportion > 0) {
			double wasteMinV = 1.0 - fullness * wasteProportion * maxV;
			double wasteMaxV = maxV;
			double wasteHeight = Math.round(barHeight * wasteProportion);
			
			if(wasteHeight > 0) {
				double wasteTop = this.absoluteY + this.height - 1 - wasteHeight;

				vertexbuffer.begin(GL11.GL_QUADS, vertexbuffer.getVertexFormat());
				vertexbuffer.pos(this.absoluteX, this.absoluteY + this.height - 1, zLevel + 1).tex(wasteLeftU, wasteMaxV).endVertex();
				vertexbuffer.pos(this.absoluteX + this.width, this.absoluteY + this.height - 1, zLevel + 1).tex(wasteRightU, wasteMaxV).endVertex();
				vertexbuffer.pos(this.absoluteX + this.width, wasteTop, zLevel + 1).tex(wasteRightU, wasteMinV).endVertex();
				vertexbuffer.pos(this.absoluteX, wasteTop, zLevel + 1).tex(wasteLeftU, wasteMinV).endVertex();
				tessellator.draw();
			}
		}
	}

	@Override
	public String[] getTooltip() {
		float fullness = getProgress() * 100f;
		float depletion;
		String fuelString, wasteString;
		fuelString = wasteString = "Empty";

		if(entity.getFuelAmount() + entity.getWasteAmount() == 0) {
			depletion = 0f;
		}
		else {
			depletion = ((float)entity.getWasteAmount() / (float)(entity.getFuelAmount() + entity.getWasteAmount())) * 100f;
			
			if(entity.getFuelAmount() > 0) {
				fuelString = Integer.toString(entity.getFuelAmount()) + " mB";
			}

			if(entity.getWasteAmount() > 0) {
				wasteString = Integer.toString(entity.getWasteAmount()) + " mB";
				
			}
		}
		return new String[] {
				TextFormatting.AQUA + "Core Fuel Status",
				String.format(" %2.1f%% full", fullness),
				String.format(" %2.1f%% depleted", depletion),
				"",
				String.format("Fuel Rods: %d", entity.getFuelRodCount()),
				String.format("Max Capacity: %d mB", entity.getCapacity()),
				String.format("Fuel: %s", fuelString),
				String.format("Waste: %s", wasteString),
				String.format("Total: %d mB", entity.getFuelAmount() + entity.getWasteAmount())
		};
	}

	@Override
	protected float getProgress() {
		return (float)(entity.getFuelAmount() + entity.getWasteAmount()) / (float)entity.getCapacity();
	}

	private static ResourceLocation s_bgTexture;
}
