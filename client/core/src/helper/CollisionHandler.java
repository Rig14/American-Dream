package helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import helper.packet.GunPickupMessage;
import helper.packet.LobbyDataMessage;
import objects.gun.GunBox;
import objects.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CollisionHandler implements ContactFilter {
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        System.out.println(fixtureA.getUserData());
        System.out.println(fixtureB.getUserData());
        if (fixtureA.getUserData() instanceof Player && fixtureB.getUserData() instanceof GunBox) {
            // collision between player and gun box
            return false;
        } else if (fixtureA.getUserData() instanceof GunBox && fixtureB.getUserData() instanceof Player) {
            // collision between gun box and player
            return false;
        }
        return true;
    }
    public GunPickupMessage removeGunBoxTouchingPlayer(Array<Fixture> playerFixtureArray, List<GunBox> gunBoxes) {
        GunPickupMessage gunPickupMessage = new GunPickupMessage();
        gunPickupMessage.ids = new ArrayList<>();
        for (GunBox gunBox : gunBoxes) {
            if (gunBox.getPosition() == null) {
                gunBox.remove();
                gunPickupMessage.ids.add(gunBox.getId());
            } else if (playerFixtureArray.get(0).testPoint(gunBox.getPosition())) {
                gunBox.remove();
                gunPickupMessage.ids.add(gunBox.getId());
            }
        }
        return gunPickupMessage;
    }
    // to remove gunboxes other players have taken
    public void removeGunBoxTaken(List<GunBox> gunBoxes, String characterName) {
        AmericanDream.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof GunPickupMessage) {
                    if (!characterName.contains(((GunPickupMessage) object).character)) {
                        for (int i = 0; i < gunBoxes.size(); i++) {
                            for (Integer id : ((GunPickupMessage) object).ids)
                                if (id.equals(gunBoxes.get(i).getId())) {
                                    gunBoxes.get(i).remove();
                            }
                        }
                    }
                }
            }
        });
    }
}
