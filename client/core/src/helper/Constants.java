package helper;

public class Constants {
    // pixels per meter (not sure what it does)
    public static final float PPM = 32.0f;
    public static final float GRAVITY = -15f;
    public static final float FPS = 60f;
    public static final float PLAYER_SPEED = 5f;
    public static final float JUMP_FORCE = 7f;
    // how many jumps can the player do
    public static final int JUMP_COUNT = 100;
    // camera speed - less is faster
    public static final float CAMERA_SPEED = 10f;
    // the zoom level of the camera
    public static final float CAMERA_ZOOM = 2f;

    // the bounds of the map (for camera and player deaths)
    // higher means player can go further down
    public static final float BOUNDS = -800f;

    public static final int[] PORTS = new int[]{8080, 8081};
    public static final String IP_ADDRESS = "localhost";
    public static final float BULLET_SPEED = 500f;
}
