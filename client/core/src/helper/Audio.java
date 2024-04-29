package helper;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.*;

/**
 * Audio class utilizing singleton design pattern.
 */
public class Audio {
    private static Audio instance = null;
    private final Map<AudioType, List<Music>> music;

    private Audio() {
        this.music = new HashMap<>();
        // setting up different audio tracks

        // menu music
        Music menu = Gdx.audio.newMusic(Gdx.files.internal("audio/menu/menu.mp3"));
        menu.setVolume(0.5f);
        music.put(AudioType.MENU, Collections.singletonList(menu));

        // city music
        for (int i = 1; i < 4; i++) {
            Music city = Gdx.audio.newMusic(Gdx.files.internal("audio/game/city/" + i + ".mp3"));
            city.setVolume(0.5f);
            if (music.containsKey(AudioType.CITY)) {
                music.get(AudioType.CITY).add(city);
            } else {
                List<Music> l = new ArrayList<>();
                l.add(city);
                music.put(AudioType.CITY, l);
            }
        }

        // desert music
        for (int i = 1; i < 4; i++) {
            Music desert = Gdx.audio.newMusic(Gdx.files.internal("audio/game/desert/" + i + ".mp3"));
            desert.setVolume(0.5f);
            if (music.containsKey(AudioType.DESERT)) {
                music.get(AudioType.DESERT).add(desert);
            } else {
                List<Music> l = new ArrayList<>();
                l.add(desert);
                music.put(AudioType.DESERT, l);
            }
        }

        // swamp music
        for (int i = 1; i < 3; i++) {
            Music swamp = Gdx.audio.newMusic(Gdx.files.internal("audio/game/swamp/" + i + ".mp3"));
            swamp.setVolume(0.5f);
            if (music.containsKey(AudioType.SWAMP)) {
                music.get(AudioType.SWAMP).add(swamp);
            } else {
                List<Music> l = new ArrayList<>();
                l.add(swamp);
                music.put(AudioType.SWAMP, l);
            }
        }
    }

    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

    public void playMusic(AudioType type) {
        stopAllMusic();
        List<Music> musicList = music.get(type);
        Collections.shuffle(musicList);
        musicList.get(0).play();
        musicList.get(0).setOnCompletionListener(music -> playMusic(type));
    }

    public void stopAllMusic() {
        music.forEach((key, value) -> value.forEach(Music::stop));
    }

    public void dispose() {
        music.forEach((key, value) -> value.forEach(Music::dispose));
        instance = null;
    }

    public enum AudioType {
        MENU,
        CITY,
        DESERT,
        SWAMP
    }
}
