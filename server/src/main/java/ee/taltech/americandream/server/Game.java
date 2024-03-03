package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import helper.PlayerState;
import helper.packet.GameStateMessage;

import static helper.Constants.GAME_DURATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static helper.Constants.TICK_RATE;

public class Game extends Thread {

    private float gameTime;
    private boolean running = true;
    private Player[] players;
    private boolean bothJoinedMultiplayer = false;

    public Game(Connection[] connections) {
        // set game duration
        this.gameTime = GAME_DURATION;

        players = new Player[connections.length];
        // start game with connections
        // make players from connections
        for (int i = 0; i < connections.length; i++) {
            players[i] = new Player(connections[i], this, i+1);
        }
    }

    public void run() {
        while (running) {
            try {
                // construct game state message
                GameStateMessage gameStateMessage = new GameStateMessage();

                gameStateMessage.gameTime = Math.round(gameTime);
                gameStateMessage.playerStates = new PlayerState[players.length];
                gameStateMessage.bulletData = new ArrayList<>();
                for (int i = 0; i < players.length; i++) {
                    // add player states to the game state message (like position)
                    gameStateMessage.playerStates[i] = players[i].getState();
                    // add bullets to the game state message
                    gameStateMessage.bulletData.addAll(players[i].getPlayerBullets());
                }

                // send game state message to all players
                for (Player player : players) {
                    player.sendGameState(gameStateMessage);
                }

                // Start decrementing time when both players have joined the level
                // Fixes countdown starting too early while in title screen
                if (bothJoinedMultiplayer) {
                    gameTime -= 1f / TICK_RATE;
                } else if (!Arrays.stream(gameStateMessage.playerStates).map(x -> x.direction).toList().contains(null)) {
                    bothJoinedMultiplayer = true;  // true when both players start sending non-null position data
                }

                // end game when      time ends  ||  one player has 0 lives
                if (gameTime <= 0
                        || Arrays.stream(gameStateMessage.playerStates).map(x -> x.livesCount).toList().contains(0)) {
                    this.end();
                }

                Thread.sleep(1000 / TICK_RATE);

            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void end() {
        running = false;
    }
}
