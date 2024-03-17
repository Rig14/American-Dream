package objects.player;

import animation.PlayerAnimations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import static helper.Constants.FRAME_HEIGHT;
import static helper.Constants.FRAME_WIDTH;

public class RemotePlayer {
    private float x, y;
    private float velX, velY;
    private PlayerAnimations playerAnimations;
    private int isShooting;
    public enum State { WALKING, IDLE, JUMPING, SHOOTING }

    public RemotePlayer(float x, float y, TextureAtlas textureAtlas, float velX, float velY, int isShooting) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.isShooting = isShooting;
        this.playerAnimations = new PlayerAnimations(textureAtlas);
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public int isShooting() {
        return isShooting;
    }

    public void render(SpriteBatch batch, Vector2 playerDimensions) {
        // Render the remote player based on its velocity
        TextureRegion currentFrame = playerAnimations.getFrameRemote(Gdx.graphics.getDeltaTime(), this);
        batch.draw(currentFrame, x - playerDimensions.x / 2 - 15, y - playerDimensions.y / 2, FRAME_WIDTH, FRAME_HEIGHT);
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
