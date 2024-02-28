package objects.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class RemotePlayer {
    private float x, y;
    private int livesCount;

    public RemotePlayer(float x, float y, int livesCount) {
        this.x = x;
        this.y = y;
        this.livesCount = livesCount;
    }

    public void render(SpriteBatch batch, Vector2 playerDimensions) {
        // render the remote player
        Texture playerTexture = new Texture("badlogic.jpg");
        batch.draw(playerTexture, x - playerDimensions.x / 2, y - playerDimensions.y / 2, playerDimensions.x, playerDimensions.y);
    }

    public int getLivesCount() {
        return this.livesCount;
    }
}
