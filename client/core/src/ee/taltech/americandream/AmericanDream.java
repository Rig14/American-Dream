package ee.taltech.americandream;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class AmericanDream extends Game {
    public static AmericanDream INSTANCE;
    private OrthographicCamera camera;
    private int screenWidth, screenHeight;

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

        // navigating to the starting screen
        setScreen(new GameScreen(this.camera));
    }
}
