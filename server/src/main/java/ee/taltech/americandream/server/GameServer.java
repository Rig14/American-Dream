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
import java.util.HashMap;
import java.util.List;

import static helper.Constants.LOBBY_UPDATE_RATE_IN_SECONDS;
import static helper.Constants.PORTS;

public class GameServer extends Thread {
    private final Server server;
    private final List<Lobby> lobbies;

    /**
     * The GameServer contains lobbies and runs threaded game instances.
     * Game instance creation is managed by the Lobby class.
     * Receives: LobbyJoinMessage - new client connection and the desired lobby that the player (connection) will be added to.
     * Sends: LobbyDataMessage - lobby names and the amount of players in each lobby.
     */
    public GameServer() {
        // setup server and open ports
        this.server = new Server();

        // create lobbies
        this.lobbies = new ArrayList<>();
        // initialize default lobbies
        lobbies.add(new Lobby("Default lobby " + (1), 2));
        lobbies.add(new Lobby("Default lobby " + (2), 3));
        lobbies.add(new Lobby("Default lobby " + (3), 4));

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

    /**
     * This method registers classes for serialization.
     * Classes that are sent over the network need to be registered.
     */
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
        kryo.register(AddAIMessage.class);
        kryo.register(MapSelectionMessage.class);
        kryo.register(GunBoxMessage.class);
        kryo.register(GunPickupMessage.class);
    }

    /**
     * Run GameServer and update lobbies.
     */
    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                // send lobby data message to all clients
                LobbyDataMessage lobbyDataMessage = new LobbyDataMessage();
                lobbyDataMessage.lobbies = new HashMap<>();
                lobbyDataMessage.maps = new HashMap<>();
                lobbyDataMessage.maxPlayers = new HashMap<>();
                lobbyDataMessage.playerCount = new HashMap<>();
                lobbies.forEach((l) -> {
                    lobbyDataMessage.lobbies.put(l.getId(), l.getName());
                    lobbyDataMessage.maxPlayers.put(l.getId(), l.getMaxPlayerCount());
                    lobbyDataMessage.playerCount.put(l.getId(), l.getPlayerCount());
                    lobbyDataMessage.maps.put(l.getId(), l.getCurrentMap());
                    l.removeDisconnected();
                    if (l.getPlayerCount() < 1) {
                        l.setCurrentMap(null);
                    }
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
