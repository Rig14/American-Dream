package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import ee.taltech.americandream.AmericanDream;
import helper.Direction;
import helper.packet.PlayerPositionMessage;

import static helper.Constants.*;

public class Player extends GameEntity {
    private final float speed;

    private int jumpCounter;

    private float keyDownTime = 0;

    public Player(float width, float height, Body body) {
        super(width, height, body);
        this.speed = PLAYER_SPEED;
        this.jumpCounter = 0;
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        handleInput(delta);
        handlePlatform();

        // construct player position message to be sent to the server
        PlayerPositionMessage positionMessage = new PlayerPositionMessage();
        positionMessage.x = x;
        positionMessage.y = y;
        positionMessage.direction = Direction.LEFT;
        // send player position message to the server
        AmericanDream.client.sendUDP(positionMessage);
    }

    private void handleInput(float delta) {
        velX = 0;
        // Moving right
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = 1;
        }
        // Moving left
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -1;
        }

        // Jumping
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && jumpCounter < JUMP_COUNT) {
            float force = body.getMass() * JUMP_FORCE;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getWorldCenter(), true);
            jumpCounter++;
        }

        // key down on platform
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            keyDownTime += delta;
        } else {
            keyDownTime = 0;
        }

        // reset jump counter if landed (sometimes stopping in midair works as well)
        if (body.getLinearVelocity().y == 0) {
            jumpCounter = 0;
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y);
    }

    /*
     * Handle the platform.
     * If player is below the platform, move it some random distance to the right
     * If player is above the platform, move it back to the original position
     * TODO: Make the logic less hacky
     */
    private void handlePlatform() {
        Array<Body> bodies = new Array<Body>();
        body.getWorld().getBodies(bodies);

        for (Body b : bodies) {
            if (b.getUserData() != null && b.getUserData().toString().contains("platform")) {
                float height = Float.parseFloat(b.getUserData().toString().split(":")[1]);
                height = height / PPM;
                if (body.getPosition().y - this.height / PPM >= height && b.getPosition().x >= 2000 && (keyDownTime == 0 || keyDownTime > PLATFORM_DESCENT * 1.1)) {
                    // bring back platform
                    b.setTransform(b.getPosition().x - 2000, b.getPosition().y, 0);
                } else if ((body.getPosition().y - 2 < height || (keyDownTime >= PLATFORM_DESCENT && keyDownTime <= PLATFORM_DESCENT * 1.1)) && b.getPosition().x <= 2000) {
                    // remove platform
                    b.setTransform(b.getPosition().x + 2000, b.getPosition().y, 0);
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // here we can draw the player sprite eventually
    }

    public Vector2 getPosition() {
        return body.getPosition().scl(PPM);
    }

    /*
     * Get the dimensions of the player
     * (width, height)
     */
    public Vector2 getDimensions() {
        return new Vector2(width, height);
    }
}
