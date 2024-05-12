package objects.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ee.taltech.americandream.AmericanDream;
import helper.BulletData;
import helper.Direction;
import helper.PlayerState;
import helper.packet.BulletMessage;
import helper.packet.PlayerPositionMessage;

import java.util.ArrayList;
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

public class AIPlayer extends Player {

    public static final int Y_BUFFER = 45;
    public static final int X_BUFFER = 100;
    private String movingState = "";
    private String shootingState = "";
    private boolean jumpingState = false;
    private Optional<List<BulletData>> bullets;
    private Player realPlayer;

    private int ammoReserve = 10;
    private float shotExtraBulletsDelta = 10f;

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
     * Plan method as per SPA architecture.
     * Different scenarios sorted by priority (ascending), more critical scenario will override previous "state"
     * or "commands".
     */
    private void plan() {

        float realPlayerVelY = realPlayer.getBody().getLinearVelocity().y;
        int desiredPosition = 1375 - (damage * 2);

        List<BulletData> enemyBullets = new ArrayList<>();
        List<BulletData> dangeriousBullets = new ArrayList<>();
        List<BulletData> bulletsGoingToHit = new ArrayList<>();

        // initial position
        if (thisX < desiredPosition - 25) {
            movingState = "right";
        } else if (thisX > desiredPosition + 10) { movingState = "left"; }

        // shoot extra bullets when the realPlayer comes too close (by decreasing ammo reserve)
        if (shotExtraBulletsDelta > 10f &&
                Math.abs(thisX - realPlayer.thisX) < 300 &&
                Math.abs(thisY - realPlayer.thisY) < 20 && realPlayerVelY == 0f) {
            jumpingState = true;
            shotExtraBulletsDelta = 0f;
            ammoReserve -= 3;
        }

        boolean someBodyIsRespawning =
                (thisY > 1150f || thisY < 600f || realPlayer.thisY > 1150f || realPlayer.thisY < 600f)
                && !(thisY > 1200f && realPlayer.thisY > 1200f);  // excludes game start

        // shoot bullets towards the real player  ||  increase ammoReserve during respawning
        if (ammoCount >= ammoReserve && !someBodyIsRespawning) {
            shootingState = (realPlayer.getPosition().x < thisX) ? "left" : "right";
        } else if (someBodyIsRespawning && ammoCount < 9) {
            ammoReserve = ammoCount + 1;
        }

        // realPLayer is on top of AI  (avoid running too far)
        if (Math.abs(thisX - realPlayer.thisX) < 45 &&
                Math.abs(thisY - realPlayer.thisY) < 80 &&
                !(Math.abs(thisX - desiredPosition) > 200)) {
            movingState = "left";
        }

        // analyze bullets
        if (bullets.isPresent() && !bullets.get().isEmpty()) {
            enemyBullets = bullets.get().stream()
                    .filter(x -> !(x.name.equals("AI")))
                    .collect(Collectors.toList());;
            dangeriousBullets = enemyBullets.stream()
                    .filter(bul ->
                            (bul.speedBullet > 0 && bul.x < thisX + X_BUFFER) ||
                            (bul.speedBullet < 0 && bul.x > thisX - X_BUFFER))
                    .collect(Collectors.toList());
            bulletsGoingToHit = dangeriousBullets.stream()
                    .filter(bul -> (bul.y > thisY - Y_BUFFER && bul.y < thisY + Y_BUFFER))
                    .collect(Collectors.toList());
        }

        // recover from being hit  (and overcompensate when in the air, for safety)
        if (Math.abs(thisX - desiredPosition) < 500 && Math.abs(bulletHitForce) > 1) {
            movingState = bulletHitForce > 0 ? "left" : "right";
            jumpingState = true;
        }

        // jump away from enemy bullets
        if (enemyBullets.stream().anyMatch(bul -> (bul.y > thisY - Y_BUFFER && bul.y < thisY + Y_BUFFER)  // check if bullet is at the same level as AI
                && ((bul.speedBullet > 0 && bul.x < thisX + 50) || (bul.speedBullet < 0 && bul.x > thisX - 50))  // x coord
                && Math.abs(thisX - bul.x) < 200)) {
            jumpingState = true;
        }

        // avoid "wall of bullets"  (avoid jumping into bullets above the AI)
        if (dangeriousBullets.size() >= 7 && bulletsGoingToHit.size() <= 3) {
            jumpingState = false;
            movingState = dangeriousBullets.get(0).speedBullet > 0 ? "left" : "right";
        }

        // avoid using all 3 jumps right away
        if (jumpingState && body.getLinearVelocity().y > 0) { jumpingState = false; }
    }

    /**
     * Update player data according to input, collisions (platforms) and respawning.
     * Construct and send new playerPositionMessage.
     * @param delta delta time
     * @param center point of the map/world
     * @param playerState optional of AI player's state
     * @param bullets all bullets in current world
     * @param player real player
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
        thisX = body.getPosition().x * PPM;
        thisY = body.getPosition().y * PPM;
        shotExtraBulletsDelta += delta;
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
        positionMessage.x = thisX;
        positionMessage.y = thisY;
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
     * Act method as per SPA architecture.
     * Implement commands that were given in the plan method.
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
     * Extension of Act (handleInput) method.
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
