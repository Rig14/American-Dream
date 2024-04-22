package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import helper.packet.JoinLobbyMessage;
import helper.packet.LobbyDataMessage;

import static helper.UI.createButton;
import static helper.UI.createLabel;

public class LobbySelectionScreen extends ScreenAdapter {
    private final Camera camera;
    private final Stage stage;
    private final Table table;
    private LobbyDataMessage lobbyDataMessage;
    private float updateCounter = 0;

    /**
     * Initialize LobbySelectionScreen where the status of all available lobbies is displayed to the player.
     * If another (remote) player joins or leaves a lobby, the number next to lobby's name will change.
     * Receives: LobbyDataMessage
     * Sends: LobbyJoinMessage
     *
     * @param camera used for creating the image that the player will see on the screen
     */
    public LobbySelectionScreen(Camera camera) {
        this.camera = camera;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        this.table = new Table();
        table.center();
        table.setFillParent(true);


        Label placeholder = createLabel("Loading lobbies Please wait. ", Color.WHITE, 1f);
        table.add(placeholder).row();

        AmericanDream.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                if (object instanceof LobbyDataMessage) {
                    lobbyDataMessage = (LobbyDataMessage) object;
                }
            }
        });

        // add table to screen
        stage.addActor(table);
    }

    /**
     * Render LobbySelectionScreen. Lost connection to the server does not disable joining a lobby and starting
     * the game. The only difference is that the other player won't be loaded.
     * Check for the pressing of 'esc', return to TitleScreen if pressed.
     */
    @Override
    public void render(float delta) {
        super.render(delta);

        // black background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        update(delta);
        stage.draw();

        // ESC navigate to title screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            AmericanDream.instance.setScreen(new TitleScreen(camera));
        }
    }

    private void update(float delta) {
        updateCounter += delta;
        if (updateCounter < 1f) return;
        table.clear();

        Label title = createLabel("Select a Lobby:");
        table.add(title).row();
        table.top();

        lobbyDataMessage.lobbies.forEach((id, info) -> {
            TextButton button = createButton(info);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    JoinLobbyMessage message = new JoinLobbyMessage();
                    message.lobbyId = id;
                    AmericanDream.client.sendTCP(message);
                    AmericanDream.instance.setScreen(new LobbyScreen(camera));
                }
            });
            table.add(button).row();
        });

        updateCounter = 0;
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    /**
     * Handle changing screen size.
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }
}
