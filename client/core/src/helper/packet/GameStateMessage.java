package helper.packet;

import helper.BulletData;
import helper.PlayerState;

import java.util.List;

public class GameStateMessage {
    public PlayerState[] playerStates;
    public List<BulletData> bulletData;
    public int gameTime;
    public float AIplayerX;
    public float AIplayerY;
}
