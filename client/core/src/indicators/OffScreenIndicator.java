package indicators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import helper.PlayerState;

import java.util.Optional;

import static helper.Constants.CAMERA_ZOOM;
import static helper.Textures.BULLET_TEXTURE;

public class OffScreenIndicator {

    // For some unknown reason increasing zoom constant over 2f causes the indicator to gradually "move outwards" so that
    // only half of it is visible. The magic of floats, powers and CAMERA_ZOOM const prevents that from happening. All
    // that while not disturbing lower zoom levels. Magic ☺.

    // Indicator appears a little before player goes fully off-screen, prevents barely visible player
    public static final float PLAYER_BARELY_VISIBLE = (float) Math.pow(CAMERA_ZOOM - 0.5f, 5f);

    // Could 266 and 333 somehow be tied to map size or tilemap?
    public static final float OFFSCREEN_Y = 266.6f * CAMERA_ZOOM - PLAYER_BARELY_VISIBLE;
    public static final float OFFSCREEN_Y_NEG = -266.6f * CAMERA_ZOOM + PLAYER_BARELY_VISIBLE;
    public static final float OFFSCREEN_X = 333.3f * CAMERA_ZOOM - PLAYER_BARELY_VISIBLE;
    public static final float OFFSCREEN_X_NEG = -333.3f * CAMERA_ZOOM + PLAYER_BARELY_VISIBLE;


    private final Vector2 playerDimensions;
    private final Texture indicator = BULLET_TEXTURE; // rendered as the indicator

    public OffScreenIndicator(Vector2 playerDimensions) {
        this.playerDimensions = playerDimensions;
    }

    // Render indicator when local or remote player is off-screen
    // Can handle changing zoom constant and replacing current texture with other similar size textures
    public void renderIndicators(SpriteBatch batch, Vector3 cameraPosition, Optional<PlayerState[]> allPlayerStates) {

        float cameraX = cameraPosition.x;
        float cameraY = cameraPosition.y;

        if (allPlayerStates.isPresent()) {
            for (PlayerState player : allPlayerStates.get()) {

                // Check if remote player is off-screen and handle corners "FizzBuzz" style
                float cameraDeltaX = player.getX() - cameraX;
                float cameraDeltaY = player.getY() - cameraY;

                // right
                if (cameraDeltaX > OFFSCREEN_X) {
                    // up right
                    if (cameraDeltaY > OFFSCREEN_Y) {
                        batch.draw(indicator, cameraX + OFFSCREEN_X - indicator.getWidth() * 1.25f,
                                cameraY + OFFSCREEN_Y - indicator.getHeight() * 2.5f);
                    // down right
                    } else if (cameraDeltaY < OFFSCREEN_Y_NEG) {
                        batch.draw(indicator, cameraX + OFFSCREEN_X - indicator.getWidth() * 1.25f,
                                cameraY - OFFSCREEN_Y + indicator.getHeight() * 1.5f);
                    // right
                    } else {
                        batch.draw(indicator, cameraX + OFFSCREEN_X - indicator.getWidth() * 1.25f,
                                player.getY() - playerDimensions.y / 2 + indicator.getHeight() / 2f);
                    }

                // left
                } else if (cameraDeltaX < OFFSCREEN_X_NEG) {
                    // up left
                    if (cameraDeltaY > OFFSCREEN_Y) {
                        batch.draw(indicator, cameraX - OFFSCREEN_X + indicator.getWidth() * 0.25f,
                                cameraY + OFFSCREEN_Y - indicator.getHeight() * 2.5f);
                    // down left
                    } else if (cameraDeltaY < OFFSCREEN_Y_NEG) {
                        batch.draw(indicator, cameraX - OFFSCREEN_X + indicator.getWidth() * 0.25f,
                                cameraY - OFFSCREEN_Y + indicator.getHeight() * 1.5f);
                    // left
                    } else {
                        batch.draw(indicator, cameraX - OFFSCREEN_X + indicator.getWidth() * 0.25f,
                                player.getY() - playerDimensions.y / 2 + indicator.getHeight() / 2f);
                    }

                // connecting playerDimensions and texture.getWidth() is likely impossible without magic numbers and weird logic
                // up
                } else if (cameraDeltaY > OFFSCREEN_Y) {
                    batch.draw(indicator, player.getX() - playerDimensions.x / 2 - 15,
                            cameraY + OFFSCREEN_Y - indicator.getHeight() * 2.5f);

                // down
                } else if (cameraDeltaY < OFFSCREEN_Y_NEG) {
                    batch.draw(indicator, player.getX() - playerDimensions.x / 2 - 15,
                            cameraY - OFFSCREEN_Y + indicator.getHeight() * 1.5f);
                }
            }
        }
    }

}