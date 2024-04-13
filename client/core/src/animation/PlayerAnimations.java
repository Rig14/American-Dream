package animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import objects.player.Player;
import objects.player.RemotePlayer;

import static helper.Constants.*;


public class PlayerAnimations {

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> shootAnimation;
    private Animation<TextureRegion> walkAnimationRemote;
    private Animation<TextureRegion> idleAnimationRemote;
    private Animation<TextureRegion> shootAnimationRemote;
    private final TextureAtlas textureAtlas;
    private Player.State currentState;
    private RemotePlayer.State currentStateRemote;
    private Player.State previousState;
    private RemotePlayer.State previousStateRemote;
    private float stateTimer;
    private float stateTimerRemote;
    // used for flipping the idle animation based on the last movement or shooting command
    private boolean lastWalkedRight = false;
    private boolean lastWalkedRightRemote = false;


    public PlayerAnimations (TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
        stateTimer = 0;
        stateTimerRemote = 0;
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

        if (!lastWalkedRight && !region.isFlipX()) {
            region.flip(true, false);
        } else if (lastWalkedRight && region.isFlipX()) {
            region.flip(true, false);
        }
        if (player.getVelX() < 0 && !region.isFlipX()) {
            region.flip(true, false);
            lastWalkedRight = false;
        } else if (player.getVelX() > 0 && region.isFlipX()) {
            region.flip(true, false);
            lastWalkedRight = true;
        }
        if (player.isShooting() < 0 && !region.isFlipX()) {
            region.flip(true, false);
            lastWalkedRight = false;
        } else if (player.isShooting() > 0 && region.isFlipX()) {
            region.flip(true, false);
            lastWalkedRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }
    public TextureRegion getFrameRemote(float delta, RemotePlayer player) {
        currentStateRemote = getStateRemote(player);
        TextureRegion region = null;
        if (currentStateRemote == RemotePlayer.State.SHOOTING && shootAnimationRemote != null) {
            region = shootAnimationRemote.getKeyFrame(stateTimerRemote);
        } else if (currentStateRemote == RemotePlayer.State.WALKING && walkAnimationRemote != null) {
            region = walkAnimationRemote.getKeyFrame(stateTimerRemote);
        } else if (idleAnimationRemote != null) {
            region = idleAnimationRemote.getKeyFrame(stateTimerRemote);
        }
        if (region != null) {
            if (!lastWalkedRightRemote && !region.isFlipX()) {
                region.flip(true, false);
            } else if (lastWalkedRightRemote && region.isFlipX()) {
                region.flip(true, false);
            }
            if (player.getVelX() < 0 && !region.isFlipX()) {
                region.flip(true, false);
                lastWalkedRightRemote = false;
            } else if (player.getVelX() > 0 && region.isFlipX()) {
                region.flip(true, false);
                lastWalkedRightRemote = true;
            }
            if (player.isShooting() < 0 && !region.isFlipX()) {
                region.flip(true, false);
                lastWalkedRightRemote = false;
            } else if (player.isShooting() > 0 && region.isFlipX()) {
                region.flip(true, false);
                lastWalkedRightRemote = true;
            }
        }
        stateTimerRemote = currentStateRemote == previousStateRemote ? stateTimerRemote + delta : 0;
        previousStateRemote = currentStateRemote;
        return region;
    }
    public RemotePlayer.State getStateRemote(RemotePlayer player) {
        if (player.isShooting() != 0) {
            return RemotePlayer.State.SHOOTING;
        } else if (player.getVelX() < 0 || player.getVelX() > 0) {
            return RemotePlayer.State.WALKING;
        } else if (player.getVelY() > 0 || player.getVelY() < 0 && previousStateRemote == RemotePlayer.State.JUMPING) {
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
        // System.out.println("current: " + currentStateRemote);
        // System.out.println("previous: " + previousStateRemote);
        if (currentStateRemote != previousStateRemote) {
            stateTimerRemote = 0;
            previousStateRemote = currentStateRemote;
        } else {
            stateTimerRemote += delta;
        }
        // System.out.println("delta :" + stateTimerRemote);
        // System.out.println("Remote update stateTimer:" + stateTimerRemote);
    }
    public void update(float delta, Player player) {
        currentState = getState(player);
        // System.out.println("currentState: " + currentState);
        // System.out.println("previousState: " + previousState);
        if (currentState != previousState) {
            stateTimer = 0;
            previousState = currentState;
        } else {
            stateTimer += delta;
        }
        // System.out.println(stateTimer);
    }
    public void generateObama() {
        walkAnimation = createAnimation("soldier-walk", 7, FRAME_DURATION);
        idleAnimation = createAnimation("soldier-idle", 7, FRAME_DURATION);
        shootAnimation = createAnimation("soldier-shoot", 4, FRAME_DURATION);
    }
    public void generateObamaRemote() {
        walkAnimationRemote = createAnimation("soldier-walk", 7, FRAME_DURATION);
        idleAnimationRemote = createAnimation("soldier-idle", 7, FRAME_DURATION);
        shootAnimationRemote = createAnimation("soldier-shoot", 4, FRAME_DURATION);
    }
    public void generateTrump() {
        walkAnimation = createAnimation("soldier2-walk", 8, FRAME_DURATION);
        idleAnimation = createAnimation("soldier2-idle", 9, FRAME_DURATION);
        shootAnimation = createAnimation("soldier2-shoot", 4, FRAME_DURATION);
    }
    public void generateTrumpRemote() {
        walkAnimationRemote = createAnimation("soldier2-walk", 8, FRAME_DURATION);
        idleAnimationRemote = createAnimation("soldier2-idle", 9, FRAME_DURATION);
        shootAnimationRemote = createAnimation("soldier2-shoot", 4, FRAME_DURATION);
    }
    public void generateBiden() {
        walkAnimation = createAnimation("soldier3-walk", 8, FRAME_DURATION);
        idleAnimation = createAnimation("soldier3-idle", 7, FRAME_DURATION);
        shootAnimation = createAnimation("soldier3-shoot", 4, FRAME_DURATION);
    }
    public void generateBidenRemote() {
        walkAnimationRemote = createAnimation("soldier3-walk", 8, FRAME_DURATION);
        idleAnimationRemote = createAnimation("soldier3-idle", 7, FRAME_DURATION);
        shootAnimationRemote = createAnimation("soldier3-shoot", 4, FRAME_DURATION);
    }

    public Animation<TextureRegion> getWalkAnimation() {
        return walkAnimation;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        return idleAnimation;
    }
}
