package dev.xdark.keystrokes.util;

import dev.xdark.keystrokes.background.BackgroundRenderer;
import dev.xdark.keystrokes.background.ConstantBackgroundRenderer;
import dev.xdark.keystrokes.background.GradientBackgroundRenderer;
import dev.xdark.keystrokes.background.RainbowBackgroundRenderer;
import dev.xdark.keystrokes.button.Button;
import dev.xdark.keystrokes.button.RenderConstants;
import dev.xdark.keystrokes.config.ConfigurationWriter;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static org.lwjgl.input.Keyboard.*;

import java.util.Map;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

public final class Util {

	private static final int NO_COLOR = -2;
	private static final Object2IntMap<String> COLOR_MAP;

	static {
		Object2IntMap<String> colorMap = new Object2IntArrayMap<>();
		colorMap.put("WHITE", 0xffffffff);
		colorMap.put("LIGHT_GRAY", 0xffc0c0c0);
		colorMap.put("GRAY", 0xff808080);
		colorMap.put("DARK_GRAY", 0xff404040);
		colorMap.put("BLACK", 0xff000000);
		colorMap.put("RED", 0xffff0000);
		colorMap.put("PINK", 0xffffafaf);
		colorMap.put("ORANGE", 0xffffc800);
		colorMap.put("YELLOW", 0xffffff00);
		colorMap.put("GREEN", 0xff00ff00);
		colorMap.put("MAGENTA", 0xffff00ff);
		colorMap.put("CYAN", 0xff00ffff);
		colorMap.put("BLUE", 0xff0000ff);
		colorMap.defaultReturnValue(NO_COLOR);
		COLOR_MAP = colorMap;
	}

	private Util() {
	}

	public static OptionalInt parseColor(String input) {
		try {
			if (input.chars().allMatch(i -> {
				char c = Character.toLowerCase((char) i);
				return (c >= 'a' && c <= 'f') || (c >= '0' && c <= '9') || c == '-';
			})) {
				return OptionalInt.of((int) Long.parseLong(input, 16));
			}
		} catch (NumberFormatException ignored) {
		}
		int color = COLOR_MAP.getInt(input.toUpperCase());
		return color == NO_COLOR ? OptionalInt.empty() : OptionalInt.of(color);
	}

	public static String getKeyName(int keyCode) {
		if (keyCode < 0)
			return keyCode == -100 ? "LMB"
					: keyCode == -99 ? "RMB"
					: keyCode == -98 ? "MMB"
					: Mouse.getButtonName(keyCode + 100);
		switch (keyCode) {
			case KEY_NONE:
				return "";
			case KEY_GRAVE:
				return "~";
			case KEY_LBRACKET:
				return "[";
			case KEY_RBRACKET:
				return "]";
			case KEY_LMENU:
				return "LALT";
			case KEY_RMENU:
				return "RALT";
			case KEY_MULTIPLY:
				return "*";
			case KEY_BACK:
				return "<-";
			case KEY_COMMA:
				return "<";
			case KEY_PERIOD:
				return ">";
			case KEY_BACKSLASH:
				return "\\";
			case KEY_NEXT:
				return "PGDOWN";
			case KEY_RETURN:
				return "ENTER";
			case KEY_LMETA:
				return "LWIN";
			case KEY_RMETA:
				return "RWIN";
			default:
				return Keyboard.getKeyName(keyCode);
		}
	}

	public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float a = (float) (startColor >> 24 & 255) / 255.0F;
		float r = (float) (startColor >> 16 & 255) / 255.0F;
		float g = (float) (startColor >> 8 & 255) / 255.0F;
		float b = (float) (startColor & 255) / 255.0F;
		float a1 = (float) (endColor >> 24 & 255) / 255.0F;
		float r1 = (float) (endColor >> 16 & 255) / 255.0F;
		float g1 = (float) (endColor >> 8 & 255) / 255.0F;
		float b1 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(right, top, 0.0D).color(r, g, b, a).endVertex();
		bufferbuilder.pos(left, top, 0.0D).color(r, g, b, a).endVertex();
		bufferbuilder.pos(left, bottom, 0.0D).color(r1, g1, b1, a1).endVertex();
		bufferbuilder.pos(right, bottom, 0.0D).color(r1, g1, b1, a1).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void updateColor(ConfigurationWriter configurationWriter, String input, IntConsumer accept) {
		parseColor(input).ifPresent(value -> {
			accept.accept(value);
			configurationWriter.save();
		});
	}

	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
				case 0:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (t * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int) (q * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (q * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int) (t * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (q * 255.0f + 0.5f);
					break;
			}
		}
		return (r << 16) | (g << 8) | b;
	}

	public static void setDefaultRenderers(Button button) {
		Map<String, BackgroundRenderer> renderers = button.getBackgroundRenderers();
		renderers.clear();
		BackgroundRenderer defaultRenderer = new ConstantBackgroundRenderer(RenderConstants.DEFAULT_BACKGROUND);
		renderers.put(ConstantBackgroundRenderer.TYPE, defaultRenderer);
		renderers.put(GradientBackgroundRenderer.TYPE, new GradientBackgroundRenderer(0xffffffff, 0xff000000));
		renderers.put(RainbowBackgroundRenderer.TYPE, new RainbowBackgroundRenderer(1.0F, 1.0F, 1.0F));
		button.setBackgroundRenderer(defaultRenderer);
	}
}
