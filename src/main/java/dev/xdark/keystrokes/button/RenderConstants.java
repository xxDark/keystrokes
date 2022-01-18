package dev.xdark.keystrokes.button;

public final class RenderConstants {

	public static final int KEY_WIDTH = 24;
	public static final int KEY_HEIGHT = 24;
	public static final int GAP = 2;
	// A, S, D, two gaps
	public static final int SPACE_WIDTH = KEY_WIDTH * 3 + GAP * 2;
	public static final int SPACE_OFFSET_X = 30;
	public static final int SPACE_HEIGHT = 16;
	public static final int MOUSE_WIDTH = (int) (KEY_WIDTH * 1.5D);
	public static final int DEFAULT_ON_COLOR = 0xff00ff00;
	public static final int DEFAULT_OFF_COLOR = 0xffff0000;
	public static final int DEFAULT_BACKGROUND = 0x77000000;
	public static final int GUI_COMPONENT_WIDTH = 128;
	public static final int GUI_COMPONENT_HEIGHT = 20;
	public static final int GUI_COMPONENT_GAP = 42;
	public static final int TEXT_LABEL_GAP = 21;

	private RenderConstants() {
	}
}
