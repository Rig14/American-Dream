package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import helper.Audio;
import helper.packet.JoinLobbyMessage;

import java.net.InetSocketAddress;

import static helper.UI.*;


public class TitleScreen extends ScreenAdapter {

    private final TextButton multiplayerButton;
    private final TextButton localButton;
    private final TextField textField;
    private final Stage stage;
    private final Table errorTable = new Table();

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
        mainContainer.add(buttonsContainer).center();

        multiplayerButton = createButton("Multiplayer");
        localButton = createButton("Start Game");
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

        localButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // start ai game
                JoinLobbyMessage message = new JoinLobbyMessage();
                message.lobbyId = AmericanDream.id;
                message.AIGame = true;
                AmericanDream.client.sendTCP(message);
                AmericanDream.instance.setScreen(new GameScreen(camera, "AIGame", "Desert"));
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

        // sliders
        Table sliderContainer = new Table();
        sliderContainer.setFillParent(true);
        sliderContainer.bottom().right();
        sliderContainer.pad(10);
        Slider volume = createSlider(0f, 1f, 0.01f, false);
        Label volumeLabel = createLabel("Volume", Color.GRAY, 2);
        volume.setValue(Audio.getInstance().getSoundVolume());
        volume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Audio.getInstance().setSoundVolume(volume.getValue());
            }
        });
        Table volumeContainer = new Table();
        volumeContainer.add(volumeLabel).padRight(10);
        volumeContainer.add(volume);
        sliderContainer.add(volumeContainer).row();

        Slider music = createSlider(0f, 1f, 0.01f, false);
        music.setValue(Audio.getInstance().getMusicVolume());
        music.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Audio.getInstance().setMusicVolume(music.getValue());
            }
        });
        Table musicContainer = new Table();
        Label musicLabel = createLabel("Music", Color.GRAY, 2);
        musicContainer.add(musicLabel).padRight(10);
        musicContainer.add(music);
        sliderContainer.add(musicContainer).bottom().right().row();

        // ip settings
        Table ipTable = new Table();
        ipTable.setFillParent(true);

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        BitmapFont theFont = generator.generateFont(parameter);
        style.font = theFont;
        style.fontColor = Color.WHITE;
        style.cursor = new TextureRegionDrawable(new TextureRegion(new Texture("blinker.png")));
        style.background = new TextureRegionDrawable(new TextureRegion(new Texture("textfield.png")));
        style.selection = new TextureRegionDrawable(new TextureRegion(new Texture("selected.png")));
        InetSocketAddress socketAddress = AmericanDream.client.getRemoteAddressTCP();
        textField = new TextField(socketAddress == null ? "" : socketAddress.getHostName(), style);
        textField.setAlignment(1);
        textField.setMaxLength(15);

        ipTable.add(textField);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = theFont;
        buttonStyle.fontColor = Color.WHITE;
        TextButton button = new TextButton("Set IP", buttonStyle);
        button.getStyle().up = new TextureRegionDrawable(new TextureRegion(new Texture("special_button_ip.png")));
        button.getStyle().over = new TextureRegionDrawable(new TextureRegion(new Texture("special_button_ip.png"))).tint(Color.BLACK);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Audio.getInstance().playSound(Audio.SoundType.BUTTON_CLICK);
                // disable the multiplayer and singleplayer button until connection is established
                disableButton(multiplayerButton);
                disableButton(localButton);

                AmericanDream.setupConnection(textField.getText());
            }
        });
        ipTable.add(button);
        ipTable.top().right();
        generator.dispose();

        errorTable.setFillParent(true);
        errorTable.bottom();
        Label errorLabel = createLabel("Could not connect", Color.RED, 1);
        errorTable.add(errorLabel);

        stage.addActor(background);

        stage.addActor(versionContainer);
        stage.addActor(mainContainer);
        stage.addActor(sliderContainer);
        stage.addActor(copyrightContainer);
        stage.addActor(ipTable);
        stage.addActor(errorTable);

        // start playing music
        Audio.getInstance().playMusic(Audio.MusicType.MENU);
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
            disableButton(localButton);
            // display error message
            errorTable.setVisible(true);
        } else {
            enableButton(multiplayerButton);
            enableButton(localButton);
            errorTable.setVisible(false);
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
