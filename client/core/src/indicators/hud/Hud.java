package indicators.hud;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;
import helper.PlayerState;

import java.util.Optional;

import static helper.Constants.LIVES_COUNT;
import static helper.Textures.HEALTH_TEXTURE;

public class Hud {
    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    //Player lives to register changes and update healthTables
    private int localLives = LIVES_COUNT;
    private int remoteLives = LIVES_COUNT;

    //labels to be displayed on the hud
    private Label timeTextLabel;
    private Label timeCountdownLabel;

    private Label localPlayerName;
    private Table localHealthTable = new Table();
    private Label localDamage;

    private Label remotePlayerName;
    private Table remoteHealthTable = new Table();
    private Label remoteDamage;

    private Label placeHolder;
    private Label gameOverLabel;



    public Hud(SpriteBatch spritebatch) {

        // set up the HUD viewport using a new camera separate from the main game camera
        // define stage using HUD viewport and game's spritebatch
        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spritebatch);

        Table table = new Table();  // define a table used to organize the hud's labels
        table.top();                // Top-Align table
        table.setFillParent(true);  // make the table fill the entire stage

        // define labels
        Label.LabelStyle whiteDefaultStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label.LabelStyle redDefaultStyle = new Label.LabelStyle(new BitmapFont(), Color.RED);

        timeTextLabel = new Label("TIME", whiteDefaultStyle);
        timeCountdownLabel = new Label( "Waiting for other player...", whiteDefaultStyle);


        localPlayerName = new Label("TRUMP", whiteDefaultStyle);
        for (int i = 0; i < LIVES_COUNT; i++) {
            localHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
        }
        localDamage = new Label("0 %", redDefaultStyle);


        remotePlayerName = new Label("BIDEN", whiteDefaultStyle);
        for (int i = 0; i < LIVES_COUNT; i++) {
            remoteHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
        }
        remoteDamage = new Label("0 %", redDefaultStyle);


        placeHolder = new Label("", whiteDefaultStyle);
        gameOverLabel = new Label("", whiteDefaultStyle);


        //add labels to table
        table.add(localPlayerName).expandX().padTop(10);
        table.add(timeTextLabel).expandX().padTop(10);
        table.add(remotePlayerName).expandX().padTop(10);

        table.row();
        table.add(localHealthTable).expandX();
        // table.add(localLives).expandX();
        table.add(timeCountdownLabel).expandX();
        table.add(remoteHealthTable).expandX();

        table.row();
        table.add(localDamage);
        table.add(placeHolder);
        table.add(remoteDamage);

        table.row();
        table.add(placeHolder);
        table.add(gameOverLabel).padTop(150);

        //add table to the stage
        stage.addActor(table);
    }

    public void update(Optional<Integer> time, Optional<PlayerState> local, Optional<PlayerState> remote) {
        updateTime(time);
        // update lives, health, damage
        if (local.isPresent() && remote.isPresent()) {
            PlayerState remotePlayer = remote.get();
            PlayerState localPlayer = local.get();

            localDamage.setText(localPlayer.damage + " %");
            remoteDamage.setText(remotePlayer.damage + " %");

            if (localLives != localPlayer.getLivesCount()) {
                localLives = localPlayer.getLivesCount();
                updateLivesTable(localLives, localHealthTable);
            }
            if (remoteLives != remotePlayer.getLivesCount()) {
                remoteLives = remotePlayer.getLivesCount();
                updateLivesTable(remoteLives, remoteHealthTable);
            }

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

    public void updateLivesTable(int newLivesAmount, Table table) {
        table.clear();
        for(int i = 0; i < newLivesAmount; i++) {
            table.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
        }
    }

    public void updateTime(Optional<Integer> time) {
        if (time.isPresent()) {
            int minutes = Math.floorDiv(time.get(), 60);
            int seconds = time.get() % 60;
            timeCountdownLabel.setText(minutes + ":" + String.format("%02d", seconds));
        }
    }
}
