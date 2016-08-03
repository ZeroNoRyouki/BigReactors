package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class BeefGuiIconProgressBar extends BeefGuiVerticalProgressBar {

	public BeefGuiIconProgressBar(BeefGuiBase container, int x, int y) {
		super(container, x, y);
		
	}

	protected abstract ResourceLocation getProgressBarIcon();
	protected abstract ResourceLocation getResourceLocation();
	
	@Override
	protected void drawProgressBar(Tessellator tessellator, TextureManager renderEngine, int barMinX, int barMaxX,
								   int barMinY, int barMaxY, int zLevel) {

		ResourceLocation progressBarIcon = getProgressBarIcon();

		if (progressBarIcon == null)
			return;

		VertexBuffer vertexBuffer = tessellator.getBuffer();
		TextureAtlasSprite sprite = this.guiContainer.mc.getTextureMapBlocks().getAtlasSprite(progressBarIcon.toString());
		double minU = sprite.getMinU();
		double minV = sprite.getMinV();
		double maxU = sprite.getMaxU();
		double maxV = sprite.getMaxV();
		
		renderEngine.bindTexture(getResourceLocation());
		
		// Draw the bar in 16-pixel slices from the bottom up.
		for (int slicedBarY = barMaxY; slicedBarY > 0; slicedBarY -= 16) {

			int slicedBarHeight = (int)Math.min(slicedBarY - barMinY, 16.0f);

			vertexBuffer.begin(GL11.GL_QUADS, vertexBuffer.getVertexFormat());
			vertexBuffer.pos(barMinX, slicedBarY, zLevel).tex(minU, minV + (maxV - minV) * slicedBarHeight / 16.0f).endVertex();
			vertexBuffer.pos(barMaxX, slicedBarY, zLevel).tex(maxU, minV + (maxV - minV) * slicedBarHeight / 16.0f).endVertex();
			vertexBuffer.pos(barMaxX, slicedBarY - slicedBarHeight, zLevel).tex(maxU, minV).endVertex();
			vertexBuffer.pos(barMinX, slicedBarY - slicedBarHeight, zLevel).tex(minU, minV).endVertex();
			tessellator.draw();
		}
	}	
}
