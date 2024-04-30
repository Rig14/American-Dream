package helper;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.*;

/**
 * Audio class utilizing singleton design pattern.
 */
public class Audio {
    private static Audio instance = null;
    private final float MUSIC_VOLUME = 0.1f;
    private final float SOUND_VOLUME = 0.5f;
    private final Map<MusicType, List<Music>> music;
    private final Map<SoundType, Sound> sound;

    private Audio() {
        this.music = new HashMap<>();
        this.sound = new HashMap<>();
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

        // sound efx
        Sound buttonClick = Gdx.audio.newSound(Gdx.files.internal("audio/menu/button_click.mp3"));
        sound.put(SoundType.BUTTON_CLICK, buttonClick);
        Sound shoot = Gdx.audio.newSound(Gdx.files.internal("audio/game/sound/shoot.wav"));
        sound.put(SoundType.GUNSHOT, shoot);
        Sound hit = Gdx.audio.newSound(Gdx.files.internal("audio/game/sound/hit.mp3"));
        sound.put(SoundType.HIT, hit);
        Sound start = Gdx.audio.newSound(Gdx.files.internal("audio/game/sound/start.ogg"));
        sound.put(SoundType.START, start);
        Sound lost = Gdx.audio.newSound(Gdx.files.internal("audio/game/sound/you_lose.ogg"));
        sound.put(SoundType.YOU_LOSE, lost);
        Sound win = Gdx.audio.newSound(Gdx.files.internal("audio/game/sound/you_win.ogg"));
        sound.put(SoundType.YOU_WIN, win);
        Sound jump = Gdx.audio.newSound(Gdx.files.internal("audio/game/sound/jump.mp3"));
        sound.put(SoundType.JUMP, jump);
        Sound death = Gdx.audio.newSound(Gdx.files.internal("audio/game/sound/death.wav"));
        sound.put(SoundType.DEATH, death);
        Sound chooseYourCharacter = Gdx.audio.newSound(Gdx.files.internal("audio/menu/choose_your_character.ogg"));
        sound.put(SoundType.CHOOSE_YOUR_CHARACTER, chooseYourCharacter);


        // walking sound effect as music
        for (int i = 0; i < 8; i++) {
            Music walk = Gdx.audio.newMusic(Gdx.files.internal("audio/game/sound/walk/Footstep_Dirt_0" + i + ".mp3"));
            walk.setVolume(SOUND_VOLUME);
            if (music.containsKey(MusicType.WALK)) {
                music.get(MusicType.WALK).add(walk);
            } else {
                List<Music> l = new ArrayList<>();
                l.add(walk);
                music.put(MusicType.WALK, l);
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

    public void playSound(SoundType type) {
        // play sound
        sound.get(type).play();
    }

    public void startWalkSound() {
        List<Music> musicList = music.get(MusicType.WALK);
        Collections.shuffle(musicList);
        musicList.get(0).play();
        musicList.get(0).setOnCompletionListener(music -> startWalkSound());
    }

    public void stopWalkSound() {
        music.get(MusicType.WALK).forEach(Music::stop);
    }

    public void stopAllMusic() {
        music.forEach((key, value) -> value.forEach(Music::stop));
    }

    public void dispose() {
        music.forEach((key, value) -> value.forEach(Music::dispose));
        sound.forEach((key, value) -> value.dispose());
        instance = null;
    }

    public enum MusicType {
        MENU,
        CITY,
        DESERT,
        SWAMP,
        WALK
    }

    public enum SoundType {
        BUTTON_CLICK,
        GUNSHOT,
        HIT,
        START,
        YOU_LOSE,
        YOU_WIN,
        JUMP,
        DEATH,
        CHOOSE_YOUR_CHARACTER
    }
}
