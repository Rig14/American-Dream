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

        // Add a listener to the client to receive bullet data from the server
        AmericanDream.client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameStateMessage) {
                    GameStateMessage gameStateMessage = (GameStateMessage) object;
                    // Clear the existing list of remote bullets before updating with new data
                    System.out.println("received gamestate");
                    // Retrieve bullet data from the game state message and add to the list
                    List<BulletData> bulletDataList = gameStateMessage.getBulletDataList();
                    if (bulletDataList != null) {
                        for (BulletData bd : bulletDataList) {

                            RemoteBullet remoteBullet = new RemoteBullet(bd.getX(), bd.getY(), bd.getSpeedBullet());
                            remoteBullets.add(remoteBullet);
                            System.out.println("added remote bullet to list");

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
