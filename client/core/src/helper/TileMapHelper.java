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
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import ee.taltech.americandream.GameScreen;
import helper.packet.GunBoxMessage;
import objects.gun.GunBox;
import objects.player.AIPlayer;
import objects.player.Player;

import java.util.Objects;

import static helper.Constants.PPM;
import static java.lang.System.currentTimeMillis;

public class TileMapHelper {
    private TiledMap tiledMap;
    private GameScreen gameScreen;
    private String selectedCharacter;
    // gunbox timers for the gunbox message to work correctly
    private float lastGunBoxSpawn = 300;
    private float gunBoxSpawnDelay = 1;

    /**
     * Initialize TileMapHelper which loads the tilemap background, tile outlines and objects.
     */
    public TileMapHelper(GameScreen gameScreen, String selectedCharacter) {
        this.gameScreen = gameScreen;
        this.selectedCharacter = selectedCharacter;
    }

    /**
     * Load tilemap from a .tmx file.
     */
    public OrthogonalTiledMapRenderer setupMap(String fileName, boolean AIGame) {
        // load map
        tiledMap = new TmxMapLoader().load(fileName);
        parseMapObjects(tiledMap.getLayers().get("objects").getObjects(), AIGame);
        return new OrthogonalTiledMapRenderer(tiledMap);
    }
    public void update(Integer gameTime) {
        spawnGunBox(tiledMap.getLayers().get("objects").getObjects(), gameTime);
    }
    /**
     * Load tilemap objects such as the player itself to enable collisions.
     */
    private void parseMapObjects(MapObjects mapObjects, boolean AIGame) {
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
                            gameScreen.getWorld(),
                            new Player(rectangle.getWidth(), rectangle.getHeight(),
                                    gameScreen.getWorld().createBody(new BodyDef()), selectedCharacter)
                    );
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body, selectedCharacter));
                    if (AIGame) {
                        Body AIBody = BodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth() / 2,
                                rectangle.getY() + rectangle.getHeight() / 2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false,
                                gameScreen.getWorld()
                        );
                        gameScreen.setAIPlayer(new AIPlayer(rectangle.getWidth(), rectangle.getHeight(), AIBody, "AI"));
                    }
                }
            }
            if (mapObject.getName().equals("Center")) {
                // get the point
                Rectangle point = ((RectangleMapObject) mapObject).getRectangle();
                gameScreen.setMapCenterPoint(new Vector2(point.x, point.y));
            }
        }
    }
    public void spawnGunBox(MapObjects mapObjects, Integer gameTime) {
        AmericanDream.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof GunBoxMessage && (lastGunBoxSpawn - gameTime) > gunBoxSpawnDelay) {
                    lastGunBoxSpawn = gameTime;
                    System.out.println("received gunbox message");
                    for (MapObject mapObject : mapObjects) {
                        if (mapObject instanceof RectangleMapObject) {
                            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                            String rectangleName = mapObject.getName();
                            if (rectangleName.contains("gunbox")) {
                                Body body = BodyHelperService.createBody(
                                        ((GunBoxMessage) object).x,
                                        ((GunBoxMessage) object).y,
                                        rectangle.getWidth(),
                                        rectangle.getHeight(),
                                        false,
                                        gameScreen.getWorld(),
                                        new GunBox(gameScreen.getWorld().createBody(new BodyDef()),
                                                ((GunBoxMessage) object).id)
                                );
                                gameScreen.addGunBox(new GunBox(body, ((GunBoxMessage) object).id));
                            }
                        }
                    }
                }
            }
        });
    }
    /**
     * Create static objects such as platforms.
     */
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

    /**
     * Load the shape of an object, for example a rectangular platform.
     */
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
