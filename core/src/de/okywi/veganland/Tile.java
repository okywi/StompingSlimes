package de.okywi.veganland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Tile {
    public Sprite sprite;
    Texture texture;
    public int type;
    public Rectangle tileRect = new Rectangle();

    Tile(int x, int y, int width, int height, int type) {
        this.type = type;
        tileRect.set(x, y, width, height);
        getTexture();
        sprite = new Sprite(texture);
        sprite.setBounds(x, y, width, height);
    }

    public void getTexture() {
        String[] textures = new String[] {"barrier", "grass", "dirt", "grass_top_right", "grass_top_left", "grass_bottom_left", "grass_bottom_right", "grass_middle_left",
                "grass_middle_center", "grass_middle_right", "grass_corner_top_left", "grass_corner_top_right", "grass_right", "grass_left", "grass_bottom_middle"};

        texture = new Texture(Gdx.files.internal("ground_tiles/" + textures[type] + ".png"));
    }

    public void updatePosition() {
        sprite.setPosition(tileRect.x, tileRect.y);
    }
}
