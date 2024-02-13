package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.awt.*;
import java.io.IOException;

public class GameServer {
    private Server server = new Server();

    private Point player = new Point(0, 0);

    public GameServer() {
        this.server = new Server();
        try {
            server.start();
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.addListener(new Listener(){
            public void received(Connection connection, Object object) {
                if (!(object instanceof String request)) return;
                if (request.equals("Left")) {
                    player.x -= 1;
                }
                if (request.equals("Right")) {
                    player.x += 1;
                }
                connection.sendTCP(player.x + "," + player.y);
            }
        });
    }

    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
    }
}
