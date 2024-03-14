package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

import java.util.HashMap;
import java.util.Map;

import static helper.Constants.LOBBY_REFRESH_RATE_IN_SECONDS;

public class LobbySelectionScreen extends ScreenAdapter {
    private final Camera camera;
    private final Stage stage;
    private final Table table;
    private final Label.LabelStyle titleStyle;
    private final TextButton.TextButtonStyle buttonStyle;
    private float timeSinceLastUpdate = 0;
    private Map<Integer, String> lobbyData;

    public LobbySelectionScreen(Camera camera) {
        this.camera = camera;
        this.stage = new Stage();
        this.table = new Table();
        this.lobbyData = new HashMap<>();

        // title text style
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(5);
        this.titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);

        // buttons style
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;

        // to update lobby on load
        this.timeSinceLastUpdate = LOBBY_REFRESH_RATE_IN_SECONDS - 0.5f;

        // add listener for lobby data messages
        AmericanDream.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof LobbyDataMessage) {
                    LobbyDataMessage lobbyDataMessage = (LobbyDataMessage) object;
                    lobbyData = lobbyDataMessage.lobbies;
                }
            }
        });

        Gdx.input.setInputProcessor(stage);

        // add table to screen and make text align top
        table.setFillParent(true);
        table.top();
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // black background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // update lobby screen
        update(delta);

        stage.draw();

        // ESC navigate to title screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            AmericanDream.instance.setScreen(new TitleScreen(camera));
        }
    }

    private void update(float delta) {
        timeSinceLastUpdate += delta;
        if (timeSinceLastUpdate < LOBBY_REFRESH_RATE_IN_SECONDS) return;
        // update every N seconds
        table.clear();

        // title text
        table.add(new Label("Choose a lobby", titleStyle)).row();

        // add lobbies to table
        lobbyData.forEach((id, status) -> {
            // add button for each lobby
            TextButton button = new TextButton(status, buttonStyle);

            // add listener to button
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    // if button clicked join the lobby
                    // construct message
                    JoinLobbyMessage joinLobbyMessage = new JoinLobbyMessage();
                    joinLobbyMessage.lobbyId = id;

                    // send message to server
                    AmericanDream.client.sendTCP(joinLobbyMessage);

                    // navigate to lobby screen
                    AmericanDream.instance.setScreen(new LobbyScreen(camera));
                }
            });

            // add button to table
            table.add(button).row();
        });

        // reset time since last update
        timeSinceLastUpdate = 0;
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }
}
