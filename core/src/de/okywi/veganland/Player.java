package de.okywi.veganland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Player {
    Texture texture = new Texture(Gdx.files.internal("player/player_idle.png"));
    float width = Map.TILE_SIZE - (Map.TILE_SIZE / 4);
    float height = Map.TILE_SIZE * 2f - (Map.TILE_SIZE / 2);

    boolean isJumping = false;
    boolean isGrounded = false;
    boolean isRunning = false;
    boolean isFlipped = false;
    float jumpHeight = 25f;
    float jumpVelocity = jumpHeight;
    float acceleration = 0.8f;
    float speedMax = 8f;
    float gravity = -1.5f;
    Vector2 movement = new Vector2(0, 0);

    // Sprite
    Sprite sprite = new Sprite(texture);
    Rectangle playerRect;

    // Collision
    boolean collidesLeft = false;
    boolean collidesRight = false;
    int scrollBorder = VeganLand.GAME_SIZE[0] / 3;

    // Animations
    Texture[] runTextures = new Texture[2];
    double runTimer = 0;
    float runTime = 90f;
    int runCounter = 0;

    // Health
    int health = 3;
    int healthBarOffset = 20;
    Texture healthBar = new Texture(Gdx.files.internal("player/healthbar-3.png"));
    Sprite healthBarSprite = new Sprite(healthBar);
    boolean isInvincible = false;
    double invincibleTimer = 0;
    double invincibleTime = 1000;

    Texture idleTexture = new Texture("player/player_idle.png");

    Player(float x, float y) {
        sprite.setBounds(x, y, width, height);
        healthBarSprite.setSize(width, healthBar.getHeight() * 1.5f);
        playerRect = new Rectangle(x, y, width, height);

        // Create animation textures
        for (int i = 0; i < runTextures.length; i++) {
            runTextures[i] = new Texture("player/player_run_" + i + ".png");
        }
    }

    public void animate() {
        // Rotation
        sprite.setFlip(isFlipped, false);

        // running
        if (System.currentTimeMillis() - runTimer >= runTime) {
            runCounter = (runCounter + 1) % runTextures.length;
            runTimer = System.currentTimeMillis();
        }
        if (isRunning) {
            sprite.setTexture(runTextures[runCounter]);
        }

        // idle
        if (!isRunning) {
            sprite.setTexture(idleTexture);
        }

        // Invincible Fade
        if (isInvincible) {
            sprite.setAlpha(0.3f);
        } else {
            sprite.setAlpha(1f);
        }
    }

    public void handleInput() {
        double noMoveTime = System.currentTimeMillis() - invincibleTimer;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && noMoveTime > 500) {
            if (isGrounded && !isJumping) {
                isJumping = true;
                isGrounded = false;
            }
        }

        // x-acceleration
        if (Gdx.input.isKeyPressed(Input.Keys.A) && !collidesLeft && noMoveTime > 500) {
            isRunning = true;
            isFlipped = false;
            movement.x -= acceleration;
            if (movement.x <= -speedMax) {
                movement.x = -speedMax;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) && !collidesRight && noMoveTime > 500) {
            isRunning = true;
            isFlipped = true;
            movement.x += acceleration;
            if (movement.x >= speedMax) {
                movement.x = speedMax;
            }
        } else {
            if (movement.x > -acceleration && movement.x < acceleration) {
                movement.x = 0;
                isRunning = false;
            } else if (movement.x < 0) {
                movement.x += acceleration;
            } else if (movement.x > 0) {
                movement.x -= acceleration;
            }
        }
    }

    public boolean insideNoScrollingArea() {
        if (playerRect.x + movement.x > scrollBorder && playerRect.x < VeganLand.GAME_SIZE[0] - scrollBorder - movement.x) {
            return true;
        }
        return false;
    }

    public ArrayList<Rectangle> getCollidingTiles() {
        ArrayList<Rectangle> hitTiles = new ArrayList<>();

        for (Tile tile : Map.mapTiles) {
            if (playerRect.overlaps(tile.tileRect)) {
                hitTiles.add(tile.tileRect);
            }
        }

        return hitTiles;
    }

    public void move() {
        // Apply gravity when not colliding with anything
        if (getCollidingTiles().isEmpty()) {
            movement.y += gravity;
            isGrounded = false;
        }

        double noMoveTime = System.currentTimeMillis() - invincibleTimer;
        if (noMoveTime < 500) {
            return;
        }

        if (isJumping && !isGrounded) {
            movement.y = jumpVelocity;
            jumpVelocity += gravity;

            if (jumpVelocity <= 0) {
                isJumping = false;
                jumpVelocity = jumpHeight;
            }
        }

        // Check death
        if (playerRect.y < -height) {
            VeganLand.isGameOver = true;
            VeganLand.gameState = "restart";
        }

        collidesLeft = false;
        collidesRight = false;

        boolean isScrolling = false;

        // Scrolling | no scrolling
        if (movement.x > 0 || movement.x < 0) {
            if (!insideNoScrollingArea()) {
                scrollMap(movement.x);
                isScrolling = true;
            } else {
                playerRect.x += movement.x;
            }
        }

        for (Rectangle tile : getCollidingTiles()) {
            if (movement.x < 0) {
                movement.x = 0;
                collidesLeft = true;
                isRunning = false;
                if (isScrolling) {
                    float tileOffset = (tile.x + tile.width) - playerRect.x;
                    scrollMap(tileOffset);
                } else {
                    playerRect.x = tile.x + tile.width;
                }
            }
            else if (movement.x > 0) {
                movement.x = 0;
                collidesRight = true;
                isRunning = false;
                if (isScrolling) {
                    float tileOffset = (tile.x - width) - playerRect.x;
                    scrollMap(tileOffset);
                } else {
                    playerRect.x = tile.x - width;
                }
            }
        }

        // y collision
        playerRect.y += movement.y;

        for (Rectangle tile : getCollidingTiles()) {
            if (movement.y < 0) {
                movement.y = 0;
                playerRect.y = tile.y + tile.height;
                isGrounded = true;
                isJumping = false;
            }
            else if (movement.y > 0) {
                jumpVelocity = jumpHeight;
                movement.y = 0;
                playerRect.y = tile.y - height;
                isGrounded = false;
                isJumping = false;
            }
        }

        // Invincible timer
        if (System.currentTimeMillis() - invincibleTimer >= invincibleTime) {
            isInvincible = false;
        }

        // Enemy collision
        Enemy removedEnemy = null;
        for (Enemy enemy : Map.enemies) {
            if (enemy.rect.overlaps(playerRect)) {
                if (movement.y < -jumpHeight + movement.y * -1) {
                    removedEnemy = enemy;
                    isJumping = true;
                    isGrounded = false;
                    isInvincible = false;
                    VeganLand.score += 1;
                    VeganLand.scoreString = "Score " + VeganLand.score;
                } else {
                    if (!isInvincible) {
                        health -= 1;
                        healthBarSprite.setTexture(new Texture(Gdx.files.internal("player/healthbar-" + health + ".png")));

                        if (health == 0)  {
                            VeganLand.isGameOver = true;
                            VeganLand.gameState = "restart";
                            return;
                        }

                        isJumping = false;
                        playerRect.y = enemy.rect.y;

                        invincibleTimer = System.currentTimeMillis();
                        isInvincible = true;
                    }
                }

            }
        }
        Map.enemies.remove(removedEnemy);
    }

    public void scrollMap(float dx) {
        for (Tile tile : Map.mapTiles) {
            tile.tileRect.x -= dx;
            tile.updatePosition();
        }
        for (Enemy enemy : Map.enemies) {
            enemy.rect.x -= dx;
            enemy.updateSprite();
        }

        VeganLand.flag.rect.x -= dx;
        VeganLand.flag.updatePosition();
    }

    public void updateSprite() {
        sprite.setPosition(playerRect.x, playerRect.y);
        healthBarSprite.setPosition(playerRect.x, playerRect.y + height + healthBarOffset);
    }

    public void drawHealthBar(SpriteBatch batch) {
        healthBarSprite.draw(batch);
    }

    public void run(SpriteBatch batch) {
        handleInput();
        move();
        animate();
        updateSprite();
    }

}
