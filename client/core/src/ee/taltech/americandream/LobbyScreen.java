package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LobbyScreen extends ScreenAdapter {
    private final TextButton startGameButton;
    private final Stage stage;

    public LobbyScreen(Camera camera) {
        this.stage = new Stage();
        Table table = new Table();
        table.setFillParent(true);

        // buttons style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;

        // make button
        startGameButton = new TextButton("Start Game", buttonStyle);

        // add listener for start game button
        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                AmericanDream.instance.setScreen(new GameScreen(camera));
            }
        });

        // add button to table
        table.add(startGameButton).row();
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // black screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }
}
