package ee.taltech.americandream.server;

import helper.BulletData;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer {
    private final List<BulletData> bullets;
    private float x;
    private float y;
    private float velocity = 100;

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

    public void update(float delta, Player[] players) {
        // select player closest to
        Player closestPlayer = null;
        float closestDistance = Float.MAX_VALUE;
        for (Player player : players) {
            float distance = (float) Math.sqrt(Math.pow(player.getState().x - x, 2) + Math.pow(player.getState().y - y, 2));
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }

        if (closestPlayer == null) {
            return;
        }

        // set velocity according to:
        // when distance is higher -> velocity higher
        // when distance is lower -> velocity lower
        velocity = closestDistance;

        // move towards player
        float angle = (float) Math.atan2(closestPlayer.getState().y - y, closestPlayer.getState().x - x);

        x += (float) (Math.cos(angle) * velocity * delta);
        y += (float) (Math.sin(angle) * velocity * delta);
    }
}
