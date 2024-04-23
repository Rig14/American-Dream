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

import java.util.Map;

public class LobbyScreen extends ScreenAdapter {
    private final Stage stage;
    private final Camera camera;
    private String selectedCharacter;
    private String selectedMap;
    private int id;

    /**
     * Initialize LobbyScreen that contains a buttons for selecting different characters. Pressing a button will start a new game instance.
     * @param camera used for creating the image that the player will see on the screen
     */
    public LobbyScreen(Camera camera, int id) {
        this.stage = new Stage();
        this.camera = camera;
        this.id = id;
        Table table = new Table();
        table.setFillParent(true);

        Gdx.input.setInputProcessor(stage);

        // buttons style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;

        Table characterSelectionTable = new Table();
        characterSelectionTable.setFillParent(true);

        // Add character selection buttons
        TextButton character1Button = createCharacterButton("Obama", new Texture("obama.jpg"));
        TextButton character2Button = createCharacterButton("Trump", new Texture("trump.jpg"));
        TextButton character3Button = createCharacterButton("Biden", new Texture("biden.jpg"));
        // Add buttons to the table
        table.add(character1Button).size(50, 200).pad(50);
        table.add(character2Button).size(50, 200).pad(50);
        table.add(character3Button).size(50, 200).pad(50);

        characterSelectionTable.center();

        table.add(characterSelectionTable).row();
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
    private TextButton createCharacterButton(String characterName, Texture characterTexture) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;
        // Create an image with the character's texture
        Image characterPreview = new Image(characterTexture);

        // Create a table to hold the character preview and the button
        Table characterTable = new Table();
        characterTable.add(characterPreview).size(100, 100).pad(10).row();
        characterTable.add(new Label(characterName, new Label.LabelStyle(new BitmapFont(), Color.WHITE))).row();

        TextButton characterButton = new TextButton("", buttonStyle); // Empty text for the button

        characterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Handle character selection
                selectedCharacter = characterName;
                if (selectedMap == null) {
                    AmericanDream.instance.setScreen(new MapSelectionScreen(camera, selectedCharacter, id));
                }
            }
        });
        characterButton.add(characterTable).pad(10);
        return characterButton;
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
        if (selectedMap != null && selectedCharacter != null) {
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

    public String getSelectedCharacter() {
        return selectedCharacter;
    }
}
