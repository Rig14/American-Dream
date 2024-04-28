package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ee.taltech.americandream.AmericanDream;
import helper.Direction;
import helper.PlayerState;
import helper.packet.AddAIMessage;
import helper.packet.BulletMessage;
import helper.packet.PlayerPositionMessage;

import java.util.Objects;
import java.util.Optional;

import static helper.Constants.JUMP_COUNT;
import static helper.Constants.JUMP_FORCE;
import static helper.Constants.PPM;

public class AIPlayer extends Player {


    /**
     * Initialize AI Player.
     *
     * @param width             width of the player object/body
     * @param height            height
     * @param body              object that moves around in the world and collides with other bodies
     * @param selectedCharacter currently defines player's name
     */
    public AIPlayer(float width, float height, Body body, String selectedCharacter) {
        super(width, height, body, selectedCharacter);
    }


    /**
     * Update player data according to input, collisions (platforms) and respawning.
     * Construct and send new playerPositionMessage.
     * @param delta delta time
     * @param center point of the map/world
     */
    @Override
    public void update(float delta, Vector2 center, Optional<PlayerState> playerState) {
        if (playerState.isPresent()) {
            PlayerState ps = playerState.get();
            damage = ps.getDamage();
            ammoCount = ps.getAmmoCount();
            // update server-sided lives here in the future
        }
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        if (livesCount > 0) {  // let the dead player spectate, but ignore its input
            handleInput(delta);
        }
        super.handlePlatform();
        super.handleOutOfBounds(delta, center);  // respawning and decrementing lives
        direction = velX > 0 ? Direction.RIGHT : Direction.LEFT;

        // construct player position message to be sent to the server
        PlayerPositionMessage positionMessage = new PlayerPositionMessage();
        positionMessage.x = x;
        positionMessage.y = y;
        positionMessage.direction = Direction.LEFT;
        positionMessage.livesCount = livesCount;
        positionMessage.name = "AI";
        positionMessage.velX = velX;
        positionMessage.velY = velY;
        positionMessage.isShooting = isShooting;

        // send player position message to the server
        AmericanDream.client.sendUDP(positionMessage);
    }


    /**
     * Handle mouse and keyboard input.
     * Update the speed of the player body according to user input.
     */
    @Override
    protected void handleInput(float delta) {
        Controller controller = Controllers.getCurrent();
        velX = 0;
        // Moving right
//        if (Gdx.input.isKeyPressed(Input.Keys.D) || (controller != null &&
//                (controller.getButton(controller.getMapping().buttonDpadRight) ||
//                        Objects.requireNonNull(controller).getAxis(controller.getMapping().axisLeftX) > 0.5f
//                ))) {
//            velX = 1;
//        }
//        // Moving left
//        if (Gdx.input.isKeyPressed(Input.Keys.A) || (controller != null &&
//                (controller.getButton(controller.getMapping().buttonDpadLeft) ||
//                        Objects.requireNonNull(controller).getAxis(controller.getMapping().axisLeftX) < -0.5f))) {
//            velX = -1;
//        }
//
//        // Jumping
//        if (jumpCounter < JUMP_COUNT && Gdx.input.isKeyJustPressed(Input.Keys.W) || (controller != null &&
//                controller.getButton(controller.getMapping().buttonA))) {
//            float force = body.getMass() * JUMP_FORCE;
//            body.setLinearVelocity(body.getLinearVelocity().x, 0);
//            body.applyLinearImpulse(new Vector2(0, force), body.getWorldCenter(), true);
//            jumpCounter++;
//        }
//
//        // key down on platform
//        if (Gdx.input.isKeyPressed(Input.Keys.S) || (controller != null &&
//                (controller.getButton(controller.getMapping().buttonDpadDown) ||
//                        Objects.requireNonNull(controller).getAxis(controller.getMapping().axisLeftY) > 0.5f))) {
//            keyDownTime += delta;
//        } else {
//            keyDownTime = 0;
//        }
//
//        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
//            // spawn AI player
//            AmericanDream.client.sendTCP(new AddAIMessage());
//        }

        // reset jump counter if landed (sometimes stopping in midair works as well)
        if (body.getLinearVelocity().y == 0) {
            // body y velocity must main 0 for some time to reset jump counter
            if (jumpCounterResetTime > 0.1f) {
                jumpCounter = 0;
                jumpCounterResetTime = 0;
            }
            jumpCounterResetTime += delta;
        }
        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y);

        // check for shooting input
        //shootingInput();
    }

    /**
     * Check for shooting input.
     * Create and send new BulletMessage if the player is shooting.
     */
    @Override
    public void shootingInput() {
        isShooting = 0;
        BulletMessage bulletMessage = new BulletMessage();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                (Controllers.getCurrent() != null &&
                        Controllers.getCurrent().getAxis(Controllers.getCurrent().getMapping().axisRightX) > 0.5f)) {
            bulletMessage.direction = Direction.RIGHT;
            isShooting = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                (Controllers.getCurrent() != null &&
                        Controllers.getCurrent().getAxis(Controllers.getCurrent().getMapping().axisRightX) < -0.5f)) {
            bulletMessage.direction = Direction.LEFT;
            isShooting = -1;
        }
        bulletMessage.name = "AI";
        AmericanDream.client.sendTCP(bulletMessage);
    }
}
