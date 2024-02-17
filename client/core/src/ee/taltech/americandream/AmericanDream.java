package ee.taltech.americandream;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

import static helper.Constants.CAMERA_ZOOM;

public class AmericanDream extends Game {
    public static AmericanDream INSTANCE;
    private OrthographicCamera camera;
    public int screenWidth, screenHeight;

    public AmericanDream() {
        INSTANCE = this;
    }

    @Override
    public void create() {
        // getting screen size
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();

        // setting up camera
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, screenWidth, screenHeight);
        this.camera.zoom = CAMERA_ZOOM;
        // navigating to the starting screen
        setScreen(new GameScreen(this.camera));
    }
}
