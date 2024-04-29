package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ee.taltech.americandream.AmericanDream;
import helper.BulletData;
import helper.Direction;
import helper.PlayerState;
import helper.packet.BulletMessage;
import helper.packet.PlayerPositionMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static helper.Constants.FRAME_HEIGHT;
import static helper.Constants.FRAME_WIDTH;
import static helper.Constants.JUMP_COUNT;
import static helper.Constants.JUMP_FORCE;
import static helper.Constants.PPM;
import static helper.Constants.REMOTE_PLAYER_INDICATORS;
import static helper.Textures.GPT_TEXTURE;
import static helper.Textures.PLAYER_INDICATOR_TEXTURE;

public class AIPlayer extends Player {

    private String movingState = "";
    private String shootingState = "";
    private boolean jumpingState = false;
    private boolean dropThroughPlatformState = false;
    private Optional<List<BulletData>> bullets;
    private Player realPlayer;

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
     * Plan method as per SPA arhitectue.
     * Different scenarios sorted by priority (ascending),
     * more critical scenario will override previous "state" or "commands".
     * TODO: Rename previous AI player top "ufo" or something. This will prevent nullpointerexceptions and naming conflicts.
     */
    private void plan() {
        // initial position
        if (x < 1350) movingState = "right";

        // shoot towards real player
        shootingState = (realPlayer.getPosition().x < x) ? "left" : "right";

        // dodge bullets
        if (bullets.isPresent() && !bullets.get().isEmpty()) {
            List<BulletData> enemyBullets = bullets.get().stream()
                    .filter(x -> !(x.name.equals("AI")))
                    .collect(Collectors.toList());
            // enemyBullets.forEach(x -> System.out.println(x.x + "   " + x.y + "   " + x.speedBullet));
            if (enemyBullets.stream().anyMatch(bul -> (bul.y > y - 45)  // check if bullet is at the same level as AI
                    && ((bul.speedBullet > 0 && bul.x < x + 50) || (bul.speedBullet < 0 && bul.x > x - 50))  // x coord
                    && Math.abs(x - bul.x) < 200)) jumpingState = true;  // prevent AI from jumping too early
        }

        // recover from being hit
        if (x > 1375 && bulletHitForce != 0f) {
            movingState = "left";
            jumpingState = true;
        }

        if (jumpingState && body.getLinearVelocity().y > 0) jumpingState = false;  // avoid using all 3 jumps right away
    }

    /**
     * Update player data according to input, collisions (platforms) and respawning.
     * Construct and send new playerPositionMessage.
     * @param delta delta time
     * @param center point of the map/world
     */
    @Override
    public void update(float delta, Vector2 center, Optional<PlayerState> playerState, Optional<List<BulletData>> bullets, Player player) {
        this.bullets = bullets;
        this.realPlayer = player;  // could be set in constructor
        if (playerState.isPresent()) {
            PlayerState ps = playerState.get();
            damage = ps.getDamage();
            ammoCount = ps.getAmmoCount();
            if (ps.getApplyForce() != 0) bulletHitForce = ps.getApplyForce();
            // update server-sided lives here in the future
        }
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        plan();
        if (livesCount > 0) {  // let the dead player spectate, but ignore its input
            handleInput(delta);
        }
        super.applyBulletHitForce();
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
        velX = 0;
        // Moving right
        if (movingState.equals("right")) {
            velX = 1;
        }
        // Moving left
        if (movingState.equals("left")) {
            velX = -1;
        }

        // Jumping
        if (jumpCounter < JUMP_COUNT && jumpingState) {
            float force = body.getMass() * JUMP_FORCE;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getWorldCenter(), true);
            jumpCounter++;
        }

        // key down on platform
        if (dropThroughPlatformState) {
            keyDownTime += delta;
        } else {
            keyDownTime = 0;
        }

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
        shootingInput();
        // reset states after every "spin()" method
        movingState = "";
        jumpingState = false;
    }

    /**
     * Check for shooting input.
     * Create and send new BulletMessage if the player is shooting.
     */
    @Override
    public void shootingInput() {
        isShooting = 0;
        BulletMessage bulletMessage = new BulletMessage();
        if (shootingState.equals("right")) {
            bulletMessage.direction = Direction.RIGHT;
            isShooting = 1;
        }
        if (shootingState.equals("left")) {
            bulletMessage.direction = Direction.LEFT;
            isShooting = -1;
        }
        shootingState = "";
        bulletMessage.name = "AI";
        AmericanDream.client.sendTCP(bulletMessage);
    }

    /**
     * Temporary solution to avoid duplicating names in single player mode.
     */
    @Override
    public String getName() {
        return "AI";
    }

    /**
     * Render player and find the correct animation frame.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (livesCount > 0) {
            batch.draw(GPT_TEXTURE, getPosition().x - getDimensions().x / 2 - 15, getPosition().y - getDimensions().y / 2, FRAME_WIDTH, FRAME_HEIGHT);
            batch.draw(REMOTE_PLAYER_INDICATORS.get(0), getPosition().x - getDimensions().x / 2, getPosition().y - getDimensions().y / 2 + 80, 35, 35);
        }
    }
}
