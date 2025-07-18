package de.okywi.stompingslimes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.File;
import java.io.IOException;

public class StompingSlimes extends ApplicationAdapter {
	public static int[] SCREEN_SIZE = {1920, 1080};
	public static int[] GAME_SIZE = {1920, 1080};

	OrthographicCamera camera;
	SpriteBatch batch;
	ShapeRenderer shape;
	public static Player player;
	static Map map = new Map();
	public static int level = 1;
	public static int score = 0;
	public static String scoreString = "Score " + score;
	public static Flag flag;
	public static boolean isGameOver = false;
	public static String gameState = "menu";
	public static String levelString = "Level " + level;
	public int levelStringOffset = 40;
	public int scoreStringOffset = 40;
	Button menuButton;
	Button playButton;
	Button quitButton;
	Button restartButton;
	Button backLevelsButton;
	Button[] levelButtons;
	Button levelButton;
	Button pausedMenuButton;
	Button pausedResumeButton;
	int levelButtonOffset = 300;

	Texture menuScreen;
	Texture background;

	BitmapFont font;
	FreeTypeFontGenerator fontGenerator;
	FreeTypeFontParameter parameter;
	GlyphLayout layout;
	float scoreStringWidth;
	float scoreStringHeight;

	@Override
	public void create () {
		camera = new OrthographicCamera(GAME_SIZE[0], GAME_SIZE[1]);
		camera.setToOrtho(false);
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		shape.setAutoShapeType(true);
		batch.setProjectionMatrix(camera.combined);
		shape.setProjectionMatrix(camera.combined);

		background = new Texture(Gdx.files.internal("background.png"));
		menuScreen = new Texture(Gdx.files.internal("menu-screen.png"));
		menuButton = new Button(GAME_SIZE[0] / 2, GAME_SIZE[1] / 2 - 400, "Menu", 2f, false);
		playButton = new Button(GAME_SIZE[0] / 2, GAME_SIZE[1] / 2 - 100, "Play", 1.2f, true);
		levelButton = new Button(GAME_SIZE[0] / 2, GAME_SIZE[1] / 2 + 100, "Levels", 1.2f, true);
		quitButton = new Button(GAME_SIZE[0] / 2, GAME_SIZE[1] / 2 + 300, "Quit", 1.2f, true);

		pausedResumeButton = new Button(GAME_SIZE[0] / 2, GAME_SIZE[1] / 2 - 100, "Resume", 1.2f, true);
		pausedMenuButton = new Button(GAME_SIZE[0] / 2, GAME_SIZE[1] / 2 + 100, "Menu", 1.2f, true);

		restartButton = new Button(GAME_SIZE[0] / 2, GAME_SIZE[1] / 2 - 100, "Restart", 1.2f, true);

		backLevelsButton = new Button(100, 1000, "Back", 1, true);

        try {
			int levelCount = getLevelCount();
            levelButtons = new Button[levelCount];

			int yOffset = 0;
			for (int i = 0; i < levelCount; i++) {
				int xOffset = levelButtonOffset * ((i % 5) + 1);
				if (i % 5 == 0 && i != 0) {
					yOffset += 200;
				}
				levelButtons[i] = new Button(100 + (xOffset), 400 + yOffset, "Level " + (i + 1), 1f, true);
			}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            changeLevel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player = map.getPlayer();

		// Font
		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Silkscreen-Regular.ttf"));
		parameter = new FreeTypeFontParameter();
		parameter.size = (int) (40);
		parameter.color = Color.WHITE;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 3f;
		parameter.borderStraight = false;
		font = fontGenerator.generateFont(parameter);

		layout = new GlyphLayout(font, scoreString);
		this.scoreStringWidth = layout.width;
		this.scoreStringHeight = layout.height;
	}

	public void updateEnemies(SpriteBatch batch) {
		for (Enemy enemy : Map.enemies) {
			if (!isGameOver && !gameState.equals("paused")) {
				enemy.run();
			}

			enemy.sprite.draw(batch);
		}
	}

	public int getLevelCount() throws IOException {
		File file = new File(Gdx.files.internal("levels").toString());
		if (file.list() != null) {
			return file.list().length;
		}
		return 0;
	}


	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		camera.update();
		batch.begin();
		manageButtons();

		if (gameState.equals("menu")) {
			batch.draw(menuScreen, 0, 0);
			menuButton.run(batch);
			playButton.run(batch);
			levelButton.run(batch);
			quitButton.run(batch);
		}

		if (gameState.equals("levels")) {
			batch.draw(menuScreen, 0, 0);
			menuButton.run(batch);
			backLevelsButton.run(batch);
			for (Button button : levelButtons) {
				button.run(batch);
			}
		}

		if (gameState.equals("playing") || gameState.equals("paused") || gameState.equals("restart")) {
			// Enemies
			updateEnemies(batch);

			// Map
			map.draw(batch);

			// Flag
			flag.sprite.draw(batch);

			// Draw level string
			font.draw(batch, levelString, levelStringOffset, GAME_SIZE[1] - levelStringOffset);

			// Draw score
			font.draw(batch, scoreString, GAME_SIZE[0] - scoreStringOffset - scoreStringWidth, GAME_SIZE[1] - scoreStringOffset);

			// Player
			if (!isGameOver && !gameState.equals("paused")) {
				player.run(batch);
				flag.playerCollision();
			}
			player.drawHealthBar(batch);
			player.sprite.draw(batch);
			flag.drawText(batch);

			// Handle restart
			if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
				try {
					changeLevel();
					isGameOver = false;
					gameState = "playing";
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			if (gameState.equals("paused")) {
				pausedMenuButton.run(batch);
				pausedResumeButton.run(batch);
			}

			if (gameState.equals("restart")) {
				restartButton.run(batch);
				pausedMenuButton.run(batch);
			}
		}

		batch.end();
	}

	public void manageButtons() {
		if (gameState.equals("menu")) {
			if (playButton.isClicked) {
				gameState = "playing";
				level = 1;
                try {
                    changeLevel();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
			}
			if (quitButton.isClicked) {
				Gdx.app.exit();
			}
			if (levelButton.isClicked) {
				gameState = "levels";
				return;
			}
		}

		if (gameState.equals("restart")) {
			if (restartButton.isClicked) {
                try {
                    changeLevel();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                isGameOver = false;
				gameState = "playing";
			}
		}

		if (gameState.equals("paused") || gameState.equals("restart")) {
			if (pausedMenuButton.isClicked) {
				gameState = "menu";
				return;
			}
		}

		if (pausedResumeButton.isClicked || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			if (StompingSlimes.gameState.equals("paused")) {
				StompingSlimes.gameState = "playing";
			} else if (StompingSlimes.gameState.equals("playing")) {
				StompingSlimes.gameState = "paused";
			}
			pausedResumeButton.isClicked = false; // Fix weird bug where pausedResumeButton is still clicked
			return;
		}

		if (gameState.equals("levels")) {
			if (backLevelsButton.isClicked) {
				gameState = "menu";
				return;
			}

			for (int i = 0; i < levelButtons.length; i++) {
				if (levelButtons[i].isClicked) {
					level = i + 1;
					try {
						changeLevel();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					gameState = "playing";
				}
			}
		}
	}

	public static void changeLevel() throws IOException {
		map = new Map();
		map.loadLevel("Level-" + level);
		StompingSlimes.levelString = "Level " + level;
		StompingSlimes.score = 0;
		StompingSlimes.scoreString = "Score " + StompingSlimes.score;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
