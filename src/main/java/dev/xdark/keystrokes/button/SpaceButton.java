package dev.xdark.keystrokes.button;

import dev.xdark.keystrokes.util.Vec2i;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;

import static dev.xdark.keystrokes.button.RenderConstants.*;

public final class SpaceButton extends Button {

	public SpaceButton(KeyBinding key) {
		super(key);
	}

	@Override
	public void render(FontRenderer fontRenderer, int x, int y) {
		int endX = x + SPACE_WIDTH, endY = y + SPACE_HEIGHT;
		backgroundRenderer.render(x, y, endX, endY);
		setupRender(fontRenderer);
		y += SPACE_HEIGHT / 2;
		Gui.drawRect(x + SPACE_OFFSET_X, y - 1, endX - SPACE_OFFSET_X, y + 1, selectColor());
	}

	@Override
	public boolean isInBounds(int x, int y, int mouseX, int mouseY) {
		Vec2i pos = normalizePosition(x, y);
		x = pos.getX();
		y = pos.getY();
		return mouseX >= x && mouseX <= x + SPACE_WIDTH && mouseY >= y && mouseY <= y + SPACE_HEIGHT;
	}

	@Override
	protected String computeRenderName() {
		return "SPACE";
	}

	@Override
	public Vec2i normalizePosition(int x, int y) {
		return new Vec2i(x - GAP - KEY_WIDTH, y + ((KEY_HEIGHT + GAP) * 2));
	}
}
