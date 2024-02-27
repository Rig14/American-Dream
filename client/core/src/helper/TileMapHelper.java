package helper;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import ee.taltech.americandream.Bullet;
import ee.taltech.americandream.GameScreen;
import objects.player.Player;

import java.util.Objects;

import static helper.Constants.PPM;

public class TileMapHelper {
    private TiledMap tiledMap;
    private GameScreen gameScreen;

    public TileMapHelper(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer setupMap(String fileName) {
        // load map
        tiledMap = new TmxMapLoader().load(fileName);
        parseMapObjects(tiledMap.getLayers().get("objects").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void parseMapObjects(MapObjects mapObjects) {
        // parsing map objects
        for (MapObject mapObject : mapObjects) {
            // these are platforms
            if (mapObject instanceof PolygonMapObject) {
                createStaticBody((PolygonMapObject) mapObject);
            }
            // this is for the player
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName = mapObject.getName();

                if (rectangleName.contains("player")) {
                    // creating player
                    Body body = BodyHelperService.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            gameScreen.getWorld()
                    );
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body));
                } else if (rectangleName.contains("bulletSpawn")) {
                    // Create a new bullet at the spawn point
                    Bullet bullet = new Bullet(rectangle.getX(), rectangle.getY(), false); // Modify as per your Bullet constructor
                    gameScreen.addBullet(bullet); // Add the bullet to the game screen
                }
            }
            if (mapObject.getName().equals("Center")) {
                // get the point
                Rectangle point = ((RectangleMapObject) mapObject).getRectangle();
                gameScreen.setCenter(new Vector2(point.x, point.y));
            }
        }
    }

    private void createStaticBody(PolygonMapObject polygonMapObject) {
        // creating static body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        // only run if it's a platform
        if (Objects.equals(polygonMapObject.getName(), "platform")) {
            // to get the platforms y coordinate for one-way platforms
            body.setUserData("platform:" + polygonMapObject.getPolygon().getY());
        }
        Shape shape = createPolygonShape(polygonMapObject);
        body.createFixture(shape, 1000);

        // shape is no more needed
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
        // points of the polygon
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        // contains 2 point pairs
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            worldVertices[i] = current;
        }

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(worldVertices);
        return polygonShape;
    }
}
