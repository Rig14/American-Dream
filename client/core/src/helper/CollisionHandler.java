package helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import helper.packet.GunPickupMessage;
import objects.gun.GunBox;
import objects.player.Player;

import java.util.List;


public class CollisionHandler implements ContactFilter {
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        System.out.println(fixtureA.getUserData());
        System.out.println(fixtureB.getUserData());
        if (fixtureA.getUserData() instanceof Player && fixtureB.getUserData() instanceof GunBox) {
            // collision between player and gun box
            System.out.println("gunbox/player collision");
            return false;

        } else if (fixtureA.getUserData() instanceof GunBox && fixtureB.getUserData() instanceof Player) {
            // collision between gun box and player
            System.out.println("gunbox/player collision");
            return false;
        }
        return true;
    }
    public GunPickupMessage removeGunBoxTouchingPlayer(Array<Fixture> playerFixtureArray, List<GunBox> gunBoxes) {
        GunPickupMessage gunPickupMessage = new GunPickupMessage();
        for (GunBox gunBox : gunBoxes) {
            if (playerFixtureArray.get(0).testPoint(gunBox.getPosition())) {
                gunBox.remove();
                gunPickupMessage.id = gunBox.getId();
            }
        }
        return gunPickupMessage;
    }




}
