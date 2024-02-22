package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import helper.PlayerState;
import helper.packet.GameStateMessage;

import static helper.Constants.GAME_DURATION;
import static helper.Constants.TICK_RATE;

public class Game extends Thread {

    private float gameTime;
    private boolean running = true;
    private Player[] players;
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
                for (int i = 0; i < players.length; i++) {
                    gameStateMessage.playerStates[i] = players[i].getState();
                    // log game state message
                    PlayerState ps = gameStateMessage.playerStates[i];
                }
                // send game state message to all players
                for (Player player : players) {
                    player.sendGameState(gameStateMessage);
                }
                // decrement game time
                gameTime -= 1f / TICK_RATE;
                // end game
                if (gameTime <= 0) {
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
