package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class BeefGuiTextureProgressBar extends BeefGuiVerticalProgressBar {

	protected double barLeftU = 0.5;
	protected double barRightU = 1;
	
	ResourceLocation barTexture;
	
	public BeefGuiTextureProgressBar(BeefGuiBase container, int x, int y) {
		super(container, x, y);
		
		barLeftU = getBarLeftU();
		barRightU = getBarRightU();
		barTexture = getBarTexture();
	}
	
	protected double getBarLeftU() { return 0.5; }
	protected double getBarRightU() { return 0.9999; }
	protected ResourceLocation getBarTexture() { return this.controlResource; }
	
	@Override
	protected double getBackgroundLeftU() { return 0; }
	
	@Override
	protected double getBackgroundRightU() { return 0.499; }

	
	@Override
	protected void drawProgressBar(Tessellator tessellator,
			TextureManager renderEngine, int barMinX, int barMaxX, int barMinY,
			int barMaxY, int zLevel) {

		double barHeight = Math.round((getProgress() * (this.height-2))) + 2;
		if(getProgress() > 0.00001) { barHeight = Math.max(getBarMinHeight(), barHeight); }

		double barMaxV = 1;
		double barMinV = 1 - Math.min(1, Math.max(0, barHeight / this.height));
		VertexBuffer vertexBuffer = tessellator.getBuffer();

		renderEngine.bindTexture(getBarTexture());
		vertexBuffer.begin(GL11.GL_QUADS, vertexBuffer.getVertexFormat());
		vertexBuffer.pos(this.absoluteX, this.absoluteY + this.height, zLevel).tex(barLeftU, barMaxV).endVertex();
		vertexBuffer.pos(this.absoluteX + this.width, this.absoluteY + this.height, zLevel).tex(barRightU, barMaxV).endVertex();
		vertexBuffer.pos(this.absoluteX + this.width, this.absoluteY + this.height - barHeight, zLevel).tex(barRightU, barMinV).endVertex();
		vertexBuffer.pos(this.absoluteX, this.absoluteY + this.height - barHeight, zLevel).tex(barLeftU, barMinV).endVertex();
		tessellator.draw();
	}

	protected double getBarMinHeight() {
		return 3;
	}
}
