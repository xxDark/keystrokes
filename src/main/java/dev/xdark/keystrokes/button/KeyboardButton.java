package dev.xdark.keystrokes.button;

import dev.xdark.keystrokes.util.Util;
import dev.xdark.keystrokes.util.Vec2i;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;

import static dev.xdark.keystrokes.button.RenderConstants.*;

public final class KeyboardButton extends Button {

	private static final int X_GAP = KEY_WIDTH / 2;
	private static final int Y_GAP = KEY_HEIGHT / 2;
	private final int offsetX, offsetY;

	public KeyboardButton(KeyBinding key, int offsetX, int offsetY) {
		super(key);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public void render(FontRenderer fontRenderer, int x, int y) {
		int endX = x + KEY_WIDTH, endY = y + KEY_HEIGHT;
		backgroundRenderer.render(x, y, endX, endY);
		setupRender(fontRenderer);
		int width = this.width;
		fontRenderer.drawString(renderName, x + (X_GAP - width / 2), y + (Y_GAP - fontRenderer.FONT_HEIGHT / 2), selectColor());
	}

	@Override
	public boolean isInBounds(int x, int y, int mouseX, int mouseY) {
		Vec2i pos = normalizePosition(x, y);
		x = pos.getX();
		y = pos.getY();
		return mouseX >= x && mouseX <= x + KEY_WIDTH && mouseY >= y && mouseY <= y + KEY_HEIGHT;
	}

	@Override
	protected String computeRenderName() {
		return Util.getKeyName(lastKeyCode);
	}

	@Override
	public Vec2i normalizePosition(int x, int y) {
		return new Vec2i(x + (KEY_WIDTH + GAP) * offsetX, y - (KEY_HEIGHT + GAP) * offsetY);
	}
}
