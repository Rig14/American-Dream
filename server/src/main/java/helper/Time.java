package helper;

import helper.Constants;

import static helper.Constants.GAME_DURATION;

public class Time {

    public int seconds = GAME_DURATION;
    private float delta = 0;
    public boolean update(float deltaTime) {
        delta = delta + deltaTime;
        if (delta >= 1 && seconds >= 1) {
            delta--;
            seconds = seconds - 1;
            return true;
        }
        return false;
    }

    public int getRemainingTime() {
        return this.seconds;
    }

}
