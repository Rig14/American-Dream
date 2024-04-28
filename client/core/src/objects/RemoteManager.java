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
import helper.Textures;
import helper.packet.GameStateMessage;
import objects.bullet.RemoteBullet;
import objects.player.Player;
import objects.player.RemotePlayer;

import java.util.*;

import static helper.Constants.AI_PLAYER_SIZE;

public class RemoteManager {
    private List<RemotePlayer> remotePlayers = new ArrayList<>();
    private Integer gameTime = null;
    private List<BulletData> remoteBullets;
    private PlayerState[] allPlayerStates;
    private PlayerState localPlayerState;
    private PlayerState AIPlayerState;
    private float AIplayerX;
    private float AIplayerY;

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
                    remotePlayers = new ArrayList<>();

                    // AI player
                    AIplayerX = gameStateMessage.AIplayerX;
                    AIplayerY = gameStateMessage.AIplayerY;

                    // overwrite the remote bullets list with new data
                    remoteBullets = gameStateMessage.bulletData;

                    for (int i = 0; i < gameStateMessage.playerStates.length; i++) {
                        PlayerState ps = gameStateMessage.playerStates[i];
                        if (ps.id != AmericanDream.id) {
                            // not current client
                            remotePlayers.add(new RemotePlayer(ps, textureAtlas));
                        } else {
                            if (ps.thisIsAI) {
                                AIPlayerState = ps;
                            } else {
                                localPlayerState = ps;
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
     * Get server-sided game time in seconds. Used for updating HUD timer.
     */
    public Optional<Integer> getGameTime() {
        if (gameTime != null) {
            return Optional.of(gameTime);
        }
        return Optional.empty();
    }

    /**
     * Get playerState for updating the local player.
     * In the future can be used for moving lives logic to server-side.
     */
    public Optional<PlayerState> getLocalPlayerState() {
        if (localPlayerState != null) {
            return Optional.of(localPlayerState);
        }
        return Optional.empty();
    }

    /**
     * Get playerState for updating the AI.
     */
    public Optional<PlayerState> getAIPlayerState() {
        if (AIPlayerState != null) {
            return Optional.of(AIPlayerState);
        }
        return Optional.empty();
    }

    /**
     * Return remote players list if it contains at least 1 remote player.
     */
    public List<RemotePlayer> getRemotePlayers() {
        return new ArrayList<>(remotePlayers);
    }

    /**
     * Get all players' state if none of them is null. Check for AI player.
     */
    public Optional<PlayerState[]> getAllPlayerStates() {
        // does not contain null -> contains info about both players
        if (allPlayerStates != null
                && allPlayerStates.length == Arrays.stream(allPlayerStates).filter(x -> x != null).toArray().length) {
            if (AIplayerX == 0 && AIplayerY == 0) {
                return Optional.of(allPlayerStates);
            }
            // add AI player to the list if it exists
            PlayerState[] newAllPlayerStates = Arrays.copyOf(allPlayerStates, allPlayerStates.length + 1);
            PlayerState AIplayer = new PlayerState();
            AIplayer.x = AIplayerX;
            AIplayer.y = AIplayerY;
            AIplayer.name = "AI";
            newAllPlayerStates[allPlayerStates.length] = AIplayer;
            return Optional.of(newAllPlayerStates);
        }
        return Optional.empty();
    }

    /**
     * Render remote player(s). Could theoretically handle more than one remote player.
     *
     * @param batch            spritebatch where to render the players
     * @param playerDimensions player object dimensions
     * @param delta            delta time
     */
    public void renderPlayers(SpriteBatch batch, Vector2 playerDimensions, float delta) {
        if (!remotePlayers.isEmpty()) {
            for (RemotePlayer rp : new ArrayList<>(remotePlayers)) {
                if (!Objects.equals(rp.getLivesCount(), 0)) {  // ignores null
                    rp.update(delta);
                    rp.render(batch, playerDimensions, remotePlayers.indexOf(rp));
                }
            }
        }
    }

    /**
     * Render AI player if it exists.
     */
    public void renderAIPlayer(SpriteBatch batch) {
        if (AIplayerX == 0 && AIplayerY == 0) return;

        batch.draw(Textures.ALIEN_TEXTURE, AIplayerX, AIplayerY, AI_PLAYER_SIZE.width, AI_PLAYER_SIZE.height);
    }

    /**
     * Render all bullets shot by remote players.
     *
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
}
