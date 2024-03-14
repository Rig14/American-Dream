package ee.taltech.americandream.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import helper.BulletData;
import helper.Direction;
import helper.PlayerState;
import helper.packet.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static helper.Constants.*;

public class GameServer extends Thread {
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
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                // handle join lobby message
                if (object instanceof JoinLobbyMessage) {
                    JoinLobbyMessage joinLobbyMessage = (JoinLobbyMessage) object;
                    // get lobby id
                    int lobbyId = joinLobbyMessage.lobbyId;

                    // find lobby by id and join
                    lobbies.forEach(l -> {
                        if (l.getId() == lobbyId) {
                            l.addConnection(connection);
                        }
                    });
                }
            }
        });
    }

    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        gameServer.start();
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
        kryo.register(LobbyDataMessage.class);
        kryo.register(HashMap.class);
        kryo.register(JoinLobbyMessage.class);
        kryo.register(GameLeaveMessage.class);
    }


    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                // send lobby data message to all clients
                LobbyDataMessage lobbyDataMessage = new LobbyDataMessage();
                lobbyDataMessage.lobbies = new HashMap<>();
                lobbies.forEach((l) -> {
                    lobbyDataMessage.lobbies.put(l.getId(), l.getStatus());
                    l.removeDisconnected();

                    if (l.canStartGame()) {
                        // start a game if lobby is full
                        l.startGame();
                    }
                });
                server.sendToAllTCP(lobbyDataMessage);

                Thread.sleep(LOBBY_UPDATE_RATE_IN_SECONDS * 1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
