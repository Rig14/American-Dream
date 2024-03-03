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

import static helper.Constants.BULLET_DIMENSIONS;
import static helper.Constants.CAMERA_ZOOM;
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

    // Render indicator when remote player is off-screen
    public void renderIndicator(SpriteBatch batch, float cameraX, float cameraY, Vector2 playerDimensions) {
        if (remotePlayers != null) {
            for (RemotePlayer rp : remotePlayers) {
                if (rp != null && CAMERA_ZOOM == 1.5f) { // Changing zoom level will stop rendering the indicator
                    // Check if remote player is off-screen and handle corners "FizzBuzz" style
                    float cameraDeltaX = rp.getX() - cameraX;
                    float cameraDeltaY = rp.getY() - cameraY;

                    // right
                    if (cameraDeltaX > OFFSCREEN_X) {
                        if (cameraDeltaY > OFFSCREEN_Y) {
                            batch.draw(BULLET_TEXTURE, cameraX + 415, cameraY + 325); // up right
                        } else if (cameraDeltaY < OFFSCREEN_Y_NEG) {
                            batch.draw(BULLET_TEXTURE, cameraX + 415, cameraY - 350); // down right
                        } else {
                            batch.draw(BULLET_TEXTURE, cameraX + 415,
                                    rp.getY() - playerDimensions.y / 2 + BULLET_DIMENSIONS.y / 2);
                        }

                    // left
                    } else if (cameraDeltaX < OFFSCREEN_X_NEG) {
                        if (cameraDeltaY > OFFSCREEN_Y) {
                            batch.draw(BULLET_TEXTURE, cameraX - 475, cameraY + 325); // up left
                        } else if (cameraDeltaY < OFFSCREEN_Y_NEG) {
                            batch.draw(BULLET_TEXTURE, cameraX - 475, cameraY - 350); // down left
                        } else {
                            batch.draw(BULLET_TEXTURE, cameraX - 475,
                                    rp.getY() - playerDimensions.y / 2 + BULLET_DIMENSIONS.y / 2);
                        }

                    // up
                    } else if (cameraDeltaY > OFFSCREEN_Y) {  // subtracting magic number 15 just works
                        batch.draw(BULLET_TEXTURE, rp.getX() - playerDimensions.x / 2 - 15, cameraY + 325);

                    // down
                    } else if (cameraDeltaY < OFFSCREEN_Y_NEG) {  // subtracting magic number 15 just works
                        batch.draw(BULLET_TEXTURE, rp.getX() - playerDimensions.x / 2 - 15, cameraY - 350);
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
