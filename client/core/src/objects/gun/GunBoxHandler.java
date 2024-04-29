package objects.gun;

import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import helper.packet.GunBoxMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GunBoxHandler {
    private List<GunBox> gunBoxList;
    private float lastGunBoxSpawn = 0;
    private float gunBoxSpawnDelay = 3000;
    private Body gunBoxBody;

    public GunBoxHandler(Body gunBoxBody) {
        gunBoxList = new ArrayList<>();
    }

    public void spawnGunBox() {
        AmericanDream.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof GunBoxMessage && System.currentTimeMillis() - lastGunBoxSpawn > gunBoxSpawnDelay) {
                    lastGunBoxSpawn = System.currentTimeMillis();
                    System.out.println("received gunbox message");
                    GunBox gunBox = new GunBox(gunBoxBody);
                    gunBox.setId(((GunBoxMessage) object).id);
                    gunBoxList.add(gunBox);
                }
            }
        });
    }

    public List<GunBox> getGunBoxList() {
        return gunBoxList;
    }
}
