package animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.Text;

public class PlayerAnimations {
    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> idleAnimation;

    public PlayerAnimations() {
        textureAtlas = new TextureAtlas(Gdx.files.internal("spriteatlas/SoldierSprites.atlas"));
        walkAnimation = createAnimation("soldier-walk", 1f, Animation.PlayMode.LOOP);
        walkAnimation = createAnimation("soldier-walk", 1f, Animation.PlayMode.LOOP);
        idleAnimation = createAnimation("soldier-idle", 1f, Animation.PlayMode.LOOP);
    }

    public Animation<TextureRegion> getWalkAnimation() {
        return walkAnimation;
    }
    public Animation<TextureRegion> getWalkLeftAnimation() {
        Array<TextureAtlas.AtlasRegion> frames = textureAtlas.findRegions("soldier-walk");
        for (TextureAtlas.AtlasRegion region : frames) {
            region.flip(true, false);
        }
        Animation<TextureRegion> walkLeftAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
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
