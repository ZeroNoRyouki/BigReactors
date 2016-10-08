package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class GuiIconButton extends GuiButton implements IBeefTooltipControl {

	protected ResourceLocation icon;
	protected String[] tooltip;

	public GuiIconButton(int buttonId, int x, int y, int width, int height) {
		super(buttonId, x, y, width, height, "");
		icon = null;
		tooltip = null;
	}

	public GuiIconButton(int buttonId, int x, int y, int width, int height, ResourceLocation icon) {
		this(buttonId, x, y, width, height);
		this.icon = icon;
		tooltip = null;
	}

	public GuiIconButton(int buttonId, int x, int y, int width, int height, ResourceLocation icon, String[] tooltip) {
		this(buttonId, x, y, width, height, icon);
		this.tooltip = tooltip;
	}

	public void setIcon(ResourceLocation icon) {
		this.icon = icon;
	}

    /**
     * Draws this button to the screen.
     */
	@Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {

        if (this.visible)  {

            // Draw the border

			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition &&
					mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        	this.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height,
					this.getHoverState(this.hovered) == 2 ? 0xFF5555AA : 0xFF000000);
            this.mouseDragged(mc, mouseX, mouseY);

            // Draw the icon

            if (this.icon != null) {

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(this.icon.toString());

				mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

				this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + 1, sprite, this.width - 2, this.height - 2);
            }
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



		return null;
		/*
		if(this.visible) {
			return tooltip;
		}
		else {
			return null;
		}*/
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
}
