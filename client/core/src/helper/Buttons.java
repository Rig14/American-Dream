package helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import static helper.Constants.FONT_SCALING_FACTOR;

public class Buttons {
    /**
     * Disable a button by setting it to be not clickable
     * Changes the button style to be disabled
     *
     * @param button The button to be disabled
     */
    public static void disableButton(TextButton button) {
        button.setDisabled(true);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = new BitmapFont();
        textButtonStyle.fontColor = Color.GRAY;
        button.setStyle(textButtonStyle);
    }

    public static TextButton createButton(String text) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;
        TextButton button = new TextButton(text, buttonStyle);
        button.getLabel().setFontScale(Gdx.graphics.getWidth() / FONT_SCALING_FACTOR, Gdx.graphics.getHeight() / FONT_SCALING_FACTOR);
        return button;
    }
}
