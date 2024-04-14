package helper;

import java.awt.*;

public class Constants {
    public static final int[] PORTS = new int[]{8080, 8081};
    public static final int LOBBY_SIZE = 2;
    public static final int TICK_RATE = 60;
    public static final int GAME_DURATION = 300;
    public static final float BOUNDS = 1000f;
    public static final float SHOOT_DELAY = 0.3f;
    public static final float PISTOL_BULLET_SPEED = 5;
    public static final int PLAYER_HEIGHT = 64;
    public static final int PLAYER_WIDTH = 32;
    // should be the same as the bullet texture size in the client
    public static final int BULLET_HITBOX = 20;
    public static final float PISTOL_BULLET_FORCE = 1000;
    // will send lobby data to clients every N seconds
    public static final int LOBBY_UPDATE_RATE_IN_SECONDS = 1;

    // lower value = more pushback; higher value = less pushback
    // optimal range 20-50 (10 for testing)
    // force *= 1 + (damage / x)
    public static final int DAMAGE_INCREASES_PUSHBACK_COEFFICIENT = 10;
    // how fast the AI player shoots bullets
    public static final int AI_PLAYER_SHOOTING_INTERVAL = 2;
    public static final Dimension AI_PLAYER_SIZE = new Dimension(40, 40);
}
