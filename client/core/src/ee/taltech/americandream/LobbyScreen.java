package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import static helper.Constants.LOBBY_REFRESH_RATE_IN_SECONDS;

public class LobbyScreen extends ScreenAdapter {
    private final Camera camera;
    private final Stage stage;
    private final Table table;
    private final Label.LabelStyle titleStyle;
    private float timeSinceLastUpdate = 0;

    public LobbyScreen(Camera camera) {
        this.camera = camera;
        this.stage = new Stage();
        this.table = new Table();

        // title text style
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(5);
        this.titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);

        // to update lobby on load
        this.timeSinceLastUpdate = LOBBY_REFRESH_RATE_IN_SECONDS + 1;

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
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
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

        // reset time since last update
        timeSinceLastUpdate = 0;
    }
}
