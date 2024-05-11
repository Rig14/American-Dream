package ee.taltech.americandream.server;

import helper.BulletData;

import java.util.ArrayList;
import java.util.List;

import static helper.Constants.*;

public class UFO {
    private final List<BulletData> bullets;
    private float x;
    private float y;
    private float velocity = 100;
    private float shootCountdown = 0;
    private float knockback = 0;
    private float bulletForce = 1000;
    private float bulletSpeed = 5;


    /**
     * Create UFO.
     * It will slowly move towards the closest player and randomly shoot bullets. The UFO doesn't have any lives
     * and can't fall off the platforms.
     */
    public UFO(float x, float y) {
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

    /**
     * Update the UFO's position (move towards the closest player), shoot bullets and check for bullet hits.
     * @param players all regular players of the current game instance
     */
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

        // shoot a bullet if countdown is over
        if (shootCountdown >= UFO_SHOOTING_INTERVAL) {
            BulletData bullet = new BulletData();
            bullet.x = x;
            bullet.y = y;
            bullet.speedBullet = bulletSpeed * (closestPlayer.getState().x < x ? -1 : 1);
            bullet.id = -1;
            bullet.name = "UFO";
            bullets.add(bullet);
            shootCountdown = 0;
        }

        // update bullets
        bullets.forEach(bullet -> bullet.x += bullet.speedBullet);

        // removing bullets
        bullets.removeIf(bullet -> bullet.x < x - BOUNDS || bullet.x > x + BOUNDS);

        if (Math.abs(knockback) < 1) {
            // if knockback is small enough, set it to 0
            knockback = 0;
        } else {
            // apply knockback
            x += knockback * delta;
            // reduce knockback
            knockback *= 0.9;
        }

        shootCountdown += delta;
    }

    /**
     * UFO can be hit by bullets just like a regular player.
     * Except it doesn't have a damage percentage which means that the applied force is constant.
     */
    public void bulletHit(BulletData bullet) {
        knockback = (bullet.speedBullet < 0 ? -1 : 1) * bulletForce;
    }
}
