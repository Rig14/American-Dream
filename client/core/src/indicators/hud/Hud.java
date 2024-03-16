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
import helper.PlayerState;

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
    private Label localLives;
    private Label localDamage;

    private Label remoteLives;
    private Label remotePlayerName;
    private Label remoteDamage;

    private Label placeHolder;
    private Label gameOverLabel;

    public Hud(SpriteBatch spritebatch) {

        //set up the HUD viewport using a new camera separate from the main game camera
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

        localPlayerName = new Label("TRUMP", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        localLives = new Label(String.valueOf(LIVES_COUNT), new Label.LabelStyle(new BitmapFont(), Color.RED));
        localDamage = new Label("0%", new Label.LabelStyle(new BitmapFont(), Color.RED));

        remotePlayerName = new Label("BIDEN", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        remoteLives = new Label(String.valueOf(LIVES_COUNT), new Label.LabelStyle(new BitmapFont(), Color.RED));
        remoteDamage = new Label("0%", new Label.LabelStyle(new BitmapFont(), Color.RED));

        placeHolder = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        gameOverLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        //add labels to table
        table.add(localPlayerName).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.add(remotePlayerName).expandX().padTop(10);

        table.row();
        table.add(localLives).expandX();
        table.add(countdownLabel).expandX();
        table.add(remoteLives).expandX();

        table.row();
        table.add(localDamage);
        table.add(remoteDamage);

        table.row();
        table.add(placeHolder);
        table.add(gameOverLabel).padTop(150);

        //add table to the stage
        stage.addActor(table);



    }

    public void update(Optional<Integer> time, Optional<PlayerState> local, Optional<PlayerState> remote) {
        // update time
        if (time.isPresent()) {
            int minutes = Math.floorDiv(time.get(), 60);
            int seconds = time.get() % 60;
            countdownLabel.setText(minutes + ":" + String.format("%02d", seconds));
        }

        // update lives, health, damage
        if (local.isPresent() && remote.isPresent()) {

            PlayerState remotePlayer = remote.get();
            PlayerState localPlayer = local.get();

            localLives.setText(localPlayer.getLivesCount());
            localDamage.setText(localPlayer.damage + "%");
            remoteLives.setText(remotePlayer.getLivesCount());
            remoteDamage.setText(remotePlayer.damage + "%");

            // display game over screen when lives reach 0
            if (localPlayer.getLivesCount() == 0) {
                gameOverLabel.setText("GAME OVER!\nYou lost.");
                gameOverLabel.setColor(Color.RED);
            } else if (remotePlayer.getLivesCount() == 0) {
                gameOverLabel.setText("Congratulations you won!");
                gameOverLabel.setColor(Color.GREEN);
            }
        }
    }
}
