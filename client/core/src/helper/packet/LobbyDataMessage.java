package helper.packet;

import java.util.Map;

public class LobbyDataMessage {
    // contains data about all the available lobbies

    // lobby id -> lobby status text example: "Generic Lobby 2 0/2"
    public Map<Integer, String> lobbies;
}
