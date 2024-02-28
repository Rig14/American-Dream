package objects.bullet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class RemoteBullet {
    private final float x, y;
    private final float speed;


    public RemoteBullet(float x, float y, float speed) {
        
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void render(SpriteBatch batch, Vector2 bulletDimensions) {
        // render the remote bullet
        Texture bulletTexture = new Texture("bullet2-transformed.png");
        batch.draw(bulletTexture, x - bulletDimensions.x / 2, y - bulletDimensions.y / 2, bulletDimensions.x, bulletDimensions.y);
    }
}
