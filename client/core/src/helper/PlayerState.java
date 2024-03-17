package helper;

public class PlayerState {
    public int id;
    public float x;
    public float y;
    public float velX, velY;
    public Direction direction;
    public Integer livesCount;
    public int isShooting;

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}
