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
import static helper.Textures.BLACK_HEART_TEXTURE;
import static helper.Textures.HEALTH_TEXTURE;

public class Hud {
    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    // Player names
    private String localName = "loading...";
    private String remoteName = "loading...";

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

    /**
     * Initialize HUD.
     * Create a table that contains information about: game time, player names, lives, damage percentage and
     * "game over" message.
     * Some table slots are empty at first or contain "loading..." style placeholders.
     * Game time and player names placeholders are filled as soon as the client receives remote player's state object.
     * @param spritebatch spritebatch where to render all information
     */
    public Hud(SpriteBatch spritebatch) {

        // set up the HUD viewport using a new camera separate from the main game camera
        // define stage using HUD viewport and game's spritebatch
        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), new OrthographicCamera());
        stage = new Stage(viewport, spritebatch);

        Table table = new Table();  // define a table used to organize the hud's labels
        table.top();                // Top-Align table
        table.setFillParent(true);  // make the table fill the entire stage

        // define label styles
        Label.LabelStyle whiteDefaultStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label.LabelStyle redDefaultStyle = new Label.LabelStyle(new BitmapFont(), Color.RED);

        // define labels
        timeTextLabel = new Label("TIME", whiteDefaultStyle);
        timeCountdownLabel = new Label( "Waiting for other player...", whiteDefaultStyle);


        localPlayerName = new Label(localName, whiteDefaultStyle);
        for (int i = 0; i < LIVES_COUNT; i++) {
            localHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
        }
        localDamage = new Label("0 %", redDefaultStyle);


        remotePlayerName = new Label(remoteName, whiteDefaultStyle);
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
        table.add(localHealthTable).expandX().padTop(5);
        table.add(timeCountdownLabel).expandX().padTop(5);
        table.add(remoteHealthTable).expandX().padTop(5);

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

    /**
     * Initialize player names, update game time, players lives and players damage percentage.
     * Updating takes place every game tick.
     * @param time game time in seconds
     * @param local local player's state containing: name, health, damage
     * @param remote remote player's state containing: name, health, damage
     */
    public void update(Optional<Integer> time, Optional<PlayerState> local, Optional<PlayerState> remote) {
        updateTime(time);
        // update lives, health, damage
        if (local.isPresent() && remote.isPresent()) {
            PlayerState remotePlayer = remote.get();
            PlayerState localPlayer = local.get();

            // Initialize names
            if (localName.equals("loading...") && localPlayer.name != null && remotePlayer.name != null) {
                localName = localPlayer.name;
                remoteName = remotePlayer.name;
                localPlayerName.setText(localName);
                remotePlayerName.setText(remoteName);
            }

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
                gameOverLabel.setText("GAME OVER!\n" + localName + " lost.");
                gameOverLabel.setColor(Color.RED);
            } else if (remotePlayer.getLivesCount() == 0) {
                gameOverLabel.setText("Congratulations " + localName + " won!");
                gameOverLabel.setColor(Color.GREEN);
            }
        }
    }

    /**
     * Update lives table according to the lives count. Clear the previous lives table and add new heart image objects.
     * For every displayed heart there has to be a new Image object, otherwise the hearts won't appear on the screen
     * @param  newLivesAmount lives count
     * @param table lives table pointer
     */
    public void updateLivesTable(int newLivesAmount, Table table) {
        table.clear();
        // lost lives
        for(int i = 0; i < LIVES_COUNT - newLivesAmount; i++) {
            table.add(new Image(BLACK_HEART_TEXTURE)).width(20).height(20);
        }
        // remaining lives
        for(int i = 0; i < newLivesAmount; i++) {
            table.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
        }
    }

    /**
     * Update the displayed game time.
     * @param time new game time in seconds
     */
    public void updateTime(Optional<Integer> time) {
        if (time.isPresent()) {
            int minutes = Math.floorDiv(time.get(), 60);
            int seconds = time.get() % 60;
            timeCountdownLabel.setText(minutes + ":" + String.format("%02d", seconds));
        }
    }
}
