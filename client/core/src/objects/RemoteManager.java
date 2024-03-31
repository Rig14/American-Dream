package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import helper.BulletData;
import helper.PlayerState;
import helper.packet.GameStateMessage;
import objects.bullet.RemoteBullet;
import objects.player.RemotePlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static helper.Constants.GRAVITY;

public class RemoteManager {
    private RemotePlayer[] remotePlayers;
    private Integer gameTime = null;
    private PlayerState remotePlayerState = null;
    private PlayerState localPlayerState = null;
    private List<BulletData> remoteBullets;
    private PlayerState[] allPlayerStates;
    private float onHitForce;

    /**
     * Initialize RemoteManager that controls all data and functionality regarding remote players.
     */
    public RemoteManager() {
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("spriteatlas/SoldierSprites.atlas"));

        AmericanDream.client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameStateMessage) {
                    GameStateMessage gameStateMessage = (GameStateMessage) object;
                    allPlayerStates = gameStateMessage.playerStates;
                    // handle game state message
                    remotePlayers = new RemotePlayer[gameStateMessage.playerStates.length];

                    // overwrite the remote bullets list with new data
                    remoteBullets = gameStateMessage.bulletData;

                    for (int i = 0; i < gameStateMessage.playerStates.length; i++) {
                        PlayerState ps = gameStateMessage.playerStates[i];
                        if (ps.id != AmericanDream.id) {
                            // not current client
                            remotePlayerState = ps;
                            remotePlayers[i] = new RemotePlayer(ps.x, ps.y, ps.name, textureAtlas, ps.velX, ps.velY, ps.isShooting);
                        } else {
                            // current client
                            localPlayerState = ps;
                            // get the force of the hit
                            if (ps.applyForce != 0) {
                                onHitForce = ps.applyForce;
                            }
                        }
                    }
                    // Game duration in seconds, changes occur in server
                    gameTime = (gameStateMessage.gameTime);
                }
            }
        });
    }

    /**
     * Render remote player(s). Could theoretically handle more than one remote player.
     * @param batch spritebatch where to render the players
     * @param playerDimensions player object dimensions
     * @param delta delta time
     */
    public void renderPlayers(SpriteBatch batch, Vector2 playerDimensions, float delta) {
        if (remotePlayers != null) {
            for (RemotePlayer rp : remotePlayers) {
                if (rp != null) {
                    rp.update(delta);
                    rp.render(batch, playerDimensions);
                }
            }
        }
    }

    /**
     * Render all bullets shot by remote players.
     * @param batch spritebatch
     */
    public void renderBullets(SpriteBatch batch) {
        if (remoteBullets != null) {
            for (BulletData bullet : remoteBullets) {
                if (bullet.isDisabled) continue;

                RemoteBullet.render(batch, bullet.x, bullet.y);
            }
        }
    }

    /**
     * Get server-sided game time in seconds. Used for updating HUD timer.
     */
    public Optional<Integer> getGameTime() {
        if (gameTime != null) {
            return Optional.of(gameTime);
        }
        return Optional.empty();
    }

    /**
     * Get every player's state if none of them is null.
     */
    public Optional<PlayerState[]> getAllPlayerStates() {
        // does not contain null -> contains info about both players
        if (allPlayerStates != null
                && allPlayerStates.length == Arrays.stream(allPlayerStates).filter(x -> x != null).toArray().length) {
            return Optional.of(allPlayerStates);
        }
        return Optional.empty();
    }

    public Optional<PlayerState> getLocalPlayerState() {
        if (localPlayerState != null) {
            return Optional.of(localPlayerState);
        }
        return Optional.empty();
    }

    /**
     * Currently does not support more than 1 remote players.
     */
    public Optional<PlayerState> getRemotePlayerState() {
        if (remotePlayerState != null) {
            return Optional.of(remotePlayerState);
        }
        return Optional.empty();
    }

    /**
     * Apply bullet hit knockback (horizontal gravity) to the player if the player has been hit.
     * Float representing the force is received form the server only once, after that it's saved into the player object.
     * Exponentially decrement the applied force every game tick.
     * Stop applying knockback when the force is too small.
     * @param world world where the player moves (used for applying gravity)
     */
    public void testForHit(World world) {
        if (onHitForce != 0) {
            world.setGravity(new Vector2(onHitForce, GRAVITY));

            // make on hit force smaller
            onHitForce *= 0.9f;
        }
        // reset gravity if hit force is small enough
        if (Math.abs(onHitForce) < Math.abs(onHitForce / 10f)) {
            world.setGravity(new Vector2(0, GRAVITY));
            onHitForce = 0;
        }
    }
}
