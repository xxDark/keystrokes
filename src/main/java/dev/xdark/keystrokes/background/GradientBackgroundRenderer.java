package dev.xdark.keystrokes.background;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.xdark.keystrokes.config.ConfigurationWriter;
import dev.xdark.keystrokes.ui.FlattenTextField;
import dev.xdark.keystrokes.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.JsonUtils;

import static dev.xdark.keystrokes.button.RenderConstants.GUI_COMPONENT_GAP;
import static dev.xdark.keystrokes.button.RenderConstants.TEXT_LABEL_GAP;

public final class GradientBackgroundRenderer implements BackgroundRenderer {

	public static final String TYPE = "gradient";
	private int from;
	private int to;

	public GradientBackgroundRenderer(int from, int to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public void render(int startX, int startY, int endX, int endY) {
		Util.drawGradientRect(startX, startY, endX, endY, from, to);
	}

	@Override
	public JsonElement save() {
		JsonObject o = new JsonObject();
		o.addProperty("from", from);
		o.addProperty("to", to);
		return o;
	}

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public GuiHelper makeGuiHelper(ConfigurationWriter configurationWriter, GuiScreen screen, FontRenderer fontRenderer, int x, int y) {
		GuiTextField from = new FlattenTextField(0, fontRenderer, x, y);
		GuiTextField to = new FlattenTextField(0, fontRenderer, x, y + GUI_COMPONENT_GAP);
		from.setText(Integer.toHexString(this.from));
		to.setText(Integer.toHexString(this.to));

		return new GuiHelper() {
			@Override
			public void render(int mouseX, int mouseY, float partialTicks) {
				from.drawTextBox();
				to.drawTextBox();
			}

			@Override
			public void renderText(int x, int y, int mouseX, int mouseY, float partialTicks) {
				screen.drawCenteredString(fontRenderer, "Gradient from", x, y, -1);
				screen.drawCenteredString(fontRenderer, "Gradient to", x, y + TEXT_LABEL_GAP, -1);
			}

			@Override
			public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
				from.mouseClicked(mouseX, mouseY, mouseButton);
				to.mouseClicked(mouseX, mouseY, mouseButton);
			}

			@Override
			public void keyTyped(char typedChar, int keyCode) {
				if (from.textboxKeyTyped(typedChar, keyCode)) {
					Util.updateColor(configurationWriter, from.getText(), value -> GradientBackgroundRenderer.this.from = value);
				}
				if (to.textboxKeyTyped(typedChar, keyCode)) {
					Util.updateColor(configurationWriter, to.getText(), value -> GradientBackgroundRenderer.this.to = value);
				}
			}
		};
	}

	public static BackgroundRenderer read(JsonElement data) {
		int from = 0xffffffff, to = 0xff000000;
		try {
			JsonObject o = data.getAsJsonObject();
			from = JsonUtils.getInt(o, "from", from);
			to = JsonUtils.getInt(o, "to", to);
		} catch (Exception ex) {
			return new GradientBackgroundRenderer(0xffffffff, 0xff000000);
		}
		return new GradientBackgroundRenderer(from, to);
	}
}
