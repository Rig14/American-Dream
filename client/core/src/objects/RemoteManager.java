package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import helper.BulletData;
import helper.PlayerState;
import helper.packet.BulletPositionMessage;
import helper.packet.GameStateMessage;
import objects.bullet.Bullet;
import objects.bullet.RemoteBullet;
import objects.player.RemotePlayer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RemoteManager {
    private RemotePlayer[] remotePlayers;
    private Integer gameTime = null;
    private Integer remoteLives = null;
    private List<BulletData> remoteBullets;

    public RemoteManager() {
        AmericanDream.client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameStateMessage) {
                    GameStateMessage gameStateMessage = (GameStateMessage) object;
                    // handle game state message
                    remotePlayers = new RemotePlayer[gameStateMessage.playerStates.length];

                    // overwrite the remote bullets list with new data
                    remoteBullets = gameStateMessage.bulletData;

                    for (PlayerState ps : gameStateMessage.playerStates) {
                        if (ps.id != AmericanDream.id) {
                            remoteLives = ps.livesCount;
                            remotePlayers[ps.id - 1] = new RemotePlayer(ps.x, ps.y);
                        }
                    }
                    // Game duration in seconds, changes occur in server
                    gameTime = (gameStateMessage.gameTime);
                }
            }
        });
    }

    public void renderPlayers(SpriteBatch batch, Vector2 playerDimensions) {
        if (remotePlayers != null) {
            for (RemotePlayer rp : remotePlayers) {
                if (rp != null) {
                    rp.render(batch, playerDimensions);
                }
            }
        }
    }

    public void renderBullets(SpriteBatch batch) {
        if (remoteBullets != null) {
            for (BulletData bullet : remoteBullets) {
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

    public Optional<Integer> getRemoteLives() {
        if (remoteLives != null) {
            return Optional.of(remoteLives);
        }
        return Optional.empty();
    }

    public void sendBullets(List<Bullet> bullets) {
        BulletPositionMessage bulletPositionMessage = new BulletPositionMessage();
        bulletPositionMessage.playerBullets = bullets.stream().map(Bullet::toBulletData).collect(Collectors.toList());
        AmericanDream.client.sendUDP(bulletPositionMessage);
    }
}