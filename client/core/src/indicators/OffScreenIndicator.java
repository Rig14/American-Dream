package indicators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import helper.PlayerState;

import java.util.Optional;

import static helper.Constants.CAMERA_ZOOM;
import static helper.Textures.BULLET_TEXTURE;

public class OffScreenIndicator {
    private final Vector2 playerDimensions;
    private final Texture indicator = BULLET_TEXTURE; // rendered as the indicator
    private float time;

    public OffScreenIndicator(Vector2 playerDimensions) {
        this.playerDimensions = playerDimensions;
        this.time = 0;
    }

    // Render indicator when local or remote player is off-screen
    // Can handle changing zoom constant and replacing current texture with other similar size textures
    public void renderIndicators(SpriteBatch batch, Camera camera, Optional<PlayerState[]> allPlayerStates) {
        time += Gdx.graphics.getDeltaTime();
        if (allPlayerStates.isPresent()) {
            for (PlayerState player : allPlayerStates.get()) {
                if (!camera.frustum.boundsInFrustum(player.x, player.y, 0, playerDimensions.x, playerDimensions.y, 0)) {
                    // construct vector from center point to player
                    Vector2 direction = new Vector2(player.x - camera.position.x, player.y - camera.position.y);
                    // normalise into unit vector
                    direction.nor();

                    // length to extend the unit vector by
                    float lambda = (float) Math.min(
                            camera.viewportWidth / (2 * Math.abs(Math.cos(direction.angleRad()))),
                            camera.viewportHeight / (2 * Math.abs(Math.sin(direction.angleRad())))
                    );

                    direction.scl(lambda * (CAMERA_ZOOM - 0.1f));
                    batch.draw(indicator, camera.position.x + direction.x, camera.position.y + direction.y, 10 + (float) Math.abs(Math.sin(time)) * 10, 10 + (float) Math.abs(Math.sin(time)) * 10);
                }
            }
        }
    }

}