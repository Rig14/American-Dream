package ee.taltech.americandream;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static helper.Constants.BOUNDS;

public class Bullet {
    private static final float SPEED = 500f; // adjust speed as needed
    private static Texture texture;
    public boolean remove = false;
    private float x, y;
    private Vector2 velocity;

    public Bullet(float playerX, float playerY, boolean shootRight) {
        this.x = playerX;
        this.y = playerY;
        velocity = new Vector2(shootRight ? SPEED : -SPEED, 0); // adjust direction based on shootRight flag
        if (texture == null) {
            texture = new Texture("bullet2-transformed.png");
        }
    }

    public void update(float deltaTime, Vector2 center) {
        x += velocity.x * deltaTime;
        y += velocity.y * deltaTime;
        if (x > center.x + BOUNDS || x < center.x - BOUNDS || y > center.y + BOUNDS || y < center.y - BOUNDS) {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public boolean shouldRemove() {
        return remove;
    }
}
