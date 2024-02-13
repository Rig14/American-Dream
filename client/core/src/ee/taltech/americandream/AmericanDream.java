package ee.taltech.americandream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.awt.*;
import java.io.IOException;

public class AmericanDream extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    private Client client;
    private Point player = new Point(0, 0);

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
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                if (!(object instanceof String)) return;
                String response = (String) object;
                Gdx.app.log("Received", response);
                player = new Point(Integer.parseInt(response.split(",")[0]), Integer.parseInt(response.split(",")[1]));

            }
        });
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(img, player.x, player.y);
        batch.end();

        // left arrow key
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            client.sendTCP("Left");
        }
        // right arrow key
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            client.sendTCP("Right");
        }
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
