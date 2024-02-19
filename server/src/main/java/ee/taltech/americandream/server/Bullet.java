package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import helper.BulletData;
import helper.packet.BulletPositionMessage;
import helper.packet.GameStateMessage;


public class Bullet {
    private int id = 0;
    private float x, y;
    private float speedBullet;
    public final Game game;
    public final Connection connection;

    public Bullet(Connection connection, Game game) {
        this.game = game;
        this.connection = connection;
        this.id = id++;
        // add listeners
        connection.addListener(new Listener() {

            public void received(Connection connection, Object object) {
                super.received(connection, object);
                // handle incoming data
                if (object instanceof BulletPositionMessage positionMessage) {
                // handle position message
                    handlePositionMessage(positionMessage);
                    System.out.println("received bulletpos");
            }
        }
    });
}
    private void handlePositionMessage(BulletPositionMessage positionMessage) {
        if (positionMessage != null) {
            System.out.println("received bullet pos 0101010");

            x = positionMessage.x;
            y = positionMessage.y;
            speedBullet = positionMessage.speedBullet;
        }
    }

    public BulletData getData() {
        // get bullet state
        BulletData data = new BulletData();
        data.id = id;
        data.x = x;
        data.y = y;
        data.speedBullet = speedBullet;
        return data;
    }
    public void sendGameState(GameStateMessage gameStateMessage) {
        // send game state message
        connection.sendUDP(gameStateMessage);
        System.out.println("sent gamestate");
    }


}
