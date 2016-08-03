package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.gui.BeefGuiControlBase;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class BeefGuiInsertionProgressBar extends BeefGuiControlBase implements IBeefTooltipControl {

	public final static int controlWidth = 20;
	public final static int controlHeight = 64;

	protected ResourceLocation controlResource;

	private double backgroundLeftU = 0;
	private double backgroundRightU = 0.5;
	
	private double rodLeftU = 0.51;
	private double rodRightU = 1;
	
	protected float barAbsoluteMaxHeight;
	protected float insertion = 0f;

	protected String[] tooltip = {
			TextFormatting.AQUA + "Control Rod",
			"",
			"Insertion: XX%"
	};
	
	public BeefGuiInsertionProgressBar(BeefGuiBase container, int x, int y) {
		super(container, x, y, controlWidth, controlHeight);
		
		controlResource = this.getBackgroundTexture();
		barAbsoluteMaxHeight = this.height - 1;
	}
	
	public void setInsertion(float insertion) { this.insertion = Math.min(1f, Math.max(0f, insertion)); }
	
	protected ResourceLocation getBackgroundTexture() {

		if (null == BeefGuiInsertionProgressBar.s_texture)
			BeefGuiInsertionProgressBar.s_texture = BigReactors.createGuiResourceLocation("controls/ControlRod.png");

		return BeefGuiInsertionProgressBar.s_texture;
	}
	
	@Override
	public void drawBackground(TextureManager renderEngine, int mouseX, int mouseY) {
		if(!this.visible) { return; }

		// Draw the background

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
		
		// Draw the rod itself, on top of the background

		if(insertion > 0f) {
			int barHeight = Math.max(1, (int)Math.floor(insertion * barAbsoluteMaxHeight));
			int rodMaxY = this.absoluteY + barHeight;
			
			float rodTopV = 1f - insertion;

			vertexBuffer.begin(GL11.GL_QUADS, vertexBuffer.getVertexFormat());
			vertexBuffer.pos(this.absoluteX, rodMaxY, 2).tex(rodLeftU, 1f).endVertex();
			vertexBuffer.pos(this.absoluteX + this.width, rodMaxY, 2).tex(rodRightU, 1f).endVertex();
			vertexBuffer.pos(this.absoluteX + this.width, this.absoluteY, 2).tex(rodRightU, rodTopV).endVertex();
			vertexBuffer.pos(this.absoluteX, this.absoluteY, 2).tex(rodLeftU, rodTopV).endVertex();
			tessellator.draw();
		}
	}

	@Override
	public void drawForeground(TextureManager renderEngine, int mouseX,
			int mouseY) {
	}

	@Override
	public String[] getTooltip() {
		tooltip[2] = String.format("Insertion: %.0f%%", this.insertion*100f);
		return tooltip;
	}

	private static ResourceLocation s_texture;

}
