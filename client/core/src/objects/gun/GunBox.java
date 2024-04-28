package objects.gun;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import helper.Textures;


public class GunBox {
    private float x, y;
    private Integer id;
    private float velY = 200; // initial velocity


    public GunBox(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void render(SpriteBatch batch, float deltaTime) {
        System.out.println(y);
        y -= velY * deltaTime;
        batch.draw(Textures.GUNBOX_TEXTURE, x, y);
        if (y == 0) {
            velY = 0;
        }
    }
}
