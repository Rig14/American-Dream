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

import java.util.Map;
import java.util.Objects;

public class LobbySelectionScreen extends ScreenAdapter {
    private final Camera camera;
    private final Stage stage;
    private final Table table;
    private final Label.LabelStyle titleStyle;
    private final TextButton.TextButtonStyle buttonStyle;
    private final TextButton.TextButtonStyle disabledButtonStyle;


    /**
     * Initialize LobbySelectionScreen where the status of all available lobbies is displayed to the player.
     * If another (remote) player joins or leaves a lobby, the number next to lobby's name will change.
     * Receives: LobbyDataMessage
     * Sends: LobbyJoinMessage
     * @param camera used for creating the image that the player will see on the screen
     */
    public LobbySelectionScreen(Camera camera) {
        this.camera = camera;
        this.stage = new Stage();
        this.table = new Table();

        // title text style
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(5);
        this.titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);

        // buttons style
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.font.getData().setScale(3);
        buttonStyle.fontColor = Color.WHITE;

        // disabled button style
        disabledButtonStyle = new TextButton.TextButtonStyle();
        disabledButtonStyle.font = new BitmapFont();
        disabledButtonStyle.font.getData().setScale(3);
        disabledButtonStyle.fontColor = Color.GRAY;

        // add listener for lobby data messages
        AmericanDream.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof LobbyDataMessage) {
                    LobbyDataMessage lobbyDataMessage = (LobbyDataMessage) object;
                    Map<Integer, String> lobbyData = lobbyDataMessage.lobbies;
                    // update table

                    table.clear();

                    table.add(new Label("Select a lobby", titleStyle)).row();

                    // add lobbies to table
                    lobbyData.forEach((id, status) -> {
                        // add button for each lobby
                        TextButton button = new TextButton(status, buttonStyle);

                        // if game is full don't add a listener because the button
                        // can't be clicked
                        String players = status.split(" ")[status.split(" ").length - 1];
                        if (!Objects.equals(players.split("/")[0], players.split("/")[1])) {
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
                        } else {
                            // disable button
                            button.setDisabled(true);
                            button.setStyle(disabledButtonStyle);
                        }

                        // add button to table
                        table.add(button).row();
                    });
                }
            }
        });

        Gdx.input.setInputProcessor(stage);

        // add table to screen and make text align top
        table.setFillParent(true);
        table.top();
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

        stage.draw();

        // ESC navigate to title screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            AmericanDream.instance.setScreen(new TitleScreen(camera));
        }
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
