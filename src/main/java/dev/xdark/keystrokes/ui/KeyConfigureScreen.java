package dev.xdark.keystrokes.ui;

import com.google.common.collect.Iterators;
import dev.xdark.keystrokes.background.BackgroundRenderer;
import dev.xdark.keystrokes.button.Button;
import dev.xdark.keystrokes.config.ConfigurationWriter;
import dev.xdark.keystrokes.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.IntConsumer;

import static dev.xdark.keystrokes.button.RenderConstants.GUI_COMPONENT_GAP;
import static dev.xdark.keystrokes.button.RenderConstants.TEXT_LABEL_GAP;

public final class KeyConfigureScreen extends GuiScreen {

	static final int SELECTOR_ID = 0;

	private final ConfigurationWriter configurationWriter;
	private final Button button;
	private GuiTextField pressedColor;
	private GuiTextField unpressedColor;
	private GuiTextField nameOverride;
	private Iterator<BackgroundRenderer> backgroundRenderers;
	private BackgroundRenderer.GuiHelper backgroundGuiHelper;

	public KeyConfigureScreen(ConfigurationWriter configurationWriter, Button button) {
		this.configurationWriter = configurationWriter;
		this.button = button;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		FontRenderer fontRenderer = this.fontRenderer;
		int width = this.width;
		int center = width / 2 - 65;
		Button button = this.button;
		int fieldY = 70;
		pressedColor = new FlattenTextField(0, fontRenderer, center, fieldY);
		fieldY += GUI_COMPONENT_GAP;
		unpressedColor = new FlattenTextField(1, fontRenderer, center, fieldY);
		fieldY += GUI_COMPONENT_GAP;
		nameOverride = new FlattenTextField(2, fontRenderer, center, fieldY);
		fieldY += GUI_COMPONENT_GAP;

		BackgroundRenderer currentBackgroundRenderer = button.getBackgroundRenderer();
		GuiButton backgroundSelector = new FlattenButton(SELECTOR_ID, fontRenderer, center, fieldY, currentBackgroundRenderer.type());
		buttonList.add(backgroundSelector);
		setupIterator();
		fieldY += GUI_COMPONENT_GAP;

		pressedColor.setText(Integer.toHexString(button.getPressedColor()));
		unpressedColor.setText(Integer.toHexString(button.getUnpressedColor()));

		String nameOverride = button.getNameOverride();
		if (nameOverride != null && !nameOverride.isEmpty()) {
			this.nameOverride.setText(nameOverride);
		}

		backgroundGuiHelper = currentBackgroundRenderer.makeGuiHelper(configurationWriter, this, fontRenderer, center, fieldY);
	}

	private void setupIterator() {
		Iterator<BackgroundRenderer> backgroundRenderers = Iterators.cycle(button.getBackgroundRenderers().values());
		BackgroundRenderer currentBackgroundRenderer = button.getBackgroundRenderer();
		while (backgroundRenderers.hasNext()) {
			if (currentBackgroundRenderer == backgroundRenderers.next()) break;
		}
		this.backgroundRenderers = backgroundRenderers;
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		button.setKeyDownOverride(null);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float tickLength) {
		super.drawScreen(mouseX, mouseY, tickLength);
		Button button = this.button;
		BackgroundRenderer.GuiHelper guiHelper = backgroundGuiHelper;
		FontRenderer fontRenderer = this.fontRenderer;
		GlStateManager.scale(2.0D, 2.0D, 1.0D);
		int textX = width / 4;
		drawCenteredString(fontRenderer, "Key setup: " + button.getRenderName(), textX, 10, -1);
		int componentY = 25;
		drawCenteredString(fontRenderer, "Pressed Color", textX, componentY, -1);
		componentY += TEXT_LABEL_GAP;
		drawCenteredString(fontRenderer, "Unpressed Color", textX, componentY, -1);
		componentY += TEXT_LABEL_GAP;
		drawCenteredString(fontRenderer, "Name Override", textX, componentY, -1);
		componentY += TEXT_LABEL_GAP;
		drawCenteredString(fontRenderer, "Background", textX, componentY, -1);
		componentY += TEXT_LABEL_GAP;

		guiHelper.renderText(textX, componentY, mouseX, mouseY, tickLength);
		GlStateManager.scale(0.5D, 0.5D, 1.0D);
		GuiTextField activeColor = this.pressedColor;
		GuiTextField inactiveColor = this.unpressedColor;
		activeColor.drawTextBox();
		inactiveColor.drawTextBox();
		nameOverride.drawTextBox();
		if (activeColor.isFocused()) {
			button.setKeyDownOverride(true);
		} else if (inactiveColor.isFocused()) {
			button.setKeyDownOverride(false);
		} else {
			button.setKeyDownOverride(null);
		}
		guiHelper.render(mouseX, mouseY, tickLength);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		GuiTextField activeColor = this.pressedColor;
		if (activeColor.textboxKeyTyped(typedChar, keyCode)) {
			updateButtonColor(activeColor.getText(), button::setPressedColor);
		}
		GuiTextField inactiveColor = this.unpressedColor;
		if (inactiveColor.textboxKeyTyped(typedChar, keyCode)) {
			updateButtonColor(inactiveColor.getText(), button::setUnpressedColor);
		}
		if (nameOverride.textboxKeyTyped(typedChar, keyCode)) {
			Button button = this.button;
			button.setNameOverride(nameOverride.getText());
			button.updateData(fontRenderer);
			configurationWriter.save();
		}
		backgroundGuiHelper.keyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		pressedColor.mouseClicked(mouseX, mouseY, mouseButton);
		unpressedColor.mouseClicked(mouseX, mouseY, mouseButton);
		nameOverride.mouseClicked(mouseX, mouseY, mouseButton);
		backgroundGuiHelper.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == SELECTOR_ID) {
			BackgroundRenderer renderer = backgroundRenderers.next();
			this.button.setBackgroundRenderer(renderer);
			button.displayString = renderer.type();
			ConfigurationWriter configurationWriter = this.configurationWriter;
			configurationWriter.save();
			backgroundGuiHelper = renderer.makeGuiHelper(configurationWriter, this, fontRenderer, width / 2 - 65, 70 + GUI_COMPONENT_GAP * 4);
		}
	}

	private void updateButtonColor(String input, IntConsumer accept) {
		Util.updateColor(configurationWriter, input, accept);
	}
}
