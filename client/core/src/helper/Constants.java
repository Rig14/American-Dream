package helper;

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
    public static final float CAMERA_ZOOM = 2f;

    // the bounds of the map (for camera and player deaths)
    // higher means player can go further down
    public static final float BOUNDS = 1400f;
    // how long player must hold down the down key to fall through the platform
    public static final float PLATFORM_DESCENT = .5f;
    // in seconds
    public static final float RESPAWN_TIME = 3f;

    public static final int[] PORTS = new int[]{8080, 8081};
    public static final String IP_ADDRESS = "localhost";
}
