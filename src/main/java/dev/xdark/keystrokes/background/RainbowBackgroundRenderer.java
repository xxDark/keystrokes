package dev.xdark.keystrokes.background;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.xdark.keystrokes.config.ConfigurationWriter;
import dev.xdark.keystrokes.ui.FlattenTextField;
import dev.xdark.keystrokes.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.JsonUtils;

import java.util.function.DoubleConsumer;

import static dev.xdark.keystrokes.button.RenderConstants.GUI_COMPONENT_GAP;
import static dev.xdark.keystrokes.button.RenderConstants.TEXT_LABEL_GAP;

public final class RainbowBackgroundRenderer implements BackgroundRenderer {

	public static final String TYPE = "rainbow";

	private float saturation;
	private float brightness;
	private float alpha;

	public RainbowBackgroundRenderer(float saturation, float brightness, float alpha) {
		this.saturation = saturation;
		this.brightness = brightness;
		this.alpha = alpha;
	}

	@Override
	public void render(int startX, int startY, int endX, int endY) {
		double result = Minecraft.getSystemTime() / 20.0D % 360.0D / 360.0D;
		int rgb = Util.HSBtoRGB((float) result, saturation, brightness);
		rgb |= (int) (alpha * 255.0F) << 24;
		Gui.drawRect(startX, startY, endX, endY, rgb);
	}

	@Override
	public JsonElement save() {
		JsonObject o = new JsonObject();
		o.addProperty("saturation", saturation);
		o.addProperty("brightness", brightness);
		o.addProperty("alpha", alpha);
		return o;
	}

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public GuiHelper makeGuiHelper(ConfigurationWriter configurationWriter, GuiScreen screen, FontRenderer fontRenderer, int x, int y) {
		GuiTextField saturation = new FlattenTextField(0, fontRenderer, x, y);
		y += GUI_COMPONENT_GAP;
		GuiTextField brightness = new FlattenTextField(0, fontRenderer, x, y);
		y += GUI_COMPONENT_GAP;
		GuiTextField alpha = new FlattenTextField(0, fontRenderer, x, y);
		saturation.setText(Float.toString(this.saturation));
		brightness.setText(Float.toString(this.brightness));
		alpha.setText(Float.toString(this.alpha));

		return new GuiHelper() {
			@Override
			public void render(int mouseX, int mouseY, float partialTicks) {
				saturation.drawTextBox();
				brightness.drawTextBox();
				alpha.drawTextBox();
			}

			@Override
			public void renderText(int x, int y, int mouseX, int mouseY, float partialTicks) {
				screen.drawCenteredString(fontRenderer, "Saturation", x, y, -1);
				y += TEXT_LABEL_GAP;
				screen.drawCenteredString(fontRenderer, "Brightness", x, y, -1);
				y += TEXT_LABEL_GAP;
				screen.drawCenteredString(fontRenderer, "Alpha", x, y, -1);
			}

			@Override
			public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
				saturation.mouseClicked(mouseX, mouseY, mouseButton);
				brightness.mouseClicked(mouseX, mouseY, mouseButton);
				alpha.mouseClicked(mouseX, mouseY, mouseButton);
			}

			@Override
			public void keyTyped(char typedChar, int keyCode) {
				if (saturation.textboxKeyTyped(typedChar, keyCode)) {
					trySet(saturation.getText(), value -> RainbowBackgroundRenderer.this.saturation = (float) value);
				}
				if (brightness.textboxKeyTyped(typedChar, keyCode)) {
					trySet(brightness.getText(), value -> RainbowBackgroundRenderer.this.brightness = (float) value);
				}
				if (alpha.textboxKeyTyped(typedChar, keyCode)) {
					trySet(alpha.getText(), value -> RainbowBackgroundRenderer.this.alpha = (float) value);
				}
			}

			private void trySet(String input, DoubleConsumer consumer) {
				try {
					consumer.accept(Float.parseFloat(input));
					configurationWriter.save();
				} catch (NumberFormatException ignored) {
				}
			}
		};
	}

	public static RainbowBackgroundRenderer read(JsonElement data) {
		float saturation = 1.0F, brightness = 1.0F, alpha = 1.0F;
		try {
			JsonObject o = data.getAsJsonObject();
			saturation = JsonUtils.getFloat(o, "saturation", saturation);
			brightness = JsonUtils.getFloat(o, "brightness", brightness);
			alpha = JsonUtils.getFloat(o, "alpha", alpha);
		} catch (Exception ex) {
			return new RainbowBackgroundRenderer(1.0F, 1.0F, 1.0F);
		}
		return new RainbowBackgroundRenderer(saturation, brightness, alpha);
	}
}
