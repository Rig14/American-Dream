package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import helper.BulletData;
import helper.PlayerState;
import helper.packet.GameStateMessage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static helper.Constants.*;

public class Game extends Thread {

    private final Lobby lobby;
    private Player[] alivePlayers;
    private final Player[] allPlayers;
    private AIPlayer aiPlayer;
    private float gameTime;
    private boolean running = true;
    private boolean allJoinedMultiplayer = false;

    /**
     * Create a new game instance containing specific clients.
     * @param connections list of connections to each client
     * @param lobby lobby which manages the current game instance
     */
    public Game(Connection[] connections, Lobby lobby) {
        // set game duration
        this.gameTime = GAME_DURATION;
        this.lobby = lobby;

        alivePlayers = new Player[connections.length];
        // start game with connections
        // make players from connections
        for (int i = 0; i < connections.length; i++) {
            alivePlayers[i] = new Player(connections[i], this, connections[i].getID());
        }
        allPlayers = alivePlayers.clone();
    }

    /**
     * Update all players and bullets of the game instance. Decrement game time if both players have joined the map.
     * End the game if a player has 0 lives left or the game time runs out.
     * Sends: GameStateMessage - contains all data about players, player lives, damage, bullets and game time.
     */
    public void run() {
        while (running) {
            try {
                checkForDeadPlayers();
                // update players
                for (Player player : alivePlayers) {
                    player.update(1000f / TICK_RATE / 1000f);
                }

                // update AI player
                if (aiPlayer != null) {
                    aiPlayer.update(1000f / TICK_RATE / 1000f, alivePlayers);
                }

                // construct game state message
                GameStateMessage gameStateMessage = new GameStateMessage();

                gameStateMessage.gameTime = Math.round(gameTime);
                gameStateMessage.playerStates = new PlayerState[allPlayers.length];
                gameStateMessage.bulletData = new ArrayList<>();
                for (int i = 0; i < allPlayers.length; i++) {
                    // add player states to the game state message (like position)
                    gameStateMessage.playerStates[i] = allPlayers[i].getState();
                    // add bullets to the game state message
                    gameStateMessage.bulletData.addAll(allPlayers[i].getPlayerBullets());
                }

                // ai logic
                if (aiPlayer != null) {
                    // add AI player bullet data
                    gameStateMessage.bulletData.addAll(aiPlayer.getBullets());
                    // add AI player position
                    gameStateMessage.AIplayerX = aiPlayer.getX();
                    gameStateMessage.AIplayerY = aiPlayer.getY();
                }

                // handle bullets hitting players
                checkForBulletHits(gameStateMessage);

                // send game state message to all players, including dead players
                for (Player player : allPlayers) {
                    player.sendGameState(gameStateMessage);
                }

                // Start decrementing time when both players have joined the level
                // Fixes countdown starting too early while in title screen
                if (allJoinedMultiplayer) {
                    gameTime -= 1f / TICK_RATE;
                } else if (!Arrays.stream(gameStateMessage.playerStates).map(x -> x.direction).toList().contains(null)) {
                    allJoinedMultiplayer = true;  // true when all players start sending non-null position data
                }

                // end game when      time ends  ||  only one player has more than 0 lives
                if (gameTime <= 0 || alivePlayers.length < 2) {
                    Arrays.stream(allPlayers).forEach(x -> x.sendGameStateTCP(gameStateMessage)); // last message
                    lobby.clearLobby();
                    this.end();
                }

                Thread.sleep(1000 / TICK_RATE);

            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    /**
     * Remove players with 0 lives from alive players array. This prevents them from interfering with the
     * ongoing game.
     * allPlayers array lets them still spectate the game.
     */
    private void checkForDeadPlayers() {
        if (Arrays.stream(alivePlayers).anyMatch(x -> Objects.equals(x.getState().livesCount, 0))) {
            alivePlayers = Arrays.stream(alivePlayers)
                    .filter(x -> x.getState().livesCount != 0)  // ignores null during initialization
                    .toArray(Player[]::new);
        }
    }

    /**
     * Generate a hitbox for each player (including AI player). Iterate through all bullets and check if any of them
     * intersects the player's hitbox.
     * Handle bullet hits by disabling the bullet, calculating bullet force and applying force to the player.
     * @param gameStateMessage contains data about players' and bullets' locations
     */
    private void checkForBulletHits(GameStateMessage gameStateMessage) {
        List<BulletData> bullets = gameStateMessage.bulletData;
        PlayerState[] playerStates = gameStateMessage.playerStates;

        // construct rectangles for players
        Rectangle[] playerHitboxes = new Rectangle[playerStates.length];
        for (int i = 0; i < playerStates.length; i++) {
            playerHitboxes[i] = new Rectangle((int) playerStates[i].x - PLAYER_WIDTH / 2, (int) playerStates[i].y - PLAYER_HEIGHT / 2, PLAYER_WIDTH, PLAYER_HEIGHT);
        }

        // if AI player exists, construct hitbox for AI player
        Rectangle aiPlayerHitbox = null;
        if (aiPlayer != null) {
            aiPlayerHitbox = new Rectangle((int) aiPlayer.getX(), (int) aiPlayer.getY(), AI_PLAYER_SIZE.width, AI_PLAYER_SIZE.height);
        }

        // check if bullets hit players
        for (BulletData bullet : bullets) {
            // construct bullet hitbox
            Rectangle bulletHitbox = new Rectangle((int) bullet.x - BULLET_HITBOX / 2, (int) bullet.y - BULLET_HITBOX / 2, BULLET_HITBOX, BULLET_HITBOX);
            // check if bullet hit any player
            for (int i = 0; i < playerHitboxes.length; i++) {
                if (playerHitboxes[i].intersects(bulletHitbox)  // hitboxes hit
                        && !bullet.isDisabled  // has already hit
                        && bullet.id != playerStates[i].id  // is not the player who shot the bullet
                        && !Objects.equals(playerStates[i].livesCount, 0)  // player is not dead
                ) {
                    // remove bullet
                    bullet.isDisabled = true;
                    // find player with corresponding id

                    for (Player player : alivePlayers) {
                        if (player.getId() == playerStates[i].id) {
                            // register being hit, increment damage and calculate force
                            // apply force to player (state)
                            playerStates[i].applyForce = player.handleBeingHit(bullet);  // returns force
                        }
                    }
                }
            }

            // AI player hit
            if (aiPlayerHitbox != null
                    && aiPlayerHitbox.intersects(bulletHitbox)
                    && !bullet.isDisabled && bullet.id != -1
            ) {
                bullet.isDisabled = true;
                aiPlayer.bulletHit(bullet);
            }
        }
    }

    /**
     * Close current instance of the game and clear the lobby.
     */
    public void end() {
        running = false;
        lobby.clearLobby();
    }

    /**
     * Add an AI player to the current game instance.
     */
    public void addAIPlayer() {
        // check if AI player already exists
        if (aiPlayer != null) return;

        // find point between players and spawn the AI player there
        float x = Arrays.stream(alivePlayers).reduce(0f, (acc, player) -> acc + player.getState().x, Float::sum) / alivePlayers.length;
        float y = Arrays.stream(alivePlayers).reduce(0f, (acc, player) -> acc + player.getState().y, Float::sum) / alivePlayers.length;

        aiPlayer = new AIPlayer(x, y);
    }
}
