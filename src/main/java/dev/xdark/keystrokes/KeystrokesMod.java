package dev.xdark.keystrokes;

import com.google.gson.*;
import dev.xdark.keystrokes.background.BackgroundRenderer;
import dev.xdark.keystrokes.background.ConstantBackgroundRenderer;
import dev.xdark.keystrokes.background.GradientBackgroundRenderer;
import dev.xdark.keystrokes.background.RainbowBackgroundRenderer;
import dev.xdark.keystrokes.button.Button;
import dev.xdark.keystrokes.button.KeyboardButton;
import dev.xdark.keystrokes.button.MouseButton;
import dev.xdark.keystrokes.button.SpaceButton;
import dev.xdark.keystrokes.config.ConfigurationWriter;
import dev.xdark.keystrokes.config.JsonConstants;
import dev.xdark.keystrokes.config.SerializedButton;
import dev.xdark.keystrokes.ui.KeyConfigureScreen;
import dev.xdark.keystrokes.util.Util;
import dev.xdark.keystrokes.util.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Mod(modid = "keystrokes", name = "Keystrokes Mod", version = "1.0")
public final class KeystrokesMod implements ConfigurationWriter {

	private static final int X = 128, Y = 128;
	private static final Logger LOGGER = LogManager.getLogger(KeystrokesMod.class);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private Minecraft mc;
	private FontRenderer fontRenderer;
	private MouseButton leftClick;
	private MouseButton rightClick;
	private KeyboardButton forward;
	private KeyboardButton backward;
	private KeyboardButton left;
	private KeyboardButton right;
	private SpaceButton jump;
	private Button[] allButtons;
	private Path configPath;

