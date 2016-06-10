package erogenousbeef.bigreactors.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureMap;
import org.lwjgl.opengl.GL11;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;

public class GuiIconButton extends GuiButton implements IBeefTooltipControl {

	//TODO Commented out IIcon stuff
	//protected IIcon icon;
	
	protected String[] tooltip;


	public GuiIconButton(int buttonId, int x, int y, int width, int height) {
		super(buttonId, x, y, width, height, "");
		//TODO Commented out IIcon stuff
		//icon = null;
		tooltip = null;
	}

	//TODO Commented out IIcon stuff
	public GuiIconButton(int buttonId, int x, int y, int width, int height, /*IIcon*/Object icon) {
		this(buttonId, x, y, width, height);
		//TODO Commented out IIcon stuff
		//this.icon = icon;
		tooltip = null;
	}

	//TODO Commented out IIcon stuff
	public GuiIconButton(int buttonId, int x, int y, int width, int height, /*IIcon*/Object icon, String[] tooltip) {
		this(buttonId, x, y, width, height, icon);
		this.tooltip = tooltip;
	}

	//TODO Commented out IIcon stuff
	public void setIcon(/*IIcon*/Object icon) {
		//this.icon = icon;
	}

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.visible)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            // Draw the border
			// TODO Commented temporarily to allow this thing to compile...
			/*
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            */int k = 0;
            int borderColor = k == 2 ? 0xFF5555AA : 0xFF000000;
        	drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, borderColor);
            
            this.mouseDragged(par1Minecraft, par2, par3);

            // Draw the icon
			//TODO Commented out IIcon stuff
			/*
            if(this.icon != null) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                par1Minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            	this.drawTexturedModelRectFromIcon(this.xPosition + 1, this.yPosition + 1, this.icon, this.width-2, this.height-2);
            }
            */
        }
    }

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		if(mouseX < xPosition || mouseX > xPosition+width || mouseY < yPosition || mouseY > yPosition+height) { return false; }
		return true;
	}

	public void setTooltip(String[] tooltip) {
		this.tooltip = tooltip;  
	}
	
	@Override
	public String[] getTooltip() {
		if(this.visible) {
			return tooltip;
		}
		else {
			return null;
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
}
