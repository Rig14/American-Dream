package animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import objects.player.Player;

import static helper.Constants.*;


public class PlayerAnimations {

    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> idleAnimation;
    private final TextureAtlas textureAtlas;
    private Player.State currentState;
    private Player.State previousState;
    private float stateTimer;


    public PlayerAnimations (TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
        walkAnimation = createAnimation("soldier-walk", 7, FRAME_DURATION);
        idleAnimation = createAnimation("soldier-idle", 7, FRAME_DURATION);
        stateTimer = 0;
    }

    private Animation<TextureRegion> createAnimation(String regionName, int frameCount, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            frames.add(new TextureRegion(textureAtlas.findRegion(regionName), i * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }
    public TextureRegion getFrame(float delta, Player player) {
        currentState = getState(player);
        TextureRegion region;
        if (currentState == Player.State.WALKING) {
            region = walkAnimation.getKeyFrame(stateTimer);
        } else {
            region = idleAnimation.getKeyFrame(stateTimer);
        }
        if (player.getVelX() < 0 && !region.isFlipX()) {
            region.flip(true, false);
        } else if (player.getVelX() > 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    public Player.State getState(Player player) {
        if (player.getVelX() < 0 || player.getVelX() > 0) {
            return Player.State.WALKING;
        } else if (player.getVelY() > 0 || player.getVelY() < 0 && previousState == Player.State.JUMPING) {
            return Player.State.JUMPING;
        } else {
            return Player.State.IDLE;
        }
    }
    public void update(float delta, Player player) {
        currentState = getState(player);
        if (currentState != previousState) {
            stateTimer = 0;
            previousState = currentState;
        } else {
            stateTimer += delta;
        }
    }

    public Animation<TextureRegion> getWalkAnimation() {
        return walkAnimation;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        return idleAnimation;
    }
}