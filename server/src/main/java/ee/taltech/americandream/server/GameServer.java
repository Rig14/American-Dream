package ee.taltech.americandream.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import helper.BulletData;
import helper.Direction;
import helper.PlayerState;
import helper.packet.BulletMessage;
import helper.packet.GameStateMessage;
import helper.packet.PlayerPositionMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static helper.Constants.LOBBY_SIZE;
import static helper.Constants.PORTS;

public class GameServer {
    private final Server server;
    private final List<Lobby> lobbies;

    public GameServer() {
        // setup server and open ports
        this.server = new Server();

        // create lobbies
        this.lobbies = new ArrayList<>();
        // initialize default lobbies
        for (int i = 0; i < 3; i++) {
            Lobby lobby = new Lobby("Default lobby " + (i + 1), LOBBY_SIZE);
            lobbies.add(lobby);
        }

        // register used classes
        registerClasses();
        try {
            server.start();
            server.bind(PORTS[0], PORTS[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // add listener for new connections
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                lobbies.get(0).addConnection(connection);
                // try to start game
                lobbies.get(0).startGame();
            }
        });
    }

    public static void main(String[] args) {
        new GameServer();
    }

    private void registerClasses() {
        // register classes for serialization
        Kryo kryo = server.getKryo();
        kryo.register(GameStateMessage.class);
        kryo.register(PlayerPositionMessage.class);
        kryo.register(PlayerState[].class);
        kryo.register(PlayerState.class);
        kryo.register(Direction.class);
        kryo.register(BulletMessage.class);
        kryo.register(BulletData.class);
        kryo.register(ArrayList.class);
    }
}
