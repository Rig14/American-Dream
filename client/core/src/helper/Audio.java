package helper;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.HashMap;
import java.util.Map;

/**
 * Audio class utilizing singleton design pattern.
 */
public class Audio {
    private static Audio instance = null;
    private final Map<MusicType, Music> music;

    private Audio() {
        this.music = new HashMap<>();

        Music menu = Gdx.audio.newMusic(Gdx.files.internal("audio/menu/menu.mp3"));
        menu.setLooping(true);
        music.put(MusicType.MENU, menu);
    }

    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

    public void playMusic(MusicType type) {
        music.get(type).play();
    }

    public void dispose() {
        for (Music m : music.values()) {
            m.dispose();
        }
    }

    public enum MusicType {
        MENU,
        GAME
    }
}
