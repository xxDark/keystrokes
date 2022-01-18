package dev.xdark.keystrokes.background;

import com.google.gson.JsonElement;
import dev.xdark.keystrokes.config.ConfigurationWriter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public interface BackgroundRenderer {

	void render(int startX, int startY, int endX, int endY);

	JsonElement save();

	String type();

	GuiHelper makeGuiHelper(ConfigurationWriter configurationWriter, GuiScreen screen, FontRenderer fontRenderer, int x, int y);

	interface GuiHelper {

		void render(int mouseX, int mouseY, float partialTicks);

		void renderText(int x, int y, int mouseX, int mouseY, float partialTicks);

		void mouseClicked(int mouseX, int mouseY, int mouseButton);

		void keyTyped(char typedChar, int keyCode);
	}
}
