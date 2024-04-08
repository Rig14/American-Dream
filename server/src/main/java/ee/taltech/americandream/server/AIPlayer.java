package ee.taltech.americandream.server;

import helper.BulletData;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer {
    private final List<BulletData> bullets;
    private float x;
    private float y;

    public AIPlayer(float x, float y) {
        this.x = x;
        this.y = y;
        this.bullets = new ArrayList<>();
    }

    public List<BulletData> getBullets() {
        return bullets;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
