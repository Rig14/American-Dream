package objects.bullet;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static helper.Constants.BULLET_DIMENSIONS;
import static helper.Textures.BULLET_TEXTURE;

public class RemoteBullet {

    /**
     * Render remote bullets.
     * @param x bullet x coordinate
     * @param y y coordinate
     */
    public static void render(SpriteBatch batch, float x, float y) {
        // render the remote bullet
        batch.draw(BULLET_TEXTURE, x - BULLET_DIMENSIONS.x / 2, y - BULLET_DIMENSIONS.y / 2, BULLET_DIMENSIONS.x, BULLET_DIMENSIONS.y);
    }
}
