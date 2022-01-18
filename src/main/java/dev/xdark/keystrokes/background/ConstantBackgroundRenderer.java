package dev.xdark.keystrokes.background;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.xdark.keystrokes.button.RenderConstants;
import dev.xdark.keystrokes.config.ConfigurationWriter;
import dev.xdark.keystrokes.ui.FlattenTextField;
import dev.xdark.keystrokes.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public final class ConstantBackgroundRenderer implements BackgroundRenderer {

	public static final String TYPE = "constant";
	private int color;

	public ConstantBackgroundRenderer(int color) {
		this.color = color;
	}

	@Override
	public void render(int startX, int startY, int endX, int endY) {
		Gui.drawRect(startX, startY, endX, endY, color);
	}

	@Override
	public JsonElement save() {
		return new JsonPrimitive(color);
	}

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public GuiHelper makeGuiHelper(ConfigurationWriter configurationWriter, GuiScreen screen, FontRenderer fontRenderer, int x, int y) {
		GuiTextField color = new FlattenTextField(0, fontRenderer, x, y);
		color.setText(Integer.toHexString(this.color));

		return new GuiHelper() {
			@Override
			public void render(int mouseX, int mouseY, float partialTicks) {
				color.drawTextBox();
			}

			@Override
			public void renderText(int x, int y, int mouseX, int mouseY, float partialTicks) {
				screen.drawCenteredString(fontRenderer, "Color", x, y, -1);
			}

			@Override
			public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
				color.mouseClicked(mouseX, mouseY, mouseButton);
			}

			@Override
			public void keyTyped(char typedChar, int keyCode) {
				if (color.textboxKeyTyped(typedChar, keyCode)) {
					Util.updateColor(configurationWriter, color.getText(), value -> ConstantBackgroundRenderer.this.color = value);
				}
			}
		};
	}

	public static BackgroundRenderer read(JsonElement data) {
		int color;
		try {
			color = data.getAsInt();
		} catch (Exception ex) {
			color = RenderConstants.DEFAULT_BACKGROUND;
		}
		return new ConstantBackgroundRenderer(color);
	}
}
