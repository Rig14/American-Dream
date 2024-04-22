package helper.packet;

import java.util.Map;

public class LobbyDataMessage {
    // contains data about all the available lobbies

    // lobby id -> lobby name
    public Map<Integer, String> lobbies;
    // lobby id -> current map
    public Map<Integer, String> maps;
    // lobby id -> player count
    public Map<Integer, Integer> playerCount;
    // lobby id -> max players
    public Map<Integer, Integer> maxPlayers;
}
