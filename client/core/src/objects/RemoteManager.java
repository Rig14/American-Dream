package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
    private float AIplayerX;
    private float AIplayerY;


    public RemoteManager() {
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("spriteatlas/SoldierSprites.atlas"));

        AmericanDream.client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameStateMessage) {
                    GameStateMessage gameStateMessage = (GameStateMessage) object;
                    allPlayerStates = gameStateMessage.playerStates;
                    // handle game state message
                    remotePlayers = new RemotePlayer[gameStateMessage.playerStates.length];

                    // AI player
                    if (AIplayerX != 0 && AIplayerY != 0) {
                        AIplayerX = gameStateMessage.AIplayerX;
                        AIplayerY = gameStateMessage.AIplayerY;
                    }

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

    public void renderAIPlayer(SpriteBatch batch) {
        // check if AI player exists
        if (AIplayerX == 0 && AIplayerY == 0) return;

        Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fillCircle((int) AIplayerX, (int) AIplayerY, 10);
        Texture texture = new Texture(pixmap);

        batch.draw(texture, AIplayerX, AIplayerY);
    }

    public void renderBullets(SpriteBatch batch) {
        if (remoteBullets != null) {
            for (BulletData bullet : remoteBullets) {
                if (bullet.isDisabled) continue;

                RemoteBullet.render(batch, bullet.x, bullet.y);
            }
        }
    }

    // mainly used to update hud time
    public Optional<Integer> getGameTime() {
        if (gameTime != null) {
            return Optional.of(gameTime);
        }
        return Optional.empty();
    }

    // used for off-screen indicator
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

    public Optional<PlayerState> getRemotePlayerState() {
        if (remotePlayerState != null) {
            return Optional.of(remotePlayerState);
        }
        return Optional.empty();
    }

    public void testForHit(World world) {
        // currently the force is applied like so:
        // make the horizontal gravity equal to the force
        // and then make the force smaller over time
        // until it is small enough to reset the gravity

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
