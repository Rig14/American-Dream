package helper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.Map;

public class Constants {
    // debug mode
    public static final boolean GAMEPLAY_DEBUG = true;
    // pixels per meter (for every meter in LibGDX box2D world there are 16 pixels on player's camera)
    public static final float PPM = 32.0f;
    public static final float GRAVITY = -15f;
    public static final float FPS = 60f;
    public static final float PLAYER_SPEED = 7f;
    public static final float JUMP_FORCE = 8f;
    // how many jumps can the player do
    public static final int JUMP_COUNT = 3;
    // camera speed - less is faster
    public static final float CAMERA_SPEED = 4f;
    // the zoom level of the camera
    public static final float CAMERA_ZOOM = 1.5f;

    // the bounds of the map (for camera and player deaths)
    // higher means player can go further down
    public static final float BOUNDS = 1400f;
    // how long player must hold down the down key to fall through the platform
    public static final float PLATFORM_DESCENT = .2f;
    // in seconds
    public static final float RESPAWN_TIME = 0f;

    public static final int[] PORTS = new int[]{8080, 8081};
    public static final String IP_ADDRESS = "";
    public static final Vector2 BULLET_DIMENSIONS = new Vector2(20, 20);
    public static final int LIVES_COUNT = 3;
    public static final int FRAME_WIDTH = 64;
    public static final int FRAME_HEIGHT = 74;
    public static final float FRAME_DURATION = 0.3f;

    public static final Dimension AI_PLAYER_SIZE = new Dimension(60, 60);

    public static final float FONT_SCALING_FACTOR = 300f;
    public static final Map<Integer, Color> REMOTE_PLAYER_COLORS = Map.of(
            0, Color.RED,
            1, Color.BLUE,
            2, Color.YELLOW);
    public static final Map<Integer, Texture> REMOTE_PLAYER_INDICATORS = Map.of(
            0, new Texture("player-indicator-red.png"),
            1, new Texture("player-indicator-blue.png"),
            2, new Texture("player-indicator-yellow.png"));
}
