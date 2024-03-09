package objects;

import com.badlogic.gdx.graphics.Texture;
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

import static helper.Constants.OFFSCREEN_X;
import static helper.Constants.OFFSCREEN_X_NEG;
import static helper.Constants.OFFSCREEN_Y;
import static helper.Constants.OFFSCREEN_Y_NEG;
import static helper.Textures.BULLET_TEXTURE;

public class RemoteManager {
    private RemotePlayer[] remotePlayers;
    private Integer gameTime = null;
    private Integer remoteLives = null;
    private List<BulletData> remoteBullets;
    private PlayerState[] allPlayerStates;


    public RemoteManager() {
        AmericanDream.client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameStateMessage) {
                    GameStateMessage gameStateMessage = (GameStateMessage) object;
                    allPlayerStates = gameStateMessage.playerStates;
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

    // Render indicator when remote player is off-screen, can handle zoom and replacing with other similar size texture
    public void renderIndicator(SpriteBatch batch, float cameraX, float cameraY, Vector2 playerDimensions) {
        if (allPlayerStates != null) {
            for (PlayerState player : allPlayerStates) {
                if (player != null) {
                    // Check if remote player is off-screen and handle corners "FizzBuzz" style
                    float cameraDeltaX = player.getX() - cameraX;
                    float cameraDeltaY = player.getY() - cameraY;
                    Texture indicator = BULLET_TEXTURE;

                    // right
                    if (cameraDeltaX > OFFSCREEN_X) {
                        if (cameraDeltaY > OFFSCREEN_Y) { // up right
                            batch.draw(indicator, cameraX + OFFSCREEN_X - indicator.getWidth() * 1.25f,
                                    cameraY + OFFSCREEN_Y - indicator.getHeight() * 2.5f);
                        } else if (cameraDeltaY < OFFSCREEN_Y_NEG) { // down right
                            batch.draw(indicator, cameraX + OFFSCREEN_X - indicator.getWidth() * 1.25f,
                                    cameraY - OFFSCREEN_Y + indicator.getHeight() * 1.5f);
                        } else { // right
                            batch.draw(indicator, cameraX + OFFSCREEN_X - indicator.getWidth() * 1.25f,
                                    player.getY() - playerDimensions.y / 2 + indicator.getHeight() / 2f);
                        }

                    // left
                    } else if (cameraDeltaX < OFFSCREEN_X_NEG) {
                        if (cameraDeltaY > OFFSCREEN_Y) { // up left
                            batch.draw(indicator, cameraX - OFFSCREEN_X + indicator.getWidth() * 0.25f,
                                    cameraY + OFFSCREEN_Y - indicator.getHeight() * 2.5f);
                        } else if (cameraDeltaY < OFFSCREEN_Y_NEG) { // down left
                            batch.draw(indicator, cameraX - OFFSCREEN_X + indicator.getWidth() * 0.25f,
                                    cameraY - OFFSCREEN_Y + indicator.getHeight() * 1.5f);
                        } else { // left
                            batch.draw(indicator, cameraX - OFFSCREEN_X + indicator.getWidth() * 0.25f,
                                    player.getY() - playerDimensions.y / 2 + indicator.getHeight() / 2f);
                        }

                    // connecting playerDimensions and texture.getWidth() is likely impossible without magic numbers and weird logic
                    // up
                    } else if (cameraDeltaY > OFFSCREEN_Y) {
                        batch.draw(indicator, player.getX() - playerDimensions.x / 2 - 15, cameraY + OFFSCREEN_Y - indicator.getHeight() * 2.5f);

                    // down
                    } else if (cameraDeltaY < OFFSCREEN_Y_NEG) {
                        batch.draw(indicator, player.getX() - playerDimensions.x / 2 - 15, cameraY - OFFSCREEN_Y + indicator.getHeight() * 1.5f);
                    }
                }
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
