package objects.bullet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class RemoteBullet {
    private float x, y;

    public RemoteBullet(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch, Vector2 bulletDimensions) {
        // render the remote bullet
        Texture bulletTexture = new Texture("bullet2-transformed.jpg");
        batch.draw(bulletTexture, x - bulletDimensions.x / 2, y - bulletDimensions.y / 2, bulletDimensions.x, bulletDimensions.y);
    }
}
