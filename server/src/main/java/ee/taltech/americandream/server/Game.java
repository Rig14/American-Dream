package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import helper.BulletData;
import helper.PlayerState;
import helper.packet.GameStateMessage;
import helper.packet.GunBoxMessage;
import helper.packet.GunPickupMessage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static helper.Constants.*;

public class Game extends Thread {

    private boolean AIGame;
    private final Lobby lobby;
    private Player[] alivePlayers;
    private final Player[] allPlayers;
    private ee.taltech.americandream.server.UFO UFO;
    private float gameTime;
    private boolean running = true;
    private boolean allJoinedMultiplayer = false;
    private long lastGunBoxSpawnTime = 0;
    private int gunBoxId = 0;

    /**
     * Create a new game instance containing specific clients.
     * @param connections list of connections to each client
     * @param lobby lobby which manages the current game instance
     */
    public Game(Connection[] connections, Lobby lobby) {
        AIGame = lobby.getName().equals("AILobby");
        // set game duration
        this.gameTime = GAME_DURATION;
        this.lobby = lobby;

        alivePlayers = new Player[connections.length];
        // start game with connections
        // make players from connections
        for (int i = 0; i < connections.length; i++) {
            alivePlayers[i] = new Player(connections[i], this, connections[i].getID(), false);
        }
        if (AIGame) alivePlayers[1] = new Player(connections[1], this, connections[1].getID(), true);

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

                // update UFO
                if (UFO != null) {
                    UFO.update(1000f / TICK_RATE / 1000f, alivePlayers);
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

                // UFO logic
                if (UFO != null) {
                    // add UFO bullet data
                    gameStateMessage.bulletData.addAll(UFO.getBullets());
                    // add UFO position
                    gameStateMessage.ufoPlayerX = UFO.getX();
                    gameStateMessage.ufoPlayerY = UFO.getY();
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
                // spawning gunbox for weapons
                if (System.currentTimeMillis() - lastGunBoxSpawnTime > GUNBOX_SPAWN_DELAY) {
                    // calculate the absolute sum of x-coordinates of all players
                    lastGunBoxSpawnTime = System.currentTimeMillis();
                    float sumX = 0;
                    for (Player player : allPlayers) {
                        sumX += Math.abs(player.getState().x);
                    }
                    // calculate the average x-coordinate
                    float averageX = sumX / allPlayers.length;
                    GunBoxMessage gunBoxMessage = new GunBoxMessage();
                    gunBoxMessage.x = averageX;
                    gunBoxMessage.y = 1500;
                    gunBoxMessage.id = gunBoxId++;
                    Arrays.stream(allPlayers)
                            .filter(player -> player.getName() != null && !player.getName().contains("AI"))
                            .forEach(player -> player.sendGunBoxTCP(gunBoxMessage));
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
     * allPlayers array still lets them spectate the game.
     */
    private void checkForDeadPlayers() {
        if (Arrays.stream(alivePlayers).anyMatch(x -> Objects.equals(x.getState().livesCount, 0))) {
            alivePlayers = Arrays.stream(alivePlayers)
                    .filter(x -> x.getState().livesCount != 0)  // ignores null during initialization
                    .toArray(Player[]::new);
        }
    }

    /**
     * Generate a hitbox for each player (including UFO). Iterate through all bullets and check if any of them
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

        // if UFO exists, construct hitbox for UFO
        Rectangle ufoHitbox = null;
        if (UFO != null) {
            ufoHitbox = new Rectangle((int) UFO.getX(), (int) UFO.getY(), UFO_SIZE.width, UFO_SIZE.height);
        }

        // check if bullets hit players
        for (BulletData bullet : bullets) {
            // construct bullet hitbox
            Rectangle bulletHitbox = new Rectangle((int) bullet.x - BULLET_HITBOX / 2, (int) bullet.y - BULLET_HITBOX / 2, BULLET_HITBOX, BULLET_HITBOX);
            // check if bullet hit any player
            for (int i = 0; i < playerHitboxes.length; i++) {
                if (playerHitboxes[i].intersects(bulletHitbox)  // hitboxes hit
                        && !bullet.isDisabled  // has already hit
                        && !bullet.name.equals(playerStates[i].name)  // is not the player who shot the bullet
                        && !Objects.equals(playerStates[i].livesCount, 0)  // player is not dead
                ) {
                    // remove bullet
                    bullet.isDisabled = true;
                    // find player with corresponding id

                    for (Player player : alivePlayers) {
                        if (player.getName().equals(playerStates[i].name)) {
                            // register being hit, increment damage and calculate force
                            // apply force to player (state)
                            playerStates[i].applyForce = player.handleBeingHit(bullet, bullet.name, bullet.shotWithGun);  // returns force
                        }
                    }
                }
            }

            // UFO collision with bullets
            if (ufoHitbox != null
                    && ufoHitbox.intersects(bulletHitbox)
                    && !bullet.isDisabled && bullet.id != -1
            ) {
                bullet.isDisabled = true;
                UFO.bulletHit(bullet);
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
     * Add an UFO to the current game instance.
     */
    public void addUFO() {
        // check if UFO already exists
        if (UFO != null) return;

        // find point between players and spawn the UFO there
        float x = Arrays.stream(alivePlayers).reduce(0f, (acc, player) -> acc + player.getState().x, Float::sum) / alivePlayers.length;
        float y = Arrays.stream(alivePlayers).reduce(0f, (acc, player) -> acc + player.getState().y, Float::sum) / alivePlayers.length;

        UFO = new UFO(x, y);
    }
    public void sendToAllExcept(Player player, GunPickupMessage gunPickupMessage) {
        for (Player player1 : allPlayers) {
            if (!player1.getName().equals(player.getName())) {
                player1.sendGunPickupMessage(gunPickupMessage);
            }
        }
    }
}
