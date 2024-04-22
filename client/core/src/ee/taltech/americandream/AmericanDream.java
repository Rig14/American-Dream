package ee.taltech.americandream;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import helper.BulletData;
import helper.Direction;
import helper.PlayerState;
import helper.packet.*;

import java.util.ArrayList;
import java.util.HashMap;

import static helper.Constants.*;

public class AmericanDream extends Game {
    public static Client client;
    public static int id;
    public static AmericanDream instance;

    /**
     * This method is called when the game is created.
     * e.g. when user opens the game.
     */
    @Override
    public void create() {
        instance = this;
        setupConnection();

        // setting up camera
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = CAMERA_ZOOM;
        // navigating to the title screen
        setScreen(new TitleScreen(camera));
    }

    /**
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
        kryo.register(BulletMessage.class);
        kryo.register(BulletData.class);
        kryo.register(ArrayList.class);
        kryo.register(LobbyDataMessage.class);
        kryo.register(HashMap.class);
        kryo.register(JoinLobbyMessage.class);
        kryo.register(GameLeaveMessage.class);
        kryo.register(AddAIMessage.class);
        kryo.register(MapSelectionMessage.class);
    }

    /**
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

        id = client.getID();
    }

    /**
     * This method is called when the game is closed.
     */
    @Override
    public void dispose() {
        client.close();
    }
}
