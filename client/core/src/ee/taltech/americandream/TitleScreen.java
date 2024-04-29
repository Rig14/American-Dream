package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import helper.Audio;

import static helper.UI.*;


public class TitleScreen extends ScreenAdapter {

    private final TextButton multiplayerButton;
    private final Stage stage;

    /**
     * Initialize TitleScreen where players can choose between multiplayer and local play
     * or exit the game.
     *
     * @param camera used for creating the image that the player will see on the screen
     */
    public TitleScreen(Camera camera) {
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Table mainContainer = new Table();
        mainContainer.setFillParent(true);

        Table buttonsContainer = new Table();
        mainContainer.add(buttonsContainer).row();

        multiplayerButton = createButton("Multiplayer");
        TextButton localButton = createButton("Start Game");
        disableButton(localButton);
        TextButton exitButton = createButton("Exit");

        buttonsContainer.add(localButton).row();
        buttonsContainer.add(new Container<>().height(10)).row();
        buttonsContainer.add(multiplayerButton).row();
        buttonsContainer.add(new Container<>().height(10)).row();
        buttonsContainer.add(exitButton).row();

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
                // navigate to lobby screen
                AmericanDream.instance.setScreen(new LobbySelectionScreen(camera));
            }
        });
        // top content
        Table versionContainer = new Table();
        versionContainer.setFillParent(true);
        versionContainer.top().left();
        versionContainer.pad(5);
        Label version = createLabel("American Dream BETA-0.4", Color.GRAY, 3); // current game version number
        versionContainer.add(version).row();

        // bottom content

        // copyright
        Table copyrightContainer = new Table();
        copyrightContainer.setFillParent(true);
        copyrightContainer.bottom().left();
        copyrightContainer.pad(10);
        Label crText = createLabel("© 2024 RRE Inc", Color.GRAY, 3);
        copyrightContainer.add(crText);

        // add background to the stage
        Image background = new Image(new Texture(Gdx.files.internal("screen-bg/title.jpg")));
        background.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(background);

        stage.addActor(versionContainer);
        stage.addActor(mainContainer);
        stage.addActor(copyrightContainer);

        // start playing music
        Audio.getInstance().playMusic(Audio.AudioType.MENU);
    }

    /**
     * Render TitleScreen if the client is connected to the server. Otherwise, make the buttons
     * unclickable and add a grey shade.
     * Check for the pressing of 'esc', exit game if pressed.
     */
    @Override
    public void render(float delta) {
        stage.act(delta);
        // black background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // if no connection is established, disable the multiplayer button
        // (for example this happens when server is not started)
        if (!AmericanDream.client.isConnected()) {
            disableButton(multiplayerButton);
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
        stage.dispose();
    }

    /**
     * Handle changing screen size.
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        // updates the viewport. if this is not done the buttons will not
        // register clicks correctly
        stage.getViewport().update(width, height, true);
    }
}
