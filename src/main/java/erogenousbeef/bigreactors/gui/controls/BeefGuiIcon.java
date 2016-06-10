package erogenousbeef.bigreactors.gui.controls;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import org.lwjgl.opengl.GL11;
import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.gui.BeefGuiControlBase;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;

public class BeefGuiIcon extends BeefGuiControlBase implements IBeefTooltipControl {

	// TODO Commented out IIcon stuff
	//protected IIcon icon;
	protected String[] tooltip;

	public BeefGuiIcon(BeefGuiBase container, int absoluteX, int absoluteY) {
		this(container, absoluteX, absoluteY, 16, 16);
	}

	// TODO Commented out IIcon stuff
	public BeefGuiIcon(BeefGuiBase container, int absoluteX, int absoluteY, int sizeX, int sizeY, /*IIcon*/Object icon, String[] tooltip) {
		this(container, absoluteX, absoluteY, sizeX, sizeY);
		// TODO Commented out IIcon stuff
		//this.icon = icon;
		this.tooltip = tooltip;
	}

	
	public BeefGuiIcon(BeefGuiBase container, int absoluteX, int absoluteY,
			int width, int height) {
		super(container, absoluteX, absoluteY, width, height);
		// TODO Commented out IIcon stuff
		//icon = null;
		tooltip = null;
	}

	// TODO Commented out IIcon stuff
	public void setIcon(/*IIcon*/Object icon) {
		//this.icon = icon;
	}
	
	@Override
	public void drawBackground(TextureManager renderEngine, int mouseX,
			int mouseY) {
		if(!visible) { return;
		}

		// TODO Commented out IIcon stuff
		/*
		if(icon == null) { return; }
		

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        renderEngine.bindTexture(TextureMap.locationBlocksTexture);
    	drawTexturedModelRectFromIcon(this.absoluteX, this.absoluteY, this.icon, this.width, this.height);
    	*/
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
