package dev.xdark.keystrokes.ui;

import dev.xdark.keystrokes.button.RenderConstants;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public final class FlattenTextField extends GuiTextField {

	public FlattenTextField(int componentId, FontRenderer fontRenderer, int x, int y, int width, int height) {
		super(componentId, fontRenderer, x, y, width, height);
		setEnableBackgroundDrawing(false);
	}

	public FlattenTextField(int componentId, FontRenderer fontRenderer, int x, int y) {
		this(componentId, fontRenderer, x, y, RenderConstants.GUI_COMPONENT_WIDTH, RenderConstants.GUI_COMPONENT_HEIGHT);
	}

	@Override
	public void drawTextBox() {
		if (getVisible()) {
			int x = this.x;
			int y = this.y;
			int height = this.height;
			drawRect(x, y, x + width, y + height, 0x88000000);
			// Dirty hack to draw the text in a correct place
			// see enableBackgroundDrawing checks
			this.x = x + 4;
			this.y = y + (height - 8) / 2;
			super.drawTextBox();
			this.x = x;
			this.y = y;
		}
	}
}