	@EventHandler
	public void init(FMLInitializationEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		this.mc = mc;
		fontRenderer = mc.fontRenderer;
		GameSettings gameSettings = mc.gameSettings;
		leftClick = new MouseButton(gameSettings.keyBindAttack, true);
		rightClick = new MouseButton(gameSettings.keyBindUseItem, false);
		forward = new KeyboardButton(gameSettings.keyBindForward, 0, 1);
		backward = new KeyboardButton(gameSettings.keyBindBack, 0, 0);
		left = new KeyboardButton(gameSettings.keyBindLeft, -1, 0);
		right = new KeyboardButton(gameSettings.keyBindRight, 1, 0);
		jump = new SpaceButton(gameSettings.keyBindJump);
		allButtons = new Button[]{forward, backward, left, right, jump, leftClick, rightClick};

		for (Button b : allButtons) {
			Util.setDefaultRenderers(b);
		}

		Path configDir = mc.mcDataDir.toPath().resolve("config");
		try {
			Files.createDirectories(configDir);
			Path configPath = configDir.resolve("keystrokes.json");
			this.configPath = configPath;
			if (Files.isRegularFile(configPath)) {
				Map<String, SerializedButton> buttonMap;
				try (BufferedReader reader = Files.newBufferedReader(configPath)) {
					buttonMap = GSON.fromJson(reader, new ParameterizedType() {

						@Override
						public Type[] getActualTypeArguments() {
							return new Type[]{String.class, SerializedButton.class};
						}

						@Override
						public Type getRawType() {
							return Map.class;
						}

						@Override
						public Type getOwnerType() {
							return null;
						}
					});
				}
				readButtonsConfiguration(allButtons, buttonMap);
			}
		} catch (IOException | JsonParseException ex) {
			LOGGER.error("Could not read configuration file", ex);
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent e) {
		if (e.phase == TickEvent.Phase.END) {
			leftClick.getCounter().update();
			rightClick.getCounter().update();
			Minecraft mc = this.mc;
			GuiScreen screen = mc.currentScreen;
			if ((screen instanceof GuiChat || screen instanceof KeyConfigureScreen) && Mouse.isButtonDown(0)) {
				int width = screen.width;
				int height = screen.height;
				int mouseX = Mouse.getX() * width / mc.displayWidth;
				int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;
				Button button = findButton(mouseX, mouseY);
				if (button != null) {
					mc.displayGuiScreen(new KeyConfigureScreen(this, button));
				}
			}
		}
	}

	@SubscribeEvent
	public void onMouse(MouseEvent e) {
		if (Mouse.getEventButtonState()) {
			int button = Mouse.getEventButton();
			if (button == 0) {
				leftClick.getCounter().increment();
			} else if (button == 1) {
				rightClick.getCounter().increment();
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Pre e) {
		RenderGameOverlayEvent.ElementType type = e.getType();
		if (type == RenderGameOverlayEvent.ElementType.ALL) {
			FontRenderer fontRenderer = this.fontRenderer;
			int x = X;
			int y = Y;
			renderButton(backward, fontRenderer, x, y);
			renderButton(forward, fontRenderer, x, y);
			renderButton(left, fontRenderer, x, y);
			renderButton(right, fontRenderer, x, y);
			renderButton(leftClick, fontRenderer, x, y);
			renderButton(rightClick, fontRenderer, x, y);
			renderButton(jump, fontRenderer, x, y);
		}
	}

	private Button findButton(int mouseX, int mouseY) {
		for (Button b : allButtons) {
			if (b.isInBounds(X, Y, mouseX, mouseY)) {
				return b;
			}
		}
		return null;
	}

	private void readButtonsConfiguration(Button[] allButtons, Map<String, SerializedButton> buttonMap) {
		for (Button b : allButtons) {
			SerializedButton serialized = buttonMap.get(b.getKey().getKeyDescription());
			if (serialized != null) {
				b.setPressedColor(serialized.getPressedColor());
				b.setUnpressedColor(serialized.getUnpressedColor());
				b.setNameOverride(serialized.getNameOverride());
				readBackground(b, serialized.getBackgroundData());
			}
		}
	}

	private void readBackground(Button button, JsonObject json) {
		Map<String, BackgroundRenderer> backgroundRenderers = button.getBackgroundRenderers();
		BackgroundRenderer candidate = null;
		if (json != null) {
			JsonElement backgrounds = json.get(JsonConstants.BACKGROUNDS);
			if (backgrounds != null && backgrounds.isJsonObject()) {
				for (Map.Entry<String, JsonElement> entry : backgrounds.getAsJsonObject().entrySet()) {
					String type = entry.getKey();
					JsonElement data = entry.getValue();
					BackgroundRenderer renderer = null;
					switch (type) {
						case ConstantBackgroundRenderer.TYPE:
							renderer = ConstantBackgroundRenderer.read(data);
							break;
						case GradientBackgroundRenderer.TYPE:
							renderer = GradientBackgroundRenderer.read(data);
							break;
						case RainbowBackgroundRenderer.TYPE:
							renderer = RainbowBackgroundRenderer.read(data);
							break;
						default:
					}
					if (renderer != null) backgroundRenderers.put(type, renderer);
				}
			}
			JsonElement current = json.get(JsonConstants.CURRENT_BACKGROUND);
			if (current != null && current.isJsonPrimitive()) {
				candidate = backgroundRenderers.get(current.getAsString());
			}
		}
		if (candidate == null) {
			candidate = backgroundRenderers.get(ConstantBackgroundRenderer.TYPE);
		}
		button.setBackgroundRenderer(candidate);
	}

	private static void renderButton(Button button, FontRenderer fontRenderer, int x, int y) {
		Vec2i vec = button.normalizePosition(x, y);
		button.render(fontRenderer, vec.getX(), vec.getY());
	}

	@Override
	public void save() {
		Map<String, SerializedButton> data = Arrays.stream(allButtons)
				.map(x -> new AbstractMap.SimpleEntry<>(x.getKey().getKeyDescription(), SerializedButton.from(x)))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		try {
			Path configPath = this.configPath;
			Files.createDirectories(configPath.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException ex) {
			LOGGER.error("Could not save configuration file", ex);
		}
	}
}
