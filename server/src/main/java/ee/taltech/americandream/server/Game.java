package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import helper.PlayerState;
import helper.packet.GameStateMessage;

import static helper.Constants.TICK_RATE;

public class Game extends Thread {
    private boolean running = true;
    private Player[] players;
    public Game(Connection[] connections) {
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
                gameStateMessage.playerStates = new PlayerState[players.length];
                for (int i = 0; i < players.length; i++) {
                    gameStateMessage.playerStates[i] = players[i].getState();
                    // log game state message
                    PlayerState ps = gameStateMessage.playerStates[i];
                    System.out.print(ps.id + " at (" + ps.x + ", " + ps.y + "), ");
                }
                System.out.println();
                // send game state message to all players
                for (Player player : players) {
                    player.sendGameState(gameStateMessage);
                }
                // message is sent every game tick
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
