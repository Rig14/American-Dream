package objects.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.americandream.AmericanDream;
import helper.PlayerState;
import helper.packet.GameStateMessage;
import helper.packet.TimeMessage;
import helper.Hud;

public class RemoteManager {
    private RemotePlayer[] remotePlayers;
    private Hud hud = new Hud();

    public RemoteManager() {
        AmericanDream.client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameStateMessage) {
                    GameStateMessage gameStateMessage = (GameStateMessage) object;
                    // handle game state message
                    remotePlayers = new RemotePlayer[gameStateMessage.playerStates.length];
                    for (PlayerState ps : gameStateMessage.playerStates) {
                        if (ps.id != AmericanDream.id) {
                            remotePlayers[ps.id - 1] = new RemotePlayer(ps.x, ps.y);
                        }
                    }
                } else if (object instanceof TimeMessage) {
                    TimeMessage timeMessage = (TimeMessage) object;
                    int time = timeMessage.seconds;
                    System.out.println(timeMessage.seconds);
                    System.out.println(time);
                    // handle time message
                    hud.update(time);
                }
            }
        });
    }

    public void renderPlayers(SpriteBatch batch, Vector2 playerDimensions) {
        if (remotePlayers != null) {
            for (RemotePlayer rp : remotePlayers) {
                if (rp != null) {
                    rp.render(batch, playerDimensions);
                }
            }
        }
    }
}
