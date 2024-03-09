package indicators.hud;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;

import java.util.Optional;

import static helper.Constants.LIVES_COUNT;

public class Hud {
    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    //labels to be displayed on the hud
    private static Label countdownLabel;
    private Label timeLabel;
    private Label localPlayerName;
    private Label remotePlayerName;

    private Label localLives;
    private Label remoteLives;

    private Label placeHolder;
    private Label gameOverLabel;

    public Hud(SpriteBatch spritebatch) {

        //setup the HUD viewport using a new camera separate from the main game camera
        //define stage using HUD viewport and game's spritebatch
        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spritebatch);

        //define a table used to organize the hud's labels
        Table table = new Table();
        //Top-Align table
        table.top();
        //make the table fill the entire stage
        table.setFillParent(true);

        //define labels
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        countdownLabel = new Label( "Waiting for other player...", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // combining player name and heath label by using \n could make alignment easier
        localPlayerName = new Label("TRUMP", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        localLives = new Label("loading...", new Label.LabelStyle(new BitmapFont(), Color.RED));

        remotePlayerName = new Label("BIDEN", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        remoteLives = new Label("loading...", new Label.LabelStyle(new BitmapFont(), Color.RED));


        //add labels to table, padding the top, and giving them all equal width with expandX
        table.add(localPlayerName).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.add(remotePlayerName).expandX().padTop(10);

        //add a second row to the table
        table.row();
        table.add(localLives).expandX();
        table.add(countdownLabel).expandX();
        table.add(remoteLives).expandX();

        // third row
        table.row();
        placeHolder = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        gameOverLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        table.add(placeHolder);
        table.add(gameOverLabel).padTop(150);

        //add table to the stage
        stage.addActor(table);



    }

    // update displayed game time and lives
    public void update(Optional<Integer> time, Integer localHealth, Optional<Integer> remoteHealth) {
        if (time.isPresent()) {
            int minutes = Math.floorDiv(time.get(), 60);
            int seconds = time.get() % 60;
            countdownLabel.setText(minutes + ":" + String.format("%02d", seconds));
        }

        // update lives
        if (remoteHealth.isPresent()) {
            localLives.setText(localHealth);
            remoteLives.setText(remoteHealth.get());

            // display game over screen when lives reach 0
            if (localHealth == 0) {
                gameOverLabel.setText("GAME OVER!\nYou lost.");
                gameOverLabel.setColor(Color.RED);
            } else if (remoteHealth.get() == 0) {
                gameOverLabel.setText("Congratulations you won!");
                gameOverLabel.setColor(Color.GREEN);
            }
        } else {
            localLives.setText(LIVES_COUNT);
            remoteLives.setText(LIVES_COUNT);
        }
    }
}
