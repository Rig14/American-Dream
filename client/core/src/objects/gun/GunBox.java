package objects.gun;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import static helper.Constants.PPM;
import static helper.Textures.GUNBOX_TEXTURE;


public class GunBox {
    private Integer id;
    private Body body;

    public GunBox(Body body, Integer id) {
        this.body = body;
        this.id = id;
        body.setTransform(new Vector2(body.getPosition().x, body.getPosition().y + 30), 0);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    public void remove() {
        if (body != null) {
            World world = body.getWorld();
            if (world != null) {
                world.destroyBody(body);
            }
            body = null;
        }
    }

    public void update() {
        // to prevent gunboxes from falling after they have hit a platform
        if (Math.abs(body.getLinearVelocity().y) < 0.01) { // Adjust the threshold as needed
            // set the body type to static
            body.setType(BodyDef.BodyType.StaticBody);
        }
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        if (body != null) {
            return new Vector2(body.getPosition().x, body.getPosition().y);
        }
        return null;
    }
    public Vector2 getPositionScale() {
        if (body != null) {
            return new Vector2(body.getPosition().x, body.getPosition().y).scl(PPM);
        }
        return null;
    }

    public void applyGravity(Vector2 gravityForce) {
        // reverse the direction of gravity force
        Vector2 reverseGravityForce = gravityForce.cpy().scl(-1);
        body.applyForceToCenter(reverseGravityForce, true);
    }

    public void render(SpriteBatch batch) {
        batch.draw(GUNBOX_TEXTURE, getPositionScale().x - 24, getPositionScale().y - 20, 48, 42);
    }
}
