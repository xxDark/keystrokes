package dev.xdark.keystrokes.button;

import dev.xdark.keystrokes.util.ClickCounter;
import dev.xdark.keystrokes.util.Util;
import dev.xdark.keystrokes.util.Vec2i;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;

import static dev.xdark.keystrokes.button.RenderConstants.*;

public final class MouseButton extends Button {

	private static final int X_GAP = MOUSE_WIDTH / 2;
	private static final int Y_GAP = KEY_HEIGHT / 2;
	private final ClickCounter counter = new ClickCounter();
	private final boolean lmb;

	public MouseButton(KeyBinding key, boolean lmb) {
		super(key);
		this.lmb = lmb;
	}

	@Override
	public void render(FontRenderer fontRenderer, int x, int y) {
		int endX = x + MOUSE_WIDTH, endY = y + KEY_HEIGHT;
		backgroundRenderer.render(x, y, endX, endY);
		setupRender(fontRenderer);
		int width = this.width;
		fontRenderer.drawString(renderName, x + (X_GAP - width / 2), y + (Y_GAP - fontRenderer.FONT_HEIGHT / 2), selectColor());
		String cps = counter.getCps() + " cps";
		width = fontRenderer.getStringWidth(cps);
		GlStateManager.scale(0.5D, 0.5D, 1.0D);
		fontRenderer.drawString(cps, (int) ((x + (X_GAP - width / 4)) * 2.0D), (int) ((y + (Y_GAP * 1.5D)) * 2.0D), selectColor());
		GlStateManager.scale(2.0D, 2.0D, 1.0D);
	}

	@Override
	public boolean isInBounds(int x, int y, int mouseX, int mouseY) {
		Vec2i pos = normalizePosition(x, y);
		x = pos.getX();
		y = pos.getY();
		return mouseX >= x && mouseX <= x + MOUSE_WIDTH && mouseY >= y && mouseY <= y + KEY_HEIGHT;
	}

	@Override
	public Vec2i normalizePosition(int x, int y) {
		x = lmb ? (x - (GAP + KEY_WIDTH)) : (x + GAP + KEY_WIDTH / 2);
		return new Vec2i(x, y + KEY_HEIGHT + GAP);
	}

	@Override
	protected String computeRenderName() {
		return Util.getKeyName(lastKeyCode);
	}

	public ClickCounter getCounter() {
		return counter;
	}
}
