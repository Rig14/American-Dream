package objects.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static helper.Textures.BIDEN_TEXTURE;
import static helper.Textures.OBAMA_TEXTURE;
import static helper.Textures.TRUMP_TEXTURE;

public class RemotePlayer {
    private float x, y;
    private String character = "";

    public RemotePlayer(float x, float y, String name) {
        this.x = x;
        this.y = y;
        if (name != null) {
            this.character = name.split("_")[0];
        }
    }

    public void render(SpriteBatch batch, Vector2 playerDimensions) {
        // render the remote player
        if (character.equals("Biden")) {
            batch.draw(BIDEN_TEXTURE, x - playerDimensions.x / 2, y - playerDimensions.y / 2, playerDimensions.x, playerDimensions.y);
        } else if (character.equals("Trump")) {
            batch.draw(TRUMP_TEXTURE, x - playerDimensions.x / 2, y - playerDimensions.y / 2, playerDimensions.x, playerDimensions.y);
        } else {
            batch.draw(OBAMA_TEXTURE, x - playerDimensions.x / 2, y - playerDimensions.y / 2, playerDimensions.x, playerDimensions.y);
        }
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

}
