package erogenousbeef.bigreactors.gui.controls;

import erogenousbeef.bigreactors.client.gui.BeefGuiBase;
import erogenousbeef.bigreactors.gui.IBeefTooltipControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class GuiSelectableButton extends GuiButton implements IBeefTooltipControl {

	private boolean selected;
	private int selectedColor;
	private ResourceLocation icon;
	
	private BeefGuiBase window;

	public GuiSelectableButton(int id, int x, int y, ResourceLocation icon, int selectedColor, BeefGuiBase containingWindow) {
		super(id, x, y, 24, 24, "");
		selected = false;
		this.icon = icon;
		this.selectedColor = selectedColor;
		window = containingWindow;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isSelected() { return this.selected; }

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {

            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            
            int k = this.getHoverState(this.hovered);
            int borderColor = this.selected ? this.selectedColor : 0xFF000000;
            int bgColor = 0xFF565656; // disabled
            if(k == 1) {
            	bgColor = 0xFF999999; // enabled
            }
            else if(k == 2) {
            	bgColor = 0xFF9999CC; // hovered
            	borderColor = this.selected ? this.selectedColor : 0xFF5555AA;
            }

        	this.drawRect(this.xPosition, this.yPosition, this.xPosition+this.width, this.yPosition+this.height, borderColor);
        	this.drawRect(this.xPosition+1, this.yPosition+1, this.xPosition+this.width-1, this.yPosition+this.height-1, bgColor);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(this.icon.toString());

			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            this.drawTexturedModalRect(this.xPosition+1, this.yPosition+1, sprite, this.width-2, this.height-2);
            this.mouseDragged(mc, mouseX, mouseY);
        }
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		if(mouseX < xPosition || mouseX > xPosition+width || mouseY < yPosition || mouseY > yPosition+height) { return false; }
		return true;
	}

	@Override
	public String[] getTooltip() {
		return new String[] { this.displayString };
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
}
