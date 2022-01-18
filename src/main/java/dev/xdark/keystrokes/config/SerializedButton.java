package dev.xdark.keystrokes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.xdark.keystrokes.background.BackgroundRenderer;
import dev.xdark.keystrokes.button.Button;

import java.util.Map;

import static dev.xdark.keystrokes.button.RenderConstants.DEFAULT_OFF_COLOR;
import static dev.xdark.keystrokes.button.RenderConstants.DEFAULT_ON_COLOR;

public final class SerializedButton {

	@SerializedName("nameOverride")
	private String nameOverride;
	@SerializedName("pressedColor")
	private int pressedColor = DEFAULT_ON_COLOR;
	@SerializedName("unpressedColor")
	private int unpressedColor = DEFAULT_OFF_COLOR;
	@SerializedName("backgroundData")
	private JsonObject backgroundData;

	public SerializedButton(String nameOverride, int pressedColor, int unpressedColor, JsonObject backgroundData) {
		this.nameOverride = nameOverride;
		this.pressedColor = pressedColor;
		this.unpressedColor = unpressedColor;
		this.backgroundData = backgroundData;
	}

	public SerializedButton() {
	}

	public String getNameOverride() {
		return nameOverride;
	}

	public int getPressedColor() {
		return pressedColor;
	}

	public int getUnpressedColor() {
		return unpressedColor;
	}

	public JsonObject getBackgroundData() {
		return backgroundData;
	}

	public static SerializedButton from(Button b) {
		JsonObject backgrounds = new JsonObject();
		for (Map.Entry<String, BackgroundRenderer> entry : b.getBackgroundRenderers().entrySet()) {
			JsonElement element = entry.getValue().save();
			if (element == null) continue;
			backgrounds.add(entry.getKey(), element);
		}
		JsonObject backgroundData = new JsonObject();
		backgroundData.addProperty(JsonConstants.CURRENT_BACKGROUND, b.getBackgroundRenderer().type());
		backgroundData.add(JsonConstants.BACKGROUNDS, backgrounds);
		return new SerializedButton(b.getNameOverride(), b.getPressedColor(), b.getUnpressedColor(), backgroundData);
	}
}
