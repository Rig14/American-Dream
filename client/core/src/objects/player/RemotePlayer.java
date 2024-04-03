package objects.player;

import animation.PlayerAnimations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import static helper.Textures.BIDEN_TEXTURE;
import static helper.Textures.OBAMA_TEXTURE;
import static helper.Textures.TRUMP_TEXTURE;

import static helper.Constants.FRAME_HEIGHT;
import static helper.Constants.FRAME_WIDTH;

public class RemotePlayer {
    private float x, y;
    private float velX, velY;
    private PlayerAnimations playerAnimations;
    private int isShooting;
    public enum State { WALKING, IDLE, JUMPING, SHOOTING }
    private String character;

    public RemotePlayer(float x, float y, String name, TextureAtlas textureAtlas, float velX, float velY, int isShooting) {
        this.x = x;
        this.y = y;
        if (name != null) {
            this.character = name.split("_")[0];
        }
        this.velX = velX;
        this.velY = velY;
        this.isShooting = isShooting;
        this.playerAnimations = new PlayerAnimations(textureAtlas);
        if (character != null && character.contains("Obama")) {
            playerAnimations.generateObamaRemote();
        } else if (character != null && character.contains("Trump")) {
            playerAnimations.generateTrumpRemote();
        } else if (character != null && character.contains("Biden")){
            playerAnimations.generateBidenRemote();
        }
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
        // render the remote player

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
    public String getCharacter() {
        return this.character;
    }

}
