package helper;

import static helper.Constants.LIVES_COUNT;

public class PlayerState {
    public int id;
    public float x;
    public float y;
    public Direction direction;
    public Integer livesCount;
    public Integer damage;
    public float applyForce;

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getLivesCount() {
        if (livesCount == null) {
            return LIVES_COUNT;
        }
        return livesCount;
    }
}
