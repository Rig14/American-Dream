package helper;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.server.Game;
import ee.taltech.americandream.server.Player;
import helper.packet.*;

public class PlayerListener extends Listener {
    private final Player player;
    private final Game game;

    public PlayerListener(Player player, Game game) {
        this.player = player;
        this.game = game;
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
        player.onDisconnect();
    }

    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);
        // Handle incoming messages
        if (object instanceof PlayerPositionMessage positionMessage) {
            if (player.isThisIsAI() && positionMessage.name.equals("AI")) {
                player.handlePositionMessage(positionMessage);
            } else if (!player.isThisIsAI() && !positionMessage.name.equals("AI")) {
                player.handlePositionMessage(positionMessage);
            }
        } else if (object instanceof GameLeaveMessage && player.isThisIsAI()) {
            game.end();
        } else if (object instanceof BulletMessage bulletMessage) {
            if (player.isThisIsAI() && bulletMessage.name.equals("AI")) {
                player.handleNewBullet(bulletMessage);
            } else if (!player.isThisIsAI() && !bulletMessage.name.equals("AI")) {
                player.handleNewBullet(bulletMessage);
            }
        } else if (object instanceof AddUfoMessage addAIMessage) {
            game.addUFO();
        } else if (object instanceof GunPickupMessage gunPickupMessage) {
            if (player.getName().contains(gunPickupMessage.character)) {
                player.setAmmoIncrementingTime(0f);
                player.setGunPickedUp(true);
                if (player.getName().contains("Biden")) {
                    player.changeGun(5, 3500, 9, 1);
                } else if (player.getName().contains("Trump")) {
                    player.changeGun(20, 800, 6, 0.1f);
                } else if (player.getName().contains("Obama")) {
                    player.changeGun(25, 900, 6, 0.2f);
                }
                game.sendToAllExcept(player, gunPickupMessage);
            }
        }
    }
}
