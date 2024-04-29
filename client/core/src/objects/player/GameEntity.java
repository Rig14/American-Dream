package objects.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import helper.BulletData;
import helper.PlayerState;

import java.util.List;
import java.util.Optional;

public abstract class GameEntity {
    protected float x, y, velX, velY, speed;
    protected float width, height;
    protected Body body;

    /**
     * Initialize an object that interacts with the game and responds to player input.
     * @param width width of the player object
     * @param height height
     * @param body object that moves around in the map/world and collides with other bodies
     */
    public GameEntity(float width, float height, Body body) {
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.width = width;
        this.height = height;
        this.body = body;
        this.velX = 0;
        this.velY = 0;
        this.speed = 0;
    }

    public void update(float delta, Vector2 center, Optional<PlayerState> ps){}

    public void update(float delta, Vector2 center, Optional<PlayerState> ps, Optional<List<BulletData>> bullets, Player player){}

    public abstract void render(SpriteBatch batch);

    public Body getBody() {
        return body;
    }
}
