package objects.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static helper.Textures.BIDEN_TEXTURE;

public class RemotePlayer {
    private float x, y;

    public RemotePlayer(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch, Vector2 playerDimensions) {
        // render the remote player
        batch.draw(BIDEN_TEXTURE, x - playerDimensions.x / 2, y - playerDimensions.y / 2, playerDimensions.x, playerDimensions.y);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

}
