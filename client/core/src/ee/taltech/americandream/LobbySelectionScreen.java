package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import helper.packet.JoinLobbyMessage;
import helper.packet.LobbyDataMessage;

import static helper.UI.*;

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


        Label placeholder = createLabel("Loading lobbies...", Color.WHITE, 1f);
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
        stage.act(delta);

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

    /**
     * Updates lobby data on the screen.
     * Sends: JoinLobbyMessage
     *
     * @param delta time since the last render
     */
    private void update(float delta) {
        updateCounter += delta;
        if (updateCounter < 1f || lobbyDataMessage == null) return;
        stage.clear();
        table.clear();
        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.pad(30);
        table.setFillParent(true);

        TextButton back = createButton("Back", 2);
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                AmericanDream.instance.setScreen(new TitleScreen(camera));
            }
        });
        backTable.add(back);
        backTable.top().left();
        Label title = createLabel("Select a Lobby:", Color.WHITE, 1.5f);
        table.add(title).padBottom(40).center().row();
        table.pad(30);
        table.center();

        lobbyDataMessage.lobbies.forEach((id, name) -> {
            Integer playerCount = lobbyDataMessage.playerCount.get(id);
            Integer maxPlayerCount = lobbyDataMessage.maxPlayers.get(id);
            TextButton button = createButton(name + " " + playerCount + "/" + maxPlayerCount, 1.5f);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    JoinLobbyMessage message = new JoinLobbyMessage();
                    message.lobbyId = id;
                    AmericanDream.client.sendTCP(message);
                    AmericanDream.instance.setScreen(new LobbyScreen(camera, id));
                }
            });
            if (playerCount >= maxPlayerCount) {
                disableButton(button);
            }
            table.add(button).left().padBottom(20).row();
        });

        // add background
        Image background = new Image(new Texture(Gdx.files.internal("screen-bg/ls.png")));
        background.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);
        stage.addActor(backTable);
        stage.addActor(table);

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
