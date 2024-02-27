package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import helper.BulletData;
import helper.packet.BulletPositionMessage;
import helper.packet.GameStateMessage;

import java.util.List;


public class Bullet {
    private int id = 0;
    private float x, y;
    private float speedBullet;
    private Connection connection;
    private Game game;
    public boolean broadcasted;


    public Bullet(Connection connection, Game game) {
        this.connection = connection;
        this.game = game;
        this.broadcasted = false;

    }
    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    private void handlePositionMessage(BulletPositionMessage positionMessage) {
        if (positionMessage != null) {
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

    public void addBullet(Bullet bullet, List<Bullet> bullets) {
        if (bullet != null){

            bullets.add(bullet);
        }

    }

    public void broadcastBulletUpdate(BulletPositionMessage positionMessage, Connection[] clientConnections) {
        // Create a message containing the updated bullet position

        BulletPositionMessage updateMessage = new BulletPositionMessage();
        updateMessage.x = positionMessage.x;
        updateMessage.y = positionMessage.y;
        updateMessage.speedBullet = positionMessage.speedBullet;
        // Send the update message to all connected clients
        for (Connection clientConnection : clientConnections) {
            if (clientConnection != null) {
                clientConnection.sendUDP(updateMessage);
            }
        }

    }
}
