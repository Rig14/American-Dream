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
    private final Map<AudioType, Music> music;

    private Audio() {
        this.music = new HashMap<>();

        Music menu = Gdx.audio.newMusic(Gdx.files.internal("audio/menu/menu.mp3"));
        menu.setLooping(true);
        music.put(AudioType.MENU, menu);
    }

    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

    public void playAudio(AudioType type) {
        music.get(type).play();
    }

    public void stopAudio(AudioType type) {
        music.get(type).stop();
    }

    public void dispose() {
        for (Music m : music.values()) {
            m.dispose();
        }
    }

    public enum AudioType {
        MENU,
        GAME
    }
}
