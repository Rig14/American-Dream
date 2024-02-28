package helper.packet;

import helper.BulletData;
import helper.PlayerState;

import java.util.Collections;
import java.util.List;

public class GameStateMessage {
    public PlayerState[] playerStates;
    public int gameTime;
    public List<BulletData> bulletDataList;

    public List<BulletData> getBulletDataList() {
        if (bulletDataList != null) {
            return bulletDataList;
        } else {
            return Collections.emptyList();
        }

    }
}
