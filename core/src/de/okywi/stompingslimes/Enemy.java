package de.okywi.stompingslimes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Enemy {

    int type;
    Sprite sprite;

    Rectangle rect;
    Rectangle leftBottomRect;
    Rectangle rightBottomRect;

    Texture texture;

    float speed;
    public Vector2 movement = new Vector2(0, 0);
    int direction = 1;

    Enemy(float x, float y, float width, float height, float speed, int type) {
        this.type = type;
        this.speed = speed;
        getTexture();
        sprite = new Sprite(texture);
        rect = new Rectangle(x, y, width, height);
        leftBottomRect = new Rectangle(x, y, width, height);
        rightBottomRect = new Rectangle(x, y, width, height);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
    }

    public void getTexture() {
        String[] types = new String[] {"slime"};

        texture = new Texture(Gdx.files.internal("enemies/" + types[type] + ".png"));
    }

    public ArrayList<Rectangle> getCollidingTiles(Rectangle rect) {
        ArrayList<Rectangle> hitTiles = new ArrayList<>();

        for (Tile tile : Map.mapTiles) {
            if (rect.overlaps(tile.tileRect)) {
                hitTiles.add(tile.tileRect);
            }
        }

        return hitTiles;
    }

    public void move() {
        // Collisions
        if (!getCollidingTiles(rect).isEmpty() || getCollidingTiles(leftBottomRect).isEmpty() || getCollidingTiles(rightBottomRect).isEmpty()) {
            direction = direction * -1;
        }

        if (direction == 1) {
            movement.x = speed;
        } else if (direction == -1) {
            movement.x = -speed;
        }

        rect.x += movement.x;
        updateSprite();
    }

    public void updateSprite() {
        leftBottomRect.setPosition(rect.x - Map.TILE_SIZE, rect.y - Map.TILE_SIZE);
        rightBottomRect.setPosition(rect.x + Map.TILE_SIZE, rect.y - Map.TILE_SIZE);
        sprite.setPosition(rect.x, rect.y);
    }

    public void run() {
        move();
    }
}
