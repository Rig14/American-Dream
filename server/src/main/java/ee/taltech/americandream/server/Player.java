package ee.taltech.americandream.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import helper.BulletData;
import helper.Direction;
import helper.PlayerState;
import helper.packet.BulletMessage;
import helper.packet.GameStateMessage;
import helper.packet.PlayerPositionMessage;

import java.util.ArrayList;
import java.util.List;

import static helper.Constants.*;

public class Player {
    private final int id;
    private List<BulletData> playerBullets;
    private float x;
    private float y;
    private Direction direction;

    private Integer livesCount;

    private final Game game;
    private Direction nextBulletDirection;
    private float bulletTimeout;
    private final Connection connection;
    public float velX, velY;
    public Player(Connection connection, Game game, int id) {
        // create player
        this.id = id;
        this.game = game;
        this.connection = connection;
        this.playerBullets = new ArrayList<>();
        this.bulletTimeout = 0;
        // send id to client
        connection.sendTCP("id:" + id);
        // add listeners
        connection.addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                onDisconnect();
            }
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                // handle incoming data
                if (object instanceof PlayerPositionMessage positionMessage) {
                    // handle position message
                    handlePositionMessage(positionMessage);
                }

                // handle bullet position message
                if (object instanceof BulletMessage bulletMessage) {
                    // handle bullet position message
                    handleBullets(bulletMessage);
                }
            }
        });
    }

    private void handleBullets(BulletMessage bulletMessage) {
        // handle bullet message
        nextBulletDirection = bulletMessage.direction;
    }

    public void update(float delta) {
        // update player
        // will shoot a bullet if the bulletTimeout is 0
        if (nextBulletDirection != null && bulletTimeout >= SHOOT_DELAY) {
            // construct the bullet to be shot
            BulletData bulletData = new BulletData();
            bulletData.x = x + (nextBulletDirection == Direction.LEFT ? -1 : 1) * 20;
            bulletData.y = y;
            bulletData.speedBullet = PISTOL_BULLET_SPEED * (nextBulletDirection == Direction.LEFT ? -1 : 1);
            playerBullets.add(bulletData);

            // reset timer and bullet shooting direction
            bulletTimeout = 0;
            nextBulletDirection = null;
        }
        bulletTimeout += delta;

        // remove bullets that are out of bounds
        playerBullets.removeIf(bullet -> bullet.x < x - BOUNDS || bullet.x > x + BOUNDS);
        // move bullets
        for (BulletData bullet : playerBullets) {
            bullet.x += bullet.speedBullet;
        }
    }

    private void onDisconnect() {
        // handle disconnect
        // end game
        game.end();
    }

    private void handlePositionMessage(PlayerPositionMessage positionMessage) {
        // handle position message
        x = positionMessage.x;
        y = positionMessage.y;
        direction = positionMessage.direction;
        livesCount = positionMessage.livesCount;
        velX = positionMessage.velX;
        velY = positionMessage.velY;
    }

    public PlayerState getState() {
        // get player state
        PlayerState state = new PlayerState();
        state.id = id;
        state.x = x;
        state.y = y;
        state.direction = direction;
        state.livesCount = livesCount;
        state.velX = velX;
        state.velY = velY;

        return state;
    }

    public List<BulletData> getPlayerBullets() {
        return playerBullets;
    }

    public void sendGameState(GameStateMessage gameStateMessage) {
        // send game state message
        connection.sendUDP(gameStateMessage);
    }
}
