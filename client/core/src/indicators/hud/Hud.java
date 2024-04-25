package indicators.hud;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;
import helper.UI;
import objects.player.Player;
import objects.player.RemotePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static helper.Constants.LIVES_COUNT;
import static helper.Constants.REMOTE_PLAYER_COLORS;
import static helper.Textures.BLACK_HEART_TEXTURE;
import static helper.Textures.BULLET_TEXTURE;
import static helper.Textures.HEALTH_TEXTURE;

public class Hud {
    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private final Viewport viewport;
    private final Integer TOP_ROW_PADDING = 25;
    private final Integer ROW_PADDING = 10;
    private float heartScaling;

    //Player lives to register changes and update health tables
    private int localLives = LIVES_COUNT;
    private int firstRemoteLivesDisplayed = LIVES_COUNT;
    private int secondRemoteLivesDisplayed = LIVES_COUNT;
    private int thirdRemoteLivesDisplayed = LIVES_COUNT;

    // displayed labels
    private final Label timeTextLabel = UI.createLabel("TIME");
    private final Label timeCountdownLabel = UI.createLabel("Waiting for other player...",  Color.WHITE, 2);

    private final Label placeHolder = UI.createLabel("");
    private final Label gameOverLabel = UI.createLabel("");


    private final Label localPlayerName = UI.createLabel("loading...", Color.GREEN, 2);
    private final Table localHealthTable = new Table();
    private final Label localDamage = UI.createLabel("0 %", Color.RED, 2);
    private final Label localAmmoCount = UI.createLabel("0", Color.PURPLE, 1);

    private final Label firstRemotePlayerName = UI.createLabel("loading...", REMOTE_PLAYER_COLORS.get(0), 2);
    private final Table firstRemoteHealthTable = new Table();
    private final Label firstRemoteDamage = UI.createLabel("0 %", Color.RED, 2);

    private final Label secondRemotePlayerName = UI.createLabel("", REMOTE_PLAYER_COLORS.get(1), 2);
    private final Table secondRemoteHealthTable = new Table();
    private final Label secondRemoteDamage = UI.createLabel("", Color.RED, 2);

    private final Label thirdRemotePlayerName = UI.createLabel("", REMOTE_PLAYER_COLORS.get(2), 2);
    private final Table thirdRemoteHealthTable = new Table();
    private final Label thirdRemoteDamage = UI.createLabel("", Color.RED, 2);

    // lists of changing values
    private final List<Label> nameLabels = List.of(firstRemotePlayerName, secondRemotePlayerName, thirdRemotePlayerName);
    private final List<Label> damageLabels = List.of(firstRemoteDamage, secondRemoteDamage, thirdRemoteDamage);
    private final List<Table> healthTables = List.of(firstRemoteHealthTable, secondRemoteHealthTable, thirdRemoteHealthTable);
    private final List<Integer> livesDisplayed = new ArrayList<>(
            List.of(firstRemoteLivesDisplayed, secondRemoteLivesDisplayed, thirdRemoteLivesDisplayed)
    );

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

        heartScaling = (float) stage.getViewport().getScreenHeight() / 25;  // should find a better method
        // fill lives tables
        for (int i = 0; i < LIVES_COUNT; i++) {
            localHealthTable.add(new Image(HEALTH_TEXTURE)).size(heartScaling);
            firstRemoteHealthTable.add(new Image(HEALTH_TEXTURE)).size(heartScaling);
            secondRemoteHealthTable.add(new Image(HEALTH_TEXTURE)).size(heartScaling);
            thirdRemoteHealthTable.add(new Image(HEALTH_TEXTURE)).size(heartScaling);
        }
        secondRemoteHealthTable.setVisible(false);
        thirdRemoteHealthTable.setVisible(false);

        //add labels to table
        table.add(localPlayerName).expandX().padTop(TOP_ROW_PADDING);
        table.add(thirdRemotePlayerName).expandX().padTop(TOP_ROW_PADDING);
        table.add(timeTextLabel).expandX().padTop(TOP_ROW_PADDING);
        table.add(secondRemotePlayerName).expandX().padTop(TOP_ROW_PADDING);
        table.add(firstRemotePlayerName).expandX().padTop(TOP_ROW_PADDING);

