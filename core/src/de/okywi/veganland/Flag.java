package de.okywi.veganland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;

import java.io.IOException;

public class Flag {
    Rectangle rect = new Rectangle();
    Sprite sprite;
    Texture texture = new Texture(Gdx.files.internal("objects/flag.png"));

    BitmapFont font;
    FreeTypeFontGenerator fontGenerator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    GlyphLayout layout;
    float textWidth;
    float textHeight;
    String text = "Press [E]";
    int textOffset = 20;

    Flag(float x, float y, float width, float height) {
        rect.set(x, y, width, height);
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        sprite.setSize(width, height);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 3f;
        parameter.borderStraight = false;
        font = fontGenerator.generateFont(parameter);

        layout = new GlyphLayout(font, text);
        this.textWidth = layout.width;
        this.textHeight = layout.height;
    }

    public void updatePosition() {
        sprite.setPosition(rect.x, rect.y);
    }

    public void drawText(SpriteBatch batch) {
        if (VeganLand.player.playerRect.overlaps(rect)) {
            font.draw(batch, text, rect.x + (rect.width / 2) - textWidth / 2, rect.y - textOffset);
        }
    }

    public void playerCollision() {
        if (VeganLand.player.playerRect.overlaps(rect)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                try {
                    VeganLand.level += 1;
                    VeganLand.changeLevel();
                } catch (IOException e) {
                    VeganLand.gameState = "menu";
                }
            }
        }
    }
}
