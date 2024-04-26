package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import helper.packet.GameLeaveMessage;
import helper.packet.LobbyDataMessage;
import helper.packet.MapSelectionMessage;

import java.util.Map;

import static helper.UI.createLabel;

public class MapSelectionScreen extends ScreenAdapter {
    private final Stage stage;
    private final Camera camera;
    private final String selectedCharacter;
    private final int id;
    private String selectedMap;

    /**
     * Initialize LobbyScreen that contains a button "Start game". Pressing the button will start a new game instance.
     *
     * @param camera used for creating the image that the player will see on the screen
     */
    public MapSelectionScreen(Camera camera, String selectedCharacter, int id) {
        this.stage = new Stage();
        this.camera = camera;
        this.selectedCharacter = selectedCharacter;
        this.id = id;
        Table table = new Table();
        table.setFillParent(true);

        Gdx.input.setInputProcessor(stage);

        // add background to the stage
        Image background = new Image(new Texture(Gdx.files.internal("screen-bg/map_select.png")));
        background.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        Table mapSelectionTable = new Table();
        mapSelectionTable.setFillParent(true);

        // add map selection buttons
        TextButton map1Button = createMapButton("Swamp", new Texture("maps as .png/swamp.png"));
        TextButton map2Button = createMapButton("Desert", new Texture("maps as .png/desert.png"));
        TextButton map3Button = createMapButton("City", new Texture("maps as .png/city.png"));

        table.add(map1Button).pad(10);
        table.add(map2Button).pad(10);
        table.add(map3Button).pad(10);

        mapSelectionTable.center();

        table.add(mapSelectionTable).row();

        // add title text
        Label title = createLabel("Choose the map", Color.WHITE, 1);
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.add(title).padTop(Gdx.graphics.getHeight() / 4f);
        titleTable.top();
        stage.addActor(titleTable);

        stage.addActor(table);
        AmericanDream.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof LobbyDataMessage) {
                    Map<Integer, String> mapsMap = ((LobbyDataMessage) object).maps;
                    selectedMap = mapsMap.get(id);
                }
            }
        });
    }

    private TextButton createMapButton(String mapName, Texture mapTexture) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;
        Image mapPreview = new Image(mapTexture);

        Table mapTable = new Table();
        mapTable.add(mapPreview).size(Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() / 4f).row();
        mapTable.add(createLabel(mapName, Color.WHITE, 2)).padTop(10).row();

        TextButton mapButton = new TextButton("", buttonStyle); // empty text for the button
        mapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // handle map selection
                selectedMap = mapName;
                MapSelectionMessage mapSelectionMessage = new MapSelectionMessage();
                mapSelectionMessage.currentMap = selectedMap;
                AmericanDream.client.sendTCP(mapSelectionMessage);
                AmericanDream.instance.setScreen(new GameScreen(camera, selectedCharacter, selectedMap));
            }
        });
        mapButton.add(mapTable);
        return mapButton;
    }

    /**
     * Render LobbyScreen. Lost connection to the server does not disable starting
     * the game. The only difference is that the other player won't be loaded.
     * Check for the pressing of 'esc', return to LobbySelectionScreen if pressed.
     */
    @Override
    public void render(float delta) {
        super.render(delta);
        // black screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // pressing ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            AmericanDream.instance.setScreen(new LobbySelectionScreen(camera));
            // send message to server to remove player from lobby
            AmericanDream.client.sendTCP(new GameLeaveMessage());
        }
        if (selectedMap != null) {
            AmericanDream.instance.setScreen(new GameScreen(camera, selectedCharacter, selectedMap));
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    /**
     * Handle changing screen size.
     */
    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    public String getSelectedMap() {
        return selectedCharacter;
    }

}
