package ee.taltech.americandream;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import helper.BulletData;
import helper.Direction;
import helper.PlayerState;
import helper.packet.BulletMessage;
import helper.packet.GameStateMessage;
import helper.packet.IDMessage;
import helper.packet.PlayerPositionMessage;

import java.util.ArrayList;

import static helper.Constants.*;

public class AmericanDream extends Game {
    // static variables, you can access these
    // from literally anywhere in the code
    public static Client client;
    public static int id;
    public static AmericanDream instance;

    /*
     * This method is called when the game is created.
     * e.g. when user opens the game.
     */
    @Override
    public void create() {
        instance = this;
        setupConnection();

        // listen for id message
        client.addListener(new com.esotericsoftware.kryonet.Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof IDMessage) {
                    IDMessage idMessage = (IDMessage) object;
                    // id is used to identify the player
                    id = idMessage.id;
                }
            }
        });

        // setting up camera
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = CAMERA_ZOOM;
        // move to the game if debug is enabled
        if (DEBUG) {
            setScreen(new GameScreen(camera));
        } else {
            // navigating to the title screen
            setScreen(new TitleScreen(camera));
        }
    }

    /*
     * This method registers classes for serialization.
     * Classes that are sent over the network need to be registered.
     */
    private void registerClasses() {
        // register classes for serialization
        Kryo kryo = client.getKryo();
        kryo.register(GameStateMessage.class);
        kryo.register(PlayerPositionMessage.class);
        kryo.register(PlayerState[].class);
        kryo.register(PlayerState.class);
        kryo.register(Direction.class);
        kryo.register(IDMessage.class);
        kryo.register(BulletMessage.class);
        kryo.register(BulletData.class);
        kryo.register(ArrayList.class);
    }

    /*
     * This method sets up the connection to the server.
     */
    private void setupConnection() {
        client = new Client();
        registerClasses();
        client.start();
        try {
            client.connect(5000, IP_ADDRESS, PORTS[0], PORTS[1]);
        } catch (Exception e) {
            Gdx.app.log("Client", "Failed to connect to server");
        }
    }

    /*
     * This method is called when the game is closed.
     */
    @Override
    public void dispose() {
        client.close();
    }
}
