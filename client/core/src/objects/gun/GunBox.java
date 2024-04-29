package objects.gun;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import helper.Textures;


public class GunBox {
    private Integer id;
    private Body body;

    public GunBox(Body body) {
        this.body = body;
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

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return new Vector2(body.getPosition().x, body.getPosition().y);
    }

    public void render(SpriteBatch batch) {
        // System.out.println("gunbox x: " + body.getPosition().x + " gunbox y: " + body.getPosition().y);
        batch.draw(Textures.GUNBOX_TEXTURE, body.getPosition().x, body.getPosition().y, 64, 64);
    }
}
