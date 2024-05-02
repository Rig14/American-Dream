package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import helper.Audio;
import helper.BulletData;
import helper.PlayerState;
import helper.Textures;
import helper.packet.GameStateMessage;
import objects.bullet.RemoteBullet;
import objects.player.RemotePlayer;

import java.util.*;

import static helper.Constants.UFO_SIZE;

public class RemoteManager {
    private List<RemotePlayer> remotePlayers = new ArrayList<>();
    private Integer gameTime = null;
    private List<BulletData> remoteBullets;
    private PlayerState[] allPlayerStates;
    private PlayerState localPlayerState;
    private PlayerState AIPlayerState;
    private float ufoPlayerX;
    private float ufoPlayerY;

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

                    // UFO
                    ufoPlayerX = gameStateMessage.ufoPlayerX;
                    ufoPlayerY = gameStateMessage.ufoPlayerY;

                    // check if incoming bullets list is bigger than the current one
                    // when it is, play gun sound effect
                    if (remoteBullets != null && gameStateMessage.bulletData != null
                            && gameStateMessage.bulletData.size() > remoteBullets.size()) {
                        Audio.getInstance().playSound(Audio.SoundType.GUNSHOT);
                    }

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
                                if (ps.applyForce != 0) {
                                    Audio.getInstance().playSound(Audio.SoundType.HIT);
                                }
                            }
                        }
                    }
                    // play begin sound effect when game starts
                    if (gameStateMessage.gameTime != 0 && gameTime == null) {
                        Audio.getInstance().playSound(Audio.SoundType.START);
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
     * Get server-sided game time in seconds. Used for updating HUD timer.
     */
    public Optional<List<BulletData>> getBulletData() {
        if (remoteBullets != null) {
            return Optional.of(remoteBullets);
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
     * Get all players' state if none of them is null. Check for ufoPlayer.
     */
    public Optional<PlayerState[]> getAllPlayerStates() {
        // does not contain null -> contains info about both players
        if (allPlayerStates != null
                && allPlayerStates.length == Arrays.stream(allPlayerStates).filter(x -> x != null).toArray().length) {
            if (ufoPlayerX == 0 && ufoPlayerY == 0) {
                return Optional.of(allPlayerStates);
            }
            // add ufoPlayer to the list if it exists
            PlayerState[] newAllPlayerStates = Arrays.copyOf(allPlayerStates, allPlayerStates.length + 1);
            PlayerState ufoPlayerState = new PlayerState();
            ufoPlayerState.x = ufoPlayerX;
            ufoPlayerState.y = ufoPlayerY;
            ufoPlayerState.name = "UFO";
            newAllPlayerStates[allPlayerStates.length] = ufoPlayerState;
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
     * Render UFO if it exists.
     */
    public void renderUFO(SpriteBatch batch) {
        if (ufoPlayerX == 0 && ufoPlayerY == 0) return;

        batch.draw(Textures.ALIEN_TEXTURE, ufoPlayerX, ufoPlayerY, UFO_SIZE.width, UFO_SIZE.height);
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
