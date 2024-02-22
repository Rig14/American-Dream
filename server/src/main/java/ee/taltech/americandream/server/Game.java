package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import helper.PlayerState;
import helper.packet.GameStateMessage;
import helper.packet.TimeMessage;
import helper.Time;

import static helper.Constants.TICK_RATE;

public class Game extends Thread {
    private boolean running = true;
    private Player[] players;
    private Time time = new Time();
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
                // construct time message if the time has changed
                if (time.update((float) 1 / TICK_RATE)) {  // should be replaced with "server deltaTime"
                    TimeMessage timeMessage = new TimeMessage();
                    timeMessage.seconds = time.getRemainingTime();
                    // send game time message to all players;
                    for (Player player : players) {
                        player.sendTimeMessage(timeMessage);
                    }
                    System.out.println("here");
                }
                // construct game state message
                GameStateMessage gameStateMessage = new GameStateMessage();
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
