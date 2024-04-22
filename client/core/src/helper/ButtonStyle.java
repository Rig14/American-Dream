package helper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class ButtonStyle {
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
}