        table.row();
        table.add(localHealthTable).padTop(ROW_PADDING);
        table.add(thirdRemoteHealthTable).padTop(ROW_PADDING);
        table.add(timeCountdownLabel).padTop(ROW_PADDING);
        table.add(secondRemoteHealthTable).padTop(ROW_PADDING);
        table.add(firstRemoteHealthTable).padTop(ROW_PADDING);

        table.row();
        table.add(localDamage).padTop(ROW_PADDING);
        table.add(thirdRemoteDamage).padTop(ROW_PADDING);
        table.add(placeHolder).padTop(ROW_PADDING);
        table.add(secondRemoteDamage).padTop(ROW_PADDING);
        table.add(firstRemoteDamage).padTop(ROW_PADDING);

        table.row();
        Table ammo = new Table();
        Image bulletLogo = new Image(BULLET_TEXTURE);
        bulletLogo.rotateBy(90);
        ammo.add(bulletLogo).size(heartScaling);
        ammo.add(localAmmoCount).padLeft(-10);
        table.add(ammo).padTop(heartScaling).padLeft(heartScaling / 2);

        table.row();
        table.add(placeHolder);
        table.add(placeHolder);
        table.add(gameOverLabel).padTop(heartScaling * 9);  // should find a better method

        // add main table to the stage
        stage.addActor(table);
    }

    /**
     * Initialize player names, update game time, players lives and players damage percentage.
     * Updating takes place every game tick.
     * @param time game time in seconds
     * @param localPlayer local player
     * @param remotePlayers remote players; amount ranging from 0 to (lobbyMaxSize - 1)
     */
    public void update(Optional<Integer> time, Player localPlayer, List<RemotePlayer> remotePlayers) {
        updateTime(time);
        heartScaling = (float) stage.getViewport().getScreenHeight() / 25;
        // update lives, health, damage
        if (localPlayer != null) {
            // update local player
            if (!localPlayerName.getText().toString().equals(localPlayer.getName())) {
                localPlayerName.setText(localPlayer.getName());
            }
            if (localLives != localPlayer.getLivesCount()) {
                localLives = localPlayer.getLivesCount();
                updateLivesTable(localLives, localHealthTable);
            }
            localDamage.setText(localPlayer.getDamage() + " %");
            localAmmoCount.setText(localPlayer.getAmmoCount());

            updateRemotePlayers(remotePlayers);
            // display game over screen
            if (localPlayer.getLivesCount() == 0) {
                gameOverLabel.setText("GAME OVER!\n You lost.");
                gameOverLabel.setColor(Color.RED);
            // check if all remote players are defeated  &&  the game has already started
            } else if (!remotePlayers.isEmpty() &&
                    remotePlayers.stream().allMatch(x -> Objects.equals(x.getLivesCount(), 0))) {
                gameOverLabel.setText("Congratulations you won!");
                gameOverLabel.setColor(Color.GREEN);
            } else {
                gameOverLabel.setText("");  // prevents  error caused by UDP losses
            }
        }
    }

    /**
     * Update remote players' data.
     * To add even more remote players (over 3), extend label lists.
     * @param remotePlayers remote players; amount ranging from 0 to (lobbyMaxSize - 1)
     */
    private void updateRemotePlayers(List<RemotePlayer> remotePlayers) {
        int remotePlayerCount = remotePlayers.size();
        for (int i = 0; i < remotePlayerCount; i++) {
            RemotePlayer rp = remotePlayers.get(i);
            if (rp.getLivesCount() != null) {
                if (!nameLabels.get(i).getText().toString().equals(rp.getName())) {
                    nameLabels.get(i).setText(rp.getName());
                    healthTables.get(i).setVisible(true);  // make additional remote players tables visible
                }
                if (livesDisplayed.get(i) != rp.getLivesCount()) {
                    livesDisplayed.add(i, rp.getLivesCount());
                    updateLivesTable(rp.getLivesCount(), healthTables.get(i));
                }
                damageLabels.get(i).setText(rp.getDamage() + " %");
            }
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
            table.add(new Image(BLACK_HEART_TEXTURE)).size(heartScaling);;
        }
        // remaining lives
        for(int i = 0; i < newLivesAmount; i++) {
            table.add(new Image(HEALTH_TEXTURE)).size(heartScaling);
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
