package ee.taltech.americandream.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import helper.BulletData;
import helper.Direction;
import helper.PlayerState;
import helper.packet.BulletPositionMessage;
import helper.packet.GameStateMessage;
import helper.packet.IDMessage;
import helper.packet.PlayerPositionMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static helper.Constants.LOBBY_SIZE;
import static helper.Constants.PORTS;

public class GameServer {
    private Server server;
    private Game game;
    private Connection[] connections = new Connection[LOBBY_SIZE];

    public GameServer() {
        // setup server and open ports
        this.server = new Server();

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
                // loop through connections and add new connection to first empty slot
                for (int i = 0; i < connections.length; i++) {
                    // if connection is empty or not connected, add new connection
                    if (connections[i] == null || !connections[i].isConnected()) {
                        connections[i] = connection;
                        // send id message
                        IDMessage idMessage = new IDMessage();
                        idMessage.id = i + 1;
                        connection.sendTCP(idMessage);
                        break;
                    }
                }
                if (Arrays.stream(connections).allMatch(c -> c != null && c.isConnected())) {
                    // if all connections are filled, start game with the connections
                    game = new Game(connections);
                    game.start();
                    // clear connections if all are connected and game is started
                }
            }
            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                // handle incoming data
                if (object instanceof BulletPositionMessage) {
                    // handle bullet position message
                    BulletPositionMessage positionMessage = (BulletPositionMessage) object;
                    if (game != null) {
                        Bullet bullet = new Bullet(connection, game);
                        // Update the bullet's position
                        bullet.setPosition(positionMessage.x, positionMessage.y);
                        bullet.addBullet(bullet, game.bullets);
                        bullet.broadcastBulletUpdate(positionMessage, connections);
                    } else {
                        System.err.println("Game is null, cannot create bullet.");
                    }

            }
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
        kryo.register(IDMessage.class);
        kryo.register(BulletPositionMessage.class);
        kryo.register(BulletData.class);
        kryo.register(ArrayList.class);

    }

}
