package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final int lobbySize;
    private final String name;
    private static int id = 1;
    private final int lobbyId;
    private final List<Connection> connections;

    public Lobby(String name, int lobbySize) {
        this.name = name;
        this.lobbySize = lobbySize;
        this.lobbyId = id;
        this.connections = new ArrayList<>();

        // increment id
        id++;
    }

    private void removeDisconnected() {
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

        // start game
        Game game = new Game(connections.toArray(new Connection[0]));
        game.start();
    }

    public int getId() {
        return lobbyId;
    }

    public String getStatus() {
        return name + " " + connections.size() + "/" + lobbySize;
    }
}
