package objects.player;

import animation.PlayerAnimations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import helper.packet.GameStateMessage;
import helper.packet.PlayerPositionMessage;

import static helper.Constants.FRAME_HEIGHT;
import static helper.Constants.FRAME_WIDTH;
import static helper.Textures.BIDEN_TEXTURE;

public class RemotePlayer {
    private float x, y;
    private float velX, velY;
    private PlayerAnimations playerAnimations;
    private final Vector2 velocity; // Velocity of the remote player
    public enum State { WALKING, IDLE, JUMPING }

    public RemotePlayer(float x, float y, TextureAtlas textureAtlas, float velX, float velY) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.velocity = new Vector2(0, 0);
        this.playerAnimations = new PlayerAnimations(textureAtlas);
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public void render(SpriteBatch batch, Vector2 playerDimensions) {
        // Render the remote player based on its velocity
        TextureRegion currentFrame = playerAnimations.getFrameRemote(Gdx.graphics.getDeltaTime(), this);
        batch.draw(currentFrame, x - playerDimensions.x / 2, y - playerDimensions.y / 2, playerDimensions.x, playerDimensions.y);
        System.out.println("x: " + getVelX());
        System.out.println("y: " + getVelY());
    }
    public void update(float delta) {
        playerAnimations.updateRemote(delta, this);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

}
