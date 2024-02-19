package helper.packet;

import helper.BulletData;
import helper.PlayerState;

import java.util.List;

public class GameStateMessage {
    public PlayerState[] playerStates;
    public List<BulletData> bulletList;

    public List<BulletData> getBulletList() {
        return bulletList;
    }
}
