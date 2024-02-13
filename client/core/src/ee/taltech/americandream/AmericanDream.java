package ee.taltech.americandream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class AmericanDream extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    private Client client;

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        client = new Client();
        client.start();
        try {
            client.connect(5000, "localhost", 8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.sendTCP("Start");
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
        client.sendTCP("test");
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
