package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import helper.Direction;
import helper.PlayerState;
import helper.packet.GameStateMessage;
import helper.packet.PlayerPositionMessage;
import helper.packet.TimeMessage;

public class Player {
    private final int id;
    private float x;
    private float y;
    private Direction direction;

    private final Game game;
    private final Connection connection;
    public Player(Connection connection, Game game, int id) {
        // create player
        this.id = id;
        this.game = game;
        this.connection = connection;
        // send id to client
        connection.sendTCP("id:" + id);
        // add listeners
        connection.addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                onDisconnect();
            }
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                // handle incoming data
                if (object instanceof PlayerPositionMessage positionMessage) {
                    // handle position message
                    handlePositionMessage(positionMessage);
                }
            }
        });
    }

    private void onDisconnect() {
        // handle disconnect
        // end game
        game.end();
    }

    private void handlePositionMessage(PlayerPositionMessage positionMessage) {
        // handle position message
        x = positionMessage.x;
        y = positionMessage.y;
        direction = positionMessage.direction;
    }

    public PlayerState getState() {
        // get player state
        PlayerState state = new PlayerState();
        state.id = id;
        state.x = x;
        state.y = y;
        state.direction = direction;
        return state;
    }

    public void sendGameState(GameStateMessage gameStateMessage) {
        // send game state message
        connection.sendUDP(gameStateMessage);
    }

    public void sendTimeMessage(TimeMessage time) {
        connection.sendTCP(time);
    }
}
