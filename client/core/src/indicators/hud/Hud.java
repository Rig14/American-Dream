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
import objects.player.Player;
import objects.player.RemotePlayer;

import java.util.List;
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
    private final Label timeTextLabel;
    private final Label timeCountdownLabel;

    private final Label localPlayerName;
    private final Table localHealthTable = new Table();
    private final Label localDamage;

    private final Label firstRemotePlayerName;
    private final Table firstRemoteHealthTable = new Table();
    private final Label firstRemoteDamage;

    private final Label placeHolder;
    private final Label gameOverLabel;

    // second and third remote players
    private final Label secondRemotePlayerName = new Label("aaaaaaaaaaaaaaa", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
    private final Table secondRemoteHealthTable = new Table();
    private final Label secondRemoteDamage = new Label("dam", new Label.LabelStyle(new BitmapFont(), Color.RED));
    private final Label thirdRemotePlayerName = new Label("ph   3", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
    private final Table thirdRemoteHealthTable = new Table();
    private final Label thirdRemoteDamage = new Label("dam 3", new Label.LabelStyle(new BitmapFont(), Color.RED));

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
        localDamage = new Label("0 %", redDefaultStyle);

        firstRemotePlayerName = new Label(remoteName, whiteDefaultStyle);
        firstRemoteDamage = new Label("0 %", redDefaultStyle);

        for (int i = 0; i < LIVES_COUNT; i++) {
            localHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
            firstRemoteHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
        }

        placeHolder = new Label("", whiteDefaultStyle);
        gameOverLabel = new Label("", whiteDefaultStyle);


        //add labels to table
        table.add(localPlayerName).expandX().padTop(10);
        table.add(timeTextLabel).expandX().padTop(10);
        table.add(firstRemotePlayerName).expandX().padTop(10);

        table.row();
        table.add(localHealthTable).expandX().padTop(5);
        table.add(timeCountdownLabel).expandX().padTop(5);
        table.add(firstRemoteHealthTable).expandX().padTop(5);

        table.row();
        table.add(localDamage);
        table.add(placeHolder);
        table.add(firstRemoteDamage);

        table.row();
        table.add(placeHolder);
        table.add(gameOverLabel).padTop(150);

        // add main table to the stage
        stage.addActor(table);


        // create empty slots for potential additional remote players
        Table additionalRemotePlayersTable = new Table();
        additionalRemotePlayersTable.bottom();
        table.setFillParent(true);

        additionalRemotePlayersTable.add(secondRemotePlayerName);
        additionalRemotePlayersTable.add(placeHolder);
        additionalRemotePlayersTable.add(thirdRemotePlayerName);

        table.row();
        additionalRemotePlayersTable.add(secondRemoteHealthTable);
        additionalRemotePlayersTable.add(placeHolder);
        additionalRemotePlayersTable.add(thirdRemoteHealthTable);

        table.row();
        additionalRemotePlayersTable.add(secondRemoteDamage);
        additionalRemotePlayersTable.add(placeHolder);
        additionalRemotePlayersTable.add(thirdRemoteDamage);

        stage.addActor(additionalRemotePlayersTable);
    }

    /**
     * Initialize player names, update game time, players lives and players damage percentage.
     * Updating takes place every game tick.
     * @param time game time in seconds
     * @param local local player
     * @param remotePlayers remote players; amount ranging from 1 to (lobbyMaxSize - 1)
     */
    public void update(Optional<Integer> time, Player local, Optional<List<RemotePlayer>> remotePlayers) {
        updateTime(time);
        // update lives, health, damage
        if (remotePlayers.isPresent()) {
            Player localPlayer = local;
            RemotePlayer firstRemotePlayer = remotePlayers.get().getFirst();  // "mandatory" first remote player

            // Initialize names
            if (localName.equals("loading...") && firstRemotePlayer.getName() != null) {
                localName = localPlayer.getName();
                remoteName = firstRemotePlayer.getName();
                localPlayerName.setText(localName);
                firstRemotePlayerName.setText(remoteName);
            }

            localDamage.setText(localPlayer.getDamage() + " %");
            firstRemoteDamage.setText(firstRemotePlayer.getDamage() + " %");

            if (localLives != localPlayer.getLivesCount()) {
                localLives = localPlayer.getLivesCount();
                updateLivesTable(localLives, localHealthTable);
            }
            if (remoteLives != firstRemotePlayer.getLivesCount()) {
                remoteLives = firstRemotePlayer.getLivesCount();
                updateLivesTable(remoteLives, firstRemoteHealthTable);
            }

            // display game over screen when lives reach 0
            if (localPlayer.getLivesCount() == 0) {
                gameOverLabel.setText("GAME OVER!\n" + localName + " lost.");
                gameOverLabel.setColor(Color.RED);
            } else if (firstRemotePlayer.getLivesCount() == 0) {
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
