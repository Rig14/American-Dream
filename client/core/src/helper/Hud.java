package helper;


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

public class Hud {
    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    public Stage stage2;
    private Viewport viewport;

    //labels to be displayed on the hud
    private static Label countdownLabel;
    private Label timeLabel;
    private Label localPlayerName;
    private Label firstHealth;
    private Label remotePlayerName;
    private Label secondHealth;

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
        localLives = new Label("loading...", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        remotePlayerName = new Label("BIDEN   ", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        remoteLives = new Label("loading...", new Label.LabelStyle(new BitmapFont(), Color.WHITE));


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

    // update displayed game time
    public void update(int time, Integer localHealth, Integer remoteHealth) {
        if (time > 0) {
            int minutes = Math.floorDiv(time, 60);
            int seconds = time % 60;
            countdownLabel.setText(minutes + ":" + String.format("%02d", seconds));
        }

        // update lives (spahgetti at the moment because of remoteplayer null value)
        localLives.setText(localHealth);
        if (remoteHealth != null) {
            remoteLives.setText(remoteHealth);

            // display game over screen when lives reach 0
            if (localHealth == 0) {
                gameOverLabel.setText("GAME OVER!\n You lost.");
            } else if (remoteHealth == 0) {
                gameOverLabel.setText("GAME OVER!\n Congratulations you won!");
            }
        }
    }
}
