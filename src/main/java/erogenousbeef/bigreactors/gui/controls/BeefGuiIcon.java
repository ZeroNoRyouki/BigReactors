package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.gui.BeefGuiControlBase;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class BeefGuiIcon extends BeefGuiControlBase implements IBeefTooltipControl {

	protected ResourceLocation icon;
	protected String[] tooltip;

	public BeefGuiIcon(BeefGuiBase container, int absoluteX, int absoluteY) {
		this(container, absoluteX, absoluteY, 16, 16);
	}

	public BeefGuiIcon(BeefGuiBase container, int absoluteX, int absoluteY, int sizeX, int sizeY, ResourceLocation icon, String[] tooltip) {
		this(container, absoluteX, absoluteY, sizeX, sizeY);
		this.icon = icon;
		this.tooltip = tooltip;
	}

	public BeefGuiIcon(BeefGuiBase container, int absoluteX, int absoluteY,
			int width, int height) {
		super(container, absoluteX, absoluteY, width, height);
		icon = null;
		tooltip = null;
	}

	public void setIcon(ResourceLocation icon) {
		this.icon = icon;
	}
	
	@Override
	public void drawBackground(TextureManager renderEngine, int mouseX, int mouseY) {

		if (!visible || icon == null)
			return;

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		this.guiContainer.drawTexturedModelRectFromIcon(this.absoluteX, this.absoluteY, this.icon, this.width, this.height);
	}

	@Override
	public void drawForeground(TextureManager renderEngine, int mouseX,
			int mouseY) {
	}

	@Override
	public String[] getTooltip() {
		return tooltip;
	}

	public void setTooltip(String[] newTooltip) {
		tooltip = newTooltip;
	}
}
