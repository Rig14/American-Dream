package helper;

import static helper.Constants.LIVES_COUNT;

public class PlayerState {
    public int id;
    public String name;
    public float x;
    public float y;
    public float velX, velY;
    public Direction direction;
    public Integer livesCount;
    public int isShooting;
    public Integer damage;
    public float applyForce;

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    // custom client-side getter to prevent nullPointerException in Hud.update()
    public int getLivesCount() {
        if (livesCount == null) {
            return LIVES_COUNT;
        }
        return livesCount;
    }
}
