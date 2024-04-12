package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import helper.packet.GameLeaveMessage;

public class LobbyScreen extends ScreenAdapter {
    private final TextButton startGameButton;
    private final Stage stage;
    private final Camera camera;

    /**
     * Initialize LobbyScreen that contains a button "Start game". Pressing the button will start a new game instance.
     * @param camera used for creating the image that the player will see on the screen
     */
    public LobbyScreen(Camera camera) {
        this.stage = new Stage();
        this.camera = camera;
        Table table = new Table();
        table.setFillParent(true);

        Gdx.input.setInputProcessor(stage);

        // buttons style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;

        // make button
        startGameButton = new TextButton("Start Game", buttonStyle);

        // add button to table
        table.add(startGameButton).row();

        // add listener for start game button
        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // start the game
                AmericanDream.instance.setScreen(new GameScreen(camera));
            }
        });

        stage.addActor(table);
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
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        stage.draw();

        // pressing ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            AmericanDream.instance.setScreen(new LobbySelectionScreen(camera));
            // send message to server to remove player from lobby
            AmericanDream.client.sendTCP(new GameLeaveMessage());
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
}
