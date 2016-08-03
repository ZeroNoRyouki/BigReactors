package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.gui.BeefGuiControlBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class BeefGuiVerticalProgressBar extends BeefGuiControlBase {
	private final static int controlWidth = 20;
	private final static int controlHeight = 64;

	protected ResourceLocation controlResource;

	private double backgroundLeftU = 0;
	private double backgroundRightU = 0.32;
	
	private double gradationLeftU = 0.77;
	private double gradationRightU = 1;
	
	protected float barAbsoluteMaxHeight;
	
	public BeefGuiVerticalProgressBar(BeefGuiBase container, int x, int y) {
		super(container, x, y, controlWidth, controlHeight);
		
		this.controlResource = null;
		
		backgroundLeftU = getBackgroundLeftU();
		backgroundRightU = getBackgroundRightU();
		gradationLeftU = getGradationLeftU();
		gradationRightU = getGradationRightU();
		
		barAbsoluteMaxHeight = this.height - 1;
	}
	
	protected boolean drawGradationMarks() { return false; }

	protected abstract ResourceLocation getBackgroundTexture();

	protected abstract float getProgress();
	protected abstract void drawProgressBar(Tessellator tessellator, TextureManager renderEngine, int barMinX, int barMaxX, int barMinY, int barMaxY, int zLevel);
	
	protected double getBackgroundLeftU() { return 0; }
	protected double getBackgroundRightU() { return 0.32; }
	protected double getGradationLeftU() { return 0.77; }
	protected double getGradationRightU() { return 1; } 
	
	@Override
	public void drawBackground(TextureManager renderEngine, int mouseX, int mouseY) {
		if(!this.visible) { return; }

		// Draw the background

		if (null == this.controlResource)
			this.controlResource = this.getBackgroundTexture();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		renderEngine.bindTexture(controlResource);
		vertexBuffer.begin(GL11.GL_QUADS, vertexBuffer.getVertexFormat());
		vertexBuffer.pos(this.absoluteX, this.absoluteY + this.height, 0).tex(backgroundLeftU, 1.0).endVertex();
		vertexBuffer.pos(this.absoluteX + this.width, this.absoluteY + this.height, 0).tex(backgroundRightU, 1.0).endVertex();
		vertexBuffer.pos(this.absoluteX + this.width, this.absoluteY, 0).tex(backgroundRightU, 0).endVertex();
		vertexBuffer.pos(this.absoluteX, this.absoluteY, 0).tex(backgroundLeftU, 0).endVertex();
		tessellator.draw();

		float progress = getProgress();
		// Draw the bar itself, on top of the background
		if(progress > 0.0) {
			int barHeight = Math.max(1, (int)Math.floor(progress * barAbsoluteMaxHeight));
			int barMinX = this.absoluteX + 1;
			int barMaxX = this.absoluteX + this.width - 4;
			int barMinY = this.absoluteY + this.height - barHeight;
			int barMaxY = this.absoluteY + this.height - 1;
			
			this.drawProgressBar(tessellator, renderEngine, barMinX, barMaxX, barMinY, barMaxY, 1);
		}
		
		if(drawGradationMarks()) {

			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			renderEngine.bindTexture(controlResource);
			vertexBuffer.begin(GL11.GL_QUADS, vertexBuffer.getVertexFormat());
			vertexBuffer.pos(this.absoluteX, this.absoluteY + this.height, 2).tex(gradationLeftU, 1.0).endVertex();
			vertexBuffer.pos(this.absoluteX + this.width - 4, this.absoluteY + this.height, 2).tex(gradationRightU, 1.0).endVertex();
			vertexBuffer.pos(this.absoluteX + this.width - 4, this.absoluteY, 2).tex(gradationRightU, 0).endVertex();
			vertexBuffer.pos(this.absoluteX, this.absoluteY, 2).tex(gradationLeftU, 0).endVertex();
			tessellator.draw();
		}
	}

	@Override
	public void drawForeground(TextureManager renderEngine, int mouseX, int mouseY) {
		
	}
}
