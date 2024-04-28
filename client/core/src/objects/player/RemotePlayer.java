package objects.player;

import animation.PlayerAnimations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import helper.PlayerState;

import static helper.Constants.FRAME_HEIGHT;
import static helper.Constants.FRAME_WIDTH;
import static helper.Constants.REMOTE_PLAYER_INDICATORS;
import static helper.Textures.PLAYER_INDICATOR_TEXTURE;

public class RemotePlayer {
    private final float x;
    private final float y;
    private final float velX;
    private final float velY;
    private final PlayerAnimations playerAnimations;
    private final int isShooting;
    private final Integer livesCount;
    private final Integer damage;
    private String character = "";

    public enum State { WALKING, IDLE, JUMPING, SHOOTING }
    private String name = "";

    /**
     * Initialize RemotePlayer that represents a remote client.
     * @param ps PlayerState containing all information about remote player
     * @param textureAtlas object containing player sprites
     */
    public RemotePlayer(PlayerState ps, TextureAtlas textureAtlas) {
        this.x = ps.x;
        this.y = ps.y;
        if (ps.name != null) {
            this.name = ps.name;
        }
        this.velX = ps.velX;
        this.velY = ps.velY;
        this.isShooting = ps.isShooting;
        this.livesCount = ps.livesCount;
        this.damage = ps.damage;
        this.playerAnimations = new PlayerAnimations(textureAtlas);
        if (name != null) {
            this.character = name.split("_")[0];
        }
        if (character != null && character.contains("Obama")) {
            playerAnimations.generateObamaRemote();
        } else if (character != null && character.contains("Trump")) {
            playerAnimations.generateTrumpRemote();
        } else if (character != null) {
            playerAnimations.generateBidenRemote();
        }
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public String getName() {
        return name;
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

    public Integer getLivesCount() {
        return livesCount;
    }

    public Integer getDamage() {
        return damage;
    }

    /**
     * Render remote player.
     * @param batch spritebatch where to render the player
     * @param playerDimensions player size
     */
    public void render(SpriteBatch batch, Vector2 playerDimensions, Integer textureIndex) {
        // Render the remote player based on its velocity
        TextureRegion currentFrame = playerAnimations.getFrameRemote(Gdx.graphics.getDeltaTime(), this);

        if (currentFrame != null) {
            batch.draw(currentFrame, x - playerDimensions.x / 2 - 15, y - playerDimensions.y / 2, FRAME_WIDTH, FRAME_HEIGHT);

            if (textureIndex != -1) {  // prevents nullPointerException during game initialization
                batch.draw(REMOTE_PLAYER_INDICATORS.get(textureIndex),
                        x - playerDimensions.x / 2 + 9, y - playerDimensions.y / 2 + 80, 25, 25);
            }
        }

    }

    /**
     * Update player sprite / animation.
     * @param delta delta time
     */
    public void update(float delta) {
        playerAnimations.updateRemote(delta, this);
        // System.out.println("rp update delta: " + delta);
    }

}
