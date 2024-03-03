package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;


public class TitleScreen extends ScreenAdapter {
    private final TextButton multiplayerButton;

    private final Stage stage;

    public TitleScreen(OrthographicCamera camera) {
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        // white texts for buttons
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        textButtonStyle.fontColor = Color.WHITE;

        multiplayerButton = new TextButton("Multiplayer", textButtonStyle);
        TextButton localButton = new TextButton("Local play", textButtonStyle);
        TextButton exitButton = new TextButton("Exit", textButtonStyle);

        table.add(multiplayerButton).row();
        table.add(localButton).row();
        table.add(exitButton).row();

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // exit the app
                Gdx.app.exit();
            }
        });

        multiplayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // navigate to multiplayer screen
                AmericanDream.instance.setScreen(new GameScreen(camera));
            }
        });

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // black background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // if no connection is established, disable the multiplayer button
        // (for example this happens when server is not started)
        if (!AmericanDream.client.isConnected()) {
            multiplayerButton.setDisabled(true);
            TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.font = new BitmapFont();
            textButtonStyle.fontColor = Color.GRAY;
            multiplayerButton.setStyle(textButtonStyle);
        }

        // draw all the buttons
        stage.draw();

        // esc is pressed. "Just" is required to prevent
        // exiting the game immediately after entering the screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        // updates the viewport. if this is not done the buttons will not
        // register clicks correctly
        stage.getViewport().update(width, height, true);
    }
}
