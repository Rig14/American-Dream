package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import helper.BulletData;
import helper.Direction;
import helper.packet.BulletPositionMessage;

public class Bullet {
    private final int id;
    private float x;
    private float y;
    private Direction direction;

    private final Game game;
    private final Connection connection;

    public Bullet(Connection connection, Game game, int id) {
        this.id = id;
        this.game = game;
        this.connection = connection;
        // send id to client
        connection.sendTCP("id:" + id);
        // add listeners
        connection.addListener(new Listener() {

        public void received(Connection connection, Object object) {
            super.received(connection, object);
            // handle incoming data
            if (object instanceof BulletPositionMessage positionMessage) {
                // handle position message
                handlePositionMessage(positionMessage);
            }
        }
    });
}
    private void handlePositionMessage(BulletPositionMessage positionMessage) {
        // handle position message
        x = positionMessage.x;
        y = positionMessage.y;
    }

    public BulletData getData() {
        // get player state
        BulletData data = new BulletData();
        data.id = id;
        data.x = x;
        data.y = y;
        return data;
    }


}
