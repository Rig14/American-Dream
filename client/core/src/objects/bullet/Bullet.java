package objects.bullet;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import ee.taltech.americandream.AmericanDream;
import helper.Constants;
import helper.packet.BulletPositionMessage;
import java.util.ArrayList;
import java.util.List;

import static helper.Constants.BOUNDS;

public class Bullet {
    private static final float SPEED = Constants.BULLET_SPEED; // adjust speed as needed
    private static Texture texture;
    private float x, y;
    private Vector2 velocity;
    public boolean remove = false;
    private List<Bullet> bullets = new ArrayList<>();



    public Bullet(float playerX, float playerY, boolean shootRight) {

        this.x = playerX;
        this.y = playerY;
        velocity = new Vector2(shootRight ? SPEED : -SPEED, 0); // adjust direction based on shootRight flag


    }

    public void update(float deltaTime, Vector2 center) {

        x += velocity.x * deltaTime;
        y += velocity.y * deltaTime;
        if (x > center.x + BOUNDS || x < center.x - BOUNDS || y > center.y + BOUNDS || y < center.y - BOUNDS) {
            remove = true;
        }
        // construct bullet position message to be sent to the server
        BulletPositionMessage positionMessage = new BulletPositionMessage();
        positionMessage.x = x;
        positionMessage.y = y;
        positionMessage.speedBullet = Constants.BULLET_SPEED;
        // send player bullet message to the server
        AmericanDream.client.sendUDP(positionMessage);
    }



    public boolean shouldRemove() {
        return remove;
    }
}
