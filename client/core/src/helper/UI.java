package helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static helper.Constants.FONT_SCALING_FACTOR;

public class UI {
    /**
     * Disable a button by setting it to be not clickable
     * Changes the button style to be disabled
     *
     * @param button The button to be disabled
     */
    public static void disableButton(TextButton button) {
        button.setDisabled(true);
        button.getStyle().fontColor = Color.GRAY;
        button.getStyle().overFontColor = Color.GRAY;
    }

    /**
     * Create a text button with the given text
     *
     * @param text The text to be displayed on the button
     * @return A new TextButton with the given text
     */
    public static TextButton createButton(String text) {
        return createButton(text, 1);
    }

    /**
     * @param text The text to be displayed on the button
     * @param size The size of the text. Higher values -> smaller text
     * @return A new TextButton with the given text and size
     */
    public static TextButton createButton(String text, float size) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        TextButton button = new TextButton(text, buttonStyle);
        button.padLeft(Gdx.graphics.getWidth() / 20f / size);
        button.padRight(Gdx.graphics.getWidth() / 20f / size);
        button.padTop(Gdx.graphics.getHeight() / 30f / size);
        button.padBottom(Gdx.graphics.getHeight() / 30f / size);
        button.getLabel().setFontScale(Gdx.graphics.getWidth() / (FONT_SCALING_FACTOR * size), Gdx.graphics.getHeight() / (FONT_SCALING_FACTOR * size));
        button.getStyle().up = new TextureRegionDrawable(new TextureRegion(new Texture("pixel.jpg")));
        button.getStyle().over = new TextureRegionDrawable(new TextureRegion(new Texture("pixel.jpg"))).tint(Color.BLACK);
        button.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Audio.getInstance().playSound(Audio.SoundType.BUTTON_CLICK);
            }
        });
        return button;
    }

    /**
     * Create a label with the given text
     *
     * @param text The text to be displayed on the label
     * @return A new Label with the given text
     */
    public static Label createLabel(String text) {
        return createLabel(text, Color.WHITE, 3);
    }

    /**
     * Create a label with the given text and color
     *
     * @param text      The text to be displayed on the label
     * @param textColor The color of the text
     * @param size      The size of the text
     * @return A new Label with the given text and color
     */
    public static Label createLabel(String text, Color textColor, float size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        BitmapFont font = generator.generateFont(parameter);
        Label label = new Label(text, new Label.LabelStyle(font, textColor));
        label.setFontScale(Gdx.graphics.getWidth() / (FONT_SCALING_FACTOR * size), Gdx.graphics.getHeight() / (FONT_SCALING_FACTOR * size));
        generator.dispose();
        return label;
    }


    public static Slider createSlider(float min, float max, float stepSize, boolean vertical) {
        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = new TextureRegionDrawable(new TextureRegion(new Texture("slider_background.png")));
        style.background.setMinHeight(Gdx.graphics.getHeight() / 25f);
        style.knob = new TextureRegionDrawable(new TextureRegion(new Texture("slider_knob.png")));
        style.knob.setMinHeight(Gdx.graphics.getHeight() / 20f);
        style.knob.setMinWidth(Gdx.graphics.getHeight() / 20f);
        return new Slider(min, max, stepSize, vertical, style);
    }

    public static Table createTextFieldWithButton(String placeholder, String buttonText) {
        Table table = new Table();
        table.setFillParent(true);

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        BitmapFont theFont = generator.generateFont(parameter);
        style.font = theFont;
        style.fontColor = Color.WHITE;
        style.cursor = new TextureRegionDrawable(new TextureRegion(new Texture("blinker.png")));
        style.background = new TextureRegionDrawable(new TextureRegion(new Texture("textfield.png")));
        style.selection = new TextureRegionDrawable(new TextureRegion(new Texture("selected.png")));
        TextField textField = new TextField(placeholder, style);
        textField.setAlignment(1);
        textField.setMaxLength(15);

        table.add(textField);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = theFont;
        buttonStyle.fontColor = Color.WHITE;
        TextButton button = new TextButton(buttonText, buttonStyle);
        button.getStyle().up = new TextureRegionDrawable(new TextureRegion(new Texture("special_button_ip.png")));
        button.getStyle().over = new TextureRegionDrawable(new TextureRegion(new Texture("special_button_ip.png"))).tint(Color.BLACK);
        button.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Audio.getInstance().playSound(Audio.SoundType.BUTTON_CLICK);
            }
        });
        table.add(button);

        generator.dispose();
        return table;
    }
}
