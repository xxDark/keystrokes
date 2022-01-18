package dev.xdark.keystrokes.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import static dev.xdark.keystrokes.button.RenderConstants.GUI_COMPONENT_HEIGHT;
import static dev.xdark.keystrokes.button.RenderConstants.GUI_COMPONENT_WIDTH;

public final class FlattenButton extends GuiButton {

	private final FontRenderer fontRenderer;

	public FlattenButton(int buttonId, FontRenderer fontRenderer, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.fontRenderer = fontRenderer;
	}

	public FlattenButton(int buttonId, FontRenderer fontRenderer, int x, int y, String buttonText) {
		this(buttonId, fontRenderer, x, y, GUI_COMPONENT_WIDTH, GUI_COMPONENT_HEIGHT, buttonText);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			int x = this.x;
			int y = this.y;
			int width = this.width;
			int height = this.height;
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			Gui.drawRect(x, y, x + width, y + height, 0x88000000);
			mouseDragged(mc, mouseX, mouseY);
			int j = hovered ? 16777120 : 14737632;

			drawCenteredString(fontRenderer, displayString, x + width / 2, y + (height - 8) / 2, j);
		}
	}
}
