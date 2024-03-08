package helper;

import com.badlogic.gdx.math.Vector2;

public class Constants {
    // pixels per meter (not sure what it does)
    public static final float PPM = 32.0f;
    public static final float GRAVITY = -15f;
    public static final float FPS = 60f;
    public static final float PLAYER_SPEED = 7f;
    public static final float JUMP_FORCE = 8f;
    // how many jumps can the player do
    public static final int JUMP_COUNT = 100;
    // camera speed - less is faster
    public static final float CAMERA_SPEED = 4f;
    // the zoom level of the camera
    public static final float CAMERA_ZOOM = 1.5f;

    // the bounds of the map (for camera and player deaths)
    // higher means player can go further down
    public static final float BOUNDS = 1400f;
    // how long player must hold down the down key to fall through the platform
    public static final float PLATFORM_DESCENT = .5f;
    // in seconds
    public static final float RESPAWN_TIME = 3f;

    public static final int[] PORTS = new int[]{8080, 8081};
    public static final String IP_ADDRESS = "localhost";
    public static final float BULLET_SPEED = 500f;
    public static final Vector2 BULLET_DIMENSIONS = new Vector2(20, 20);
    public static int LIVES_COUNT = 3;

    // Used for calculating off-screen indicator
    public static final float PLAYER_BARELY_VISIBLE = 5 * CAMERA_ZOOM;  // Indicator appears before player goes off-screen
    // Could 266 and 333 somehow be tied to map size or tilemap?
    public static final float OFFSCREEN_Y = 266.6f * CAMERA_ZOOM - PLAYER_BARELY_VISIBLE;
    public static final float OFFSCREEN_Y_NEG = -266.6f * CAMERA_ZOOM + PLAYER_BARELY_VISIBLE;
    public static final float OFFSCREEN_X = 333.3f * CAMERA_ZOOM - PLAYER_BARELY_VISIBLE;
    public static final float OFFSCREEN_X_NEG = -333.3f * CAMERA_ZOOM + PLAYER_BARELY_VISIBLE;

}
