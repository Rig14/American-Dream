package helper;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.*;

/**
 * Audio class utilizing singleton design pattern.
 */
public class Audio {
    private static Audio instance = null;
    private final float MUSIC_VOLUME = 0.2f;
    private final Map<MusicType, List<Music>> music;

    private Audio() {
        this.music = new HashMap<>();
        // setting up different audio tracks

        // menu music
        Music menu = Gdx.audio.newMusic(Gdx.files.internal("audio/menu/menu.mp3"));
        menu.setVolume(MUSIC_VOLUME);
        music.put(MusicType.MENU, Collections.singletonList(menu));

        // city music
        for (int i = 1; i < 4; i++) {
            Music city = Gdx.audio.newMusic(Gdx.files.internal("audio/game/city/" + i + ".mp3"));
            city.setVolume(MUSIC_VOLUME);
            if (music.containsKey(MusicType.CITY)) {
                music.get(MusicType.CITY).add(city);
            } else {
                List<Music> l = new ArrayList<>();
                l.add(city);
                music.put(MusicType.CITY, l);
            }
        }

        // desert music
        for (int i = 1; i < 4; i++) {
            Music desert = Gdx.audio.newMusic(Gdx.files.internal("audio/game/desert/" + i + ".mp3"));
            desert.setVolume(MUSIC_VOLUME);
            if (music.containsKey(MusicType.DESERT)) {
                music.get(MusicType.DESERT).add(desert);
            } else {
                List<Music> l = new ArrayList<>();
                l.add(desert);
                music.put(MusicType.DESERT, l);
            }
        }

        // swamp music
        for (int i = 1; i < 3; i++) {
            Music swamp = Gdx.audio.newMusic(Gdx.files.internal("audio/game/swamp/" + i + ".mp3"));
            swamp.setVolume(MUSIC_VOLUME);
            if (music.containsKey(MusicType.SWAMP)) {
                music.get(MusicType.SWAMP).add(swamp);
            } else {
                List<Music> l = new ArrayList<>();
                l.add(swamp);
                music.put(MusicType.SWAMP, l);
            }
        }
    }

    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

    public void playMusic(MusicType type) {
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

    public enum MusicType {
        MENU,
        CITY,
        DESERT,
        SWAMP
    }
}
