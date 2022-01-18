package dev.xdark.keystrokes.button;

import dev.xdark.keystrokes.background.BackgroundRenderer;
import dev.xdark.keystrokes.util.Vec2i;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;

import java.util.HashMap;
import java.util.Map;

public abstract class Button {

	private final KeyBinding key;
	private final Map<String, BackgroundRenderer> backgroundRenderers = new HashMap<>();
	private int pressedColor = RenderConstants.DEFAULT_ON_COLOR;
	private int unpressedColor = RenderConstants.DEFAULT_OFF_COLOR;
	protected BackgroundRenderer backgroundRenderer;
	private String nameOverride;
	protected int lastKeyCode = Integer.MIN_VALUE;
	protected String renderName;
	protected int width;
	private Boolean keyDownOverride;

	protected Button(KeyBinding key) {
		this.key = key;
	}

	public abstract void render(FontRenderer fontRenderer, int x, int y);

	protected abstract String computeRenderName();

	public abstract boolean isInBounds(int x, int y, int mouseX, int mouseY);

	public abstract Vec2i normalizePosition(int x, int y);

	public Map<String, BackgroundRenderer> getBackgroundRenderers() {
		return backgroundRenderers;
	}

	public int getPressedColor() {
		return pressedColor;
	}

	public void setPressedColor(int pressedColor) {
		this.pressedColor = pressedColor;
	}

	public int getUnpressedColor() {
		return unpressedColor;
	}

	public void setUnpressedColor(int unpressedColor) {
		this.unpressedColor = unpressedColor;
	}

	public BackgroundRenderer getBackgroundRenderer() {
		return backgroundRenderer;
	}

	public void setBackgroundRenderer(BackgroundRenderer backgroundRenderer) {
		this.backgroundRenderer = backgroundRenderer;
	}

	public String getNameOverride() {
		return nameOverride;
	}

	public void setNameOverride(String nameOverride) {
		this.nameOverride = nameOverride;
	}

	public void setKeyDownOverride(Boolean keyDownOverride) {
		this.keyDownOverride = keyDownOverride;
	}

	public KeyBinding getKey() {
		return key;
	}

	public String getRenderName() {
		return renderName;
	}

	protected final void setupRender(FontRenderer fontRenderer) {
		KeyBinding key = this.key;
		int currentKeyCode = key.getKeyCode();

		if (lastKeyCode != currentKeyCode) {
			lastKeyCode = currentKeyCode;
			updateData(fontRenderer);
		}
	}

	protected final int selectColor() {
		Boolean keyDownOverride = this.keyDownOverride;
		boolean b = (keyDownOverride != null && keyDownOverride.booleanValue()) || key.isKeyDown();
		return b ? pressedColor : unpressedColor;
	}

	public void updateData(FontRenderer fontRenderer) {
		String renderName = nameOverride;
		if (renderName == null || renderName.isEmpty())
			renderName = computeRenderName();
		this.renderName = renderName;
		if (renderName != null) {
			width = fontRenderer.getStringWidth(renderName);
		}
	}
}
