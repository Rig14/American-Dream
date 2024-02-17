package ee.taltech.americandream;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import helper.Direction;
import helper.PlayerState;
import helper.packet.GameStateMessage;
import helper.packet.IDMessage;
import helper.packet.PlayerPositionMessage;

import static helper.Constants.*;

public class AmericanDream extends Game {
    public static AmericanDream INSTANCE;
    public static Client client;
    public static int id;
    private OrthographicCamera camera;
    private int screenWidth, screenHeight;

    public AmericanDream() {
        INSTANCE = this;
    }

    @Override
    public void create() {
        // getting screen size
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();

        // set up server connection
        client = new Client();
        registerClasses();
        client.start();
        try {
            client.connect(5000, IP_ADDRESS, PORTS[0], PORTS[1]);
        } catch (Exception e) {
            Gdx.app.log("Client", "Failed to connect to server");
        }
        // listen for id message
        client.addListener(new com.esotericsoftware.kryonet.Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof IDMessage) {
                    IDMessage idMessage = (IDMessage) object;
                    id = idMessage.id;
                }
            }
        });

        // setting up camera
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, screenWidth, screenHeight);
        this.camera.zoom = CAMERA_ZOOM;
        // navigating to the starting screen
        setScreen(new GameScreen(this.camera));
    }

    private void registerClasses() {
        // register classes for serialization
        Kryo kryo = client.getKryo();
        kryo.register(GameStateMessage.class);
        kryo.register(PlayerPositionMessage.class);
        kryo.register(PlayerState[].class);
        kryo.register(PlayerState.class);
        kryo.register(Direction.class);
        kryo.register(IDMessage.class);
    }
}
