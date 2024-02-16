package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public class Bullet {
    private static final float SPEED = 500f; // Adjust speed as needed
    private static Texture texture;
    private float x, y;
    private Vector2 velocity;
    public boolean remove = false;

    public Bullet(float playerX, float playerY, boolean shootRight) {
        this.x = playerX;
        this.y = playerY;
        velocity = new Vector2(shootRight ? SPEED : -SPEED, 0); // Adjust direction based on shootRight flag
        if (texture == null) {
            texture = new Texture("bullet1.png");
        }
    }

    public void update(float deltaTime) {
        x += velocity.x * deltaTime;
        y += velocity.y * deltaTime;
        if (x > Gdx.graphics.getWidth() || x < 0 || y > Gdx.graphics.getHeight() || y < 0) {
            remove = true; // Mark the bullet for removal if it goes out of bounds
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public boolean shouldRemove() {
        return remove;
    }
}
