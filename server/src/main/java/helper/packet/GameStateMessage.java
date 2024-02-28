package helper.packet;

import helper.BulletData;
import helper.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class GameStateMessage {
    public PlayerState[] playerStates;
    public List<BulletData> bulletDataList = new ArrayList<>();

    public int gameTime;


    }
