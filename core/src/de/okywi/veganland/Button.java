package de.okywi.veganland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import java.awt.*;

public class Button {
    Texture texture;
    Texture hoveringTexture;
    Sprite sprite;
    Rectangle rect;

    public boolean isClicked = false;
    boolean isHovering = false;
    boolean isClickable;
    String text;

    BitmapFont font;
    FreeTypeFontGenerator fontGenerator;
    FreeTypeFontParameter parameter;
    GlyphLayout layout;
    float textWidth;
    float textHeight;

    Button(float x, float y, String text, float sizeMultiplier, boolean isClickable) {
        this.isClickable = isClickable;

        texture = new Texture(Gdx.files.internal("menus/button.png"));
        hoveringTexture = new Texture(Gdx.files.internal("menus/hovering_button.png"));
        rect = new Rectangle();
        rect.setRect(x - (texture.getWidth() * sizeMultiplier / 2), y - (texture.getHeight() * sizeMultiplier / 2), texture.getWidth() * sizeMultiplier, texture.getHeight() * sizeMultiplier);
        sprite = new Sprite(texture);
        sprite.setPosition(rect.x, VeganLand.GAME_SIZE[1] - rect.y - rect.height);
        sprite.setSize(rect.width, rect.height);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));
        parameter = new FreeTypeFontParameter();
        parameter.size = (int) (40 * sizeMultiplier);
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 3f;
        parameter.borderStraight = false;
        font = fontGenerator.generateFont(parameter);

        this.text = text;
        layout = new GlyphLayout(font, text);
        this.textWidth = layout.width;
        this.textHeight = layout.height;
    }

    public void readjustText() {
        layout.setText(font, text);
        this.textWidth = layout.width;
        this.textHeight = layout.height;
    }

    public void handleInput() {
        if (!isClickable) return;
        isClicked = false;
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        if (Gdx.input.isButtonJustPressed(0)) {
            if (mouseX > rect.x && mouseX < rect.x + rect.width && mouseY > rect.y && mouseY < rect.y + rect.height) {
                isClicked = true;
            }
        }
        if (mouseX > rect.x && mouseX < rect.x + rect.width && mouseY > rect.y && mouseY < rect.y + rect.height) {
            isHovering = true;
        } else {
            isHovering = false;
        }
    }

    public void hovering() {
        if (isHovering) {
            sprite.setTexture(hoveringTexture);
        } else {
            sprite.setTexture(texture);
        }
    }

    public void renderText(SpriteBatch batch) {
        font.draw(batch, text, rect.x + rect.width / 2 - textWidth / 2, sprite.getY() + rect.height / 2 + font.getXHeight() / 2);
    }

    public void run(SpriteBatch batch) {
        hovering();
        sprite.draw(batch);
        renderText(batch);
        handleInput();
    }
}
