package ee.taltech.americandream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import helper.TileMapHelper;
import indicators.OffScreenIndicator;
import indicators.hud.Hud;
import objects.RemoteManager;
import objects.player.Player;

import static helper.Constants.*;
import static helper.Textures.TRUMP_TEXTURE;

public class GameScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    private TileMapHelper tileMapHelper;

    // ###################
    // game objects
    // client player
    private Player player;
    // remote players
    private RemoteManager remoteManager;
    // ###################

    // center point of the map
    private Vector2 center;
    // game screen overlay
    private Hud hud;
    private OffScreenIndicator offScreenIndicator;

    public GameScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        // creating a new world, vector contains the gravity constants
        // x - horizontal gravity, y - vertical gravity
        this.world = new World(new Vector2(0, GRAVITY), false);
        this.debugRenderer = new Box2DDebugRenderer();

        // setting up the map
        this.tileMapHelper = new TileMapHelper(this);
        this.orthogonalTiledMapRenderer = tileMapHelper.setupMap("Desert.tmx");

        // remote player manager
        this.remoteManager = new RemoteManager();

        // visual info for player
        this.hud = new Hud(this.batch);
        this.offScreenIndicator = new OffScreenIndicator(player.getDimensions());
    }

    @Override
    public void render(float delta) {
        this.update(delta);

        // clear the screen (black screen)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        // render map before the actual game objects
        orthogonalTiledMapRenderer.render();

        batch.begin();
        // object rendering goes here

        // temporary local player rendering for demo, rendering inside player class causes override and abstract class errors
        batch.draw(TRUMP_TEXTURE, player.getPosition().x - player.getDimensions().x / 2,
                player.getPosition().y - player.getDimensions().y / 2, player.getDimensions().x, player.getDimensions().y);

        remoteManager.renderPlayers(batch, player.getDimensions());
        remoteManager.renderBullets(batch);
        offScreenIndicator.renderIndicators(batch, camera, remoteManager.getAllPlayerStates());

        batch.end();

        // for debugging
        debugRenderer.render(world, camera.combined.scl(PPM));

        // create hud and add it to the GameScreen
        this.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    private void update(float delta) {
        // updates objects in the world
        world.step(1 / FPS, 6, 2);

        // update the camera position
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        // set the view of the map to the camera
        orthogonalTiledMapRenderer.setView(camera);
        player.update(delta, center);

        remoteManager.testForHit(player);

        // if escape is pressed, the game will close
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            AmericanDream.instance.setScreen(new TitleScreen(camera));
        }

        // update hud, currently used for timer
        hud.update(remoteManager.getGameTime(), player.getLives(), remoteManager.getRemoteLives());
    }

    /**
     * Updates the camera position relative to players and center point
     * <p>
     * new camera position will be the center point of the triangle
     * created by players and the center point
     */
    private void cameraUpdate() {
        // if player is out of bound then set the camera to the center
        if (
                player.getPosition().y > center.y + BOUNDS
                        || player.getPosition().y < center.y - BOUNDS
                        || player.getPosition().x > center.x + BOUNDS
                        || player.getPosition().x < center.x - BOUNDS
        ) {
            // "lerp" makes the camera move smoothly back to the center point.
            camera.position.lerp(new Vector3(center.x, center.y, 0), 0.1f);
            camera.update();
            return;
        }
        // make camera follow the player slowly
        // vector from center to player
        Vector2 vector = new Vector2(player.getPosition().x - center.x, player.getPosition().y - center.y);
        camera.position.x = center.x + vector.x / CAMERA_SPEED;
        camera.position.y = center.y + vector.y / CAMERA_SPEED;
        // update the camera
        camera.update();
    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCenter(Vector2 vector2) {
        this.center = vector2;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.setToOrtho(false, width, height);
    }
}
