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

    //Player lives to register changes and update health tables
    private int localLives = LIVES_COUNT;
    private int firstRemoteLives = LIVES_COUNT;
    private int secondRemoteLives = LIVES_COUNT;
    private int thirdRemoteLives = LIVES_COUNT;

    // label styles
    private final Label.LabelStyle whiteDefaultStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    private final Label.LabelStyle redDefaultStyle = new Label.LabelStyle(new BitmapFont(), Color.RED);

    // displayed labels
    private final Label timeTextLabel = new Label("TIME", whiteDefaultStyle);;
    private final Label timeCountdownLabel = new Label( "Waiting for other player...", whiteDefaultStyle);

    private final Label placeHolder = new Label("", whiteDefaultStyle);
    private final Label gameOverLabel = new Label("", whiteDefaultStyle);


    private final Label localPlayerName = new Label("loading...", whiteDefaultStyle);
    private final Table localHealthTable = new Table();
    private final Label localDamage = new Label("0 %", redDefaultStyle);

    private final Label firstRemotePlayerName = new Label("loading...", whiteDefaultStyle);
    private final Table firstRemoteHealthTable = new Table();
    private final Label firstRemoteDamage = new Label("0 %", redDefaultStyle);

    private final Label secondRemotePlayerName = new Label("", whiteDefaultStyle);
    private final Table secondRemoteHealthTable = new Table();
    private final Label secondRemoteDamage = new Label("", redDefaultStyle);

    private final Label thirdRemotePlayerName = new Label("", whiteDefaultStyle);
    private final Table thirdRemoteHealthTable = new Table();
    private final Label thirdRemoteDamage = new Label("", redDefaultStyle);


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

        // fill lives tables
        for (int i = 0; i < LIVES_COUNT; i++) {
            localHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
            firstRemoteHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
            secondRemoteHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
            thirdRemoteHealthTable.add(new Image(HEALTH_TEXTURE)).width(20).height(20);
        }
        secondRemoteHealthTable.setVisible(false);
        thirdRemoteHealthTable.setVisible(false);

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


        // additional remote players
        table.row();
        table.add(secondRemotePlayerName).padTop(150);
        table.add(placeHolder).padTop(150);
        table.add(thirdRemotePlayerName).padTop(150);

        table.row();
        table.add(secondRemoteHealthTable);
        table.add(placeHolder);
        table.add(thirdRemoteHealthTable);

        table.row();
        table.add(secondRemoteDamage);
        table.add(placeHolder);
        table.add(thirdRemoteDamage);

        // add main table to the stage
        stage.addActor(table);
    }

    /**
     * Initialize player names, update game time, players lives and players damage percentage.
     * Updating takes place every game tick.
     * @param time game time in seconds
     * @param local local player
     * @param remote remote players; amount ranging from 1 to (lobbyMaxSize - 1)
     */
    public void update(Optional<Integer> time, Player local, Optional<List<RemotePlayer>> remote) {
        updateTime(time);
        // update lives, health, damage
        if (remote.isPresent()) {
            List<RemotePlayer> remotePlayers = remote.get();
            Player localPlayer = local;

            // Initialize local player fields
            if (localPlayerName.getText().toString().equals("loading...")) {
                localPlayerName.setText(localPlayer.getName());
            }
            localDamage.setText(localPlayer.getDamage() + " %");

            if (localLives != localPlayer.getLivesCount()) {
                localLives = localPlayer.getLivesCount();
                updateLivesTable(localLives, localHealthTable);
            }

            updateRemotePlayers(remotePlayers);

            // display game over screen
            if (localPlayer.getLivesCount() == 0) {
                gameOverLabel.setText("GAME OVER!\n You lost.");
                gameOverLabel.setColor(Color.RED);
            // check if all remote players are defeated
            } else if (remotePlayers.stream().mapToInt(RemotePlayer::getLivesCount).max().getAsInt() == 0) {
                gameOverLabel.setText("Congratulations you won!");
                gameOverLabel.setColor(Color.GREEN);
            }
        }
    }

    /**
     *
     */
    private void updateRemotePlayers(List<RemotePlayer> remotePlayers) {
        int remotePlayerCount = remotePlayers.size();
        if (remotePlayerCount == 1 && firstRemotePlayerName.getText().toString().equals("loading...")) {
            firstRemotePlayerName.setText(remotePlayers.get(0).getName());
        } else if (remotePlayerCount == 2 && secondRemotePlayerName.getText().toString().isEmpty()) {
            secondRemotePlayerName.setText(remotePlayers.get(1).getName());
            secondRemoteHealthTable.setVisible(true);
        } else if (remotePlayerCount == 3 && thirdRemotePlayerName.getText().toString().isEmpty()) {
            thirdRemotePlayerName.setText(remotePlayers.get(2).getName());
            thirdRemoteHealthTable.setVisible(true);
        }
    }

    /**
     * Update lives table according to the lives count. Clear the previous lives table and add new heart image objects.
     * For every displayed heart there has to be a new Image object, otherwise the hearts won't appear on the screen
     * @param  newLivesAmount lives count
     * @param table lives table pointer
     */
    private void updateLivesTable(int newLivesAmount, Table table) {
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
    private void updateTime(Optional<Integer> time) {
        if (time.isPresent()) {
            int minutes = Math.floorDiv(time.get(), 60);
            int seconds = time.get() % 60;
            timeCountdownLabel.setText(minutes + ":" + String.format("%02d", seconds));
        }
    }
}
