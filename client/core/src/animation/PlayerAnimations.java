package animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import objects.player.Player;
import objects.player.RemotePlayer;

import static helper.Constants.*;


public class PlayerAnimations {

    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> shootAnimation;
    private final Animation<TextureRegion> walkAnimationRemote;
    private final Animation<TextureRegion> idleAnimationRemote;
    private final Animation<TextureRegion> shootAnimationRemote;
    private final TextureAtlas textureAtlas;
    private Player.State currentState;
    private RemotePlayer.State currentStateRemote;
    private Player.State previousState;
    private RemotePlayer.State previousStateRemote;
    private float stateTimer;


    public PlayerAnimations (TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
        walkAnimation = createAnimation("soldier-walk", 7, FRAME_DURATION);
        idleAnimation = createAnimation("soldier-idle", 7, FRAME_DURATION);
        shootAnimation = createAnimation("soldier-shoot", 4, FRAME_DURATION);
        walkAnimationRemote = createAnimation("soldier2-walk", 8, FRAME_DURATION);
        idleAnimationRemote = createAnimation("soldier2-idle", 9, FRAME_DURATION);
        shootAnimationRemote = createAnimation("soldier2-shoot", 4, FRAME_DURATION);

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
        if (currentState == Player.State.SHOOTING) {
            region = shootAnimation.getKeyFrame(stateTimer);
        } else if (currentState == Player.State.WALKING) {
            region = walkAnimation.getKeyFrame(stateTimer);
        } else {
            region = idleAnimation.getKeyFrame(stateTimer);
        }
        if (player.getVelX() < 0 && !region.isFlipX()) {
            region.flip(true, false);
        } else if (player.getVelX() > 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        if (player.isShooting() < 0 && !region.isFlipX()) {
            region.flip(true, false);
        } else if (player.isShooting() > 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }
    public TextureRegion getFrameRemote(float delta, RemotePlayer player) {
        currentStateRemote = getStateRemote(player);
        TextureRegion region;
        if (currentStateRemote == RemotePlayer.State.SHOOTING) {
            region = shootAnimationRemote.getKeyFrame(stateTimer);
        } else if (currentStateRemote == RemotePlayer.State.WALKING) {
            region = walkAnimationRemote.getKeyFrame(stateTimer);
        } else {
            region = idleAnimationRemote.getKeyFrame(stateTimer);
        }
        if (player.getVelX() < 0 && !region.isFlipX()) {
            region.flip(true, false);
        } else if (player.getVelX() > 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        if (player.isShooting() < 0 && !region.isFlipX()) {
            region.flip(true, false);
        } else if (player.isShooting() > 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        stateTimer = currentStateRemote == previousStateRemote ? stateTimer + delta : 0;
        previousStateRemote = currentStateRemote;
        return region;
    }
    public RemotePlayer.State getStateRemote(RemotePlayer player) {
        if (player.isShooting() != 0) {
            return RemotePlayer.State.SHOOTING;
        } else if (player.getVelX() < 0 || player.getVelX() > 0) {
            return RemotePlayer.State.WALKING;
        } else if (player.getVelY()> 0 || player.getVelY() < 0 && previousStateRemote == RemotePlayer.State.JUMPING) {
            return RemotePlayer.State.JUMPING;
        } else {
            return RemotePlayer.State.IDLE;
        }
    }

    public Player.State getState(Player player) {
        if (player.isShooting() != 0) {
            return Player.State.SHOOTING;
        } if (player.getVelX() < 0 || player.getVelX() > 0) {
            return Player.State.WALKING;
        } else if (player.getVelY() > 0 || player.getVelY() < 0 && previousState == Player.State.JUMPING) {
            return Player.State.JUMPING;
        } else {
            return Player.State.IDLE;
        }
    }
    public void updateRemote(float delta, RemotePlayer player) {
        currentStateRemote = getStateRemote(player);
        if (currentStateRemote != previousStateRemote) {
            stateTimer = 0;
            previousStateRemote = currentStateRemote;
        } else {
            stateTimer += delta;
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