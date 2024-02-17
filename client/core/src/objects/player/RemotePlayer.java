package objects.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RemotePlayer {
    private float x, y;

    public RemotePlayer(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch, Player player) {
        // render the remote player
        Texture playerTexture = new Texture("badlogic.jpg");
        batch.draw(playerTexture, x - player.getDimentions().x / 2, y - player.getDimentions().y / 2, player.getDimentions().x, player.getDimentions().y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
