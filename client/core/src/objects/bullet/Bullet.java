package objects.bullet;

import com.badlogic.gdx.math.Vector2;
import ee.taltech.americandream.AmericanDream;
import helper.BulletData;

import static helper.Constants.BOUNDS;

public class Bullet {
    private final float speed;
    private float x, y;

    public Bullet(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void update(float delta) {
        x += speed * delta;
    }

    public BulletData toBulletData() {
        BulletData bulletData = new BulletData();
        bulletData.x = x;
        bulletData.y = y;
        bulletData.speedBullet = speed;
        bulletData.id = AmericanDream.id;
        return bulletData;
    }

    public boolean shouldRemove(Vector2 center) {
        return x > center.x + BOUNDS || x < center.x - BOUNDS || y > center.y + BOUNDS || y < center.y - BOUNDS;
    }
}
