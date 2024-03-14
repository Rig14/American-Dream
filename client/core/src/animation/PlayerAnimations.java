package animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.Text;

public class PlayerAnimations {
    private final TextureAtlas textureAtlas;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> walkLeftAnimation;
    private final Animation<TextureRegion> idleAnimation;

    public PlayerAnimations() {
        textureAtlas = new TextureAtlas(Gdx.files.internal("spriteatlas/SoldierSprites.atlas"));
        walkAnimation = createAnimation("soldier-walk", 1f, Animation.PlayMode.LOOP);
        walkLeftAnimation = generateWalkLeftAnimation();
        idleAnimation = createAnimation("soldier-idle", 1f, Animation.PlayMode.LOOP);
    }

    public Animation<TextureRegion> getWalkAnimation() {
        return walkAnimation;
    }
    public Animation<TextureRegion> generateWalkLeftAnimation() {
        Array<TextureAtlas.AtlasRegion> frames = textureAtlas.findRegions("soldier-walk");
        Array<TextureAtlas.AtlasRegion> flippedFrames = new Array<>();

        for (TextureAtlas.AtlasRegion region : frames) {
            TextureAtlas.AtlasRegion flippedRegion = new TextureAtlas.AtlasRegion(region);
            flippedRegion.flip(true, false);
            flippedFrames.add(flippedRegion);
        }

        return new Animation<>(1f, flippedFrames, Animation.PlayMode.LOOP);
    }
    public Animation<TextureRegion> getWalkLeftAnimation() {
        return walkLeftAnimation;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        return idleAnimation;
    }


    private Animation<TextureRegion> createAnimation(String regionName, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureAtlas.AtlasRegion> frames = textureAtlas.findRegions(regionName);
        return new Animation<>(frameDuration, frames, playMode);
    }
}
