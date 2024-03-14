package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final int lobbySize;
    private final String name;
    private static int id = 1;
    private final int lobbyId;
    private Game game;
    private final List<Connection> connections;

    public Lobby(String name, int lobbySize) {
        this.name = name;
        this.lobbySize = lobbySize;
        this.lobbyId = id;
        this.connections = new ArrayList<>();

        // increment id
        id++;
    }

    public void removeDisconnected() {
        connections.removeIf(connection -> !connection.isConnected());
    }

    public void addConnection(Connection connection) {
        removeDisconnected();

        // check if lobby is full
        if (connections.size() >= lobbySize) return;

        // check if connection is already in the lobby
        if (connections.contains(connection)) return;

        // add connection to the lobby
        connections.add(connection);
    }

    public void startGame() {
        // check if lobby is full
        if (connections.size() < lobbySize) return;

        // start game if game is null
        if (game == null) {
            // create a array of connections
            Connection[] connectionArray = new Connection[connections.size()];
            connectionArray = connections.toArray(connectionArray);

            // create a new game
            game = new Game(connectionArray);
            game.start();
        }
    }

    public int getId() {
        return lobbyId;
    }

    public String getStatus() {
        return name + " " + connections.size() + "/" + lobbySize;
    }
}
