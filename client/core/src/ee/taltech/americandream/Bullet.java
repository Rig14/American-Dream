package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import helper.Constants;
import helper.packet.BulletPositionMessage;


public class Bullet {
    private static final float SPEED = Constants.BULLET_SPEED; // adjust speed as needed
    private static Texture texture;
    private float x, y;
    private Vector2 velocity;
    public boolean remove = false;

    public Bullet(float playerX, float playerY, boolean shootRight) {
        this.x = playerX;
        this.y = playerY;
        velocity = new Vector2(shootRight ? SPEED : -SPEED, 0); // adjust direction based on shootRight flag

    }


    public void update(float deltaTime) {
        x += velocity.x * deltaTime;
        y += velocity.y * deltaTime;
        if (x > Gdx.graphics.getWidth() * 1.7 || x < -200 || y > Gdx.graphics.getHeight() * 1.7 || y < -200) {
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
