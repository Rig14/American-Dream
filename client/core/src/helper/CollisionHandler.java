package helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
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
    public boolean isPlayerTouchingGunBox(Fixture playerFixture, List<GunBox> gunBoxes) {
        for (GunBox gunBox : gunBoxes) {
            if (playerFixture.testPoint(gunBox.getPosition())) {
                return true;
            }
        }
        return false;
    }




}
