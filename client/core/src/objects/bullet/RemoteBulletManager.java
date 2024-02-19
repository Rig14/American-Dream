package objects.bullet;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import helper.BulletData;
import helper.packet.GameStateMessage;

import java.util.ArrayList;
import java.util.List;

public class RemoteBulletManager {
    private List<RemoteBullet> remoteBullets;


    public RemoteBulletManager() {
        this.remoteBullets = new ArrayList<>();
        AmericanDream.client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameStateMessage) {
                    GameStateMessage gameStateMessage = (GameStateMessage) object;
                    // handle game state message
                    for (BulletData bd : gameStateMessage.getBulletList()) {
                        if (bd.getId() != AmericanDream.id) {
                            RemoteBullet remoteBullet = new RemoteBullet(bd.getX(), bd.getY());
                            remoteBullets.add(remoteBullet);
                        }
                    }
                }
            }
        });
    }

    public void renderBullets(SpriteBatch batch, Vector2 bulletDimensions) {
        if (remoteBullets != null) {
            for (RemoteBullet rb : remoteBullets) {
                if (rb != null) {
                    rb.render(batch, bulletDimensions);
                }
            }
        }
    }
}
