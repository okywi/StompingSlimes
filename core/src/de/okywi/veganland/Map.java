package de.okywi.veganland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {
    public static int[][] map;

    public static int MAP_HEIGHT;
    public static int TILE_SIZE;

    public static int xOffset = -4;

    public static ArrayList<Tile> mapTiles = new ArrayList<>();
    public static ArrayList<Enemy> enemies = new ArrayList<>();

    Map() {
    }

    public void loadLevel(String mapName) throws IOException {
        map = loadMap(mapName);
        MAP_HEIGHT = map.length;
        TILE_SIZE = (VeganLand.GAME_SIZE[1] / MAP_HEIGHT);
        createTiles();
        createEnemy();
        VeganLand.player = getPlayer();
    }

    public int[][] loadMap(String mapName) throws IOException {
        ArrayList<ArrayList<Integer>> data = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(Gdx.files.internal("levels/" + mapName + ".csv").toString()));

        mapTiles.clear();
        enemies.clear();

        String line = br.readLine();
        int lineCounter = 0;
        while (line != null) {
            String[] dataLine = line.split(",");

            data.add(new ArrayList<Integer>());

            for (int i = 0; i < dataLine.length; i++) {
                data.get(lineCounter).add(Integer.parseInt(dataLine[i]));
            }
            lineCounter++;
            line = br.readLine();
        }

        int[][] map = new int[data.size()][data.get(0).size()];

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                map[i][j] = data.get(i).get(j);
            }
        }

        return map;
    }

    public void createTiles() {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                int type = map[y][x];
                if (type != -1 && type <= 20) {
                    Tile tile = new Tile((x + xOffset) * TILE_SIZE, ((map.length - 1) - y) * TILE_SIZE, TILE_SIZE, TILE_SIZE, type);
                    mapTiles.add(tile);
                }
                if (type == 255) {
                    VeganLand.flag = new Flag((x + xOffset) * TILE_SIZE, ((map.length - 1) - y) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    public void createEnemy() {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                int type = map[y][x];
                if (type <= 253 && type > 20) {
                    Enemy enemy = new Enemy(((x + xOffset) * TILE_SIZE), ((map.length - 1) - y) * TILE_SIZE, TILE_SIZE, TILE_SIZE, 3, 253 - type);
                    enemies.add(enemy);
                }
            }
        }
    }

    public Player getPlayer() {
        Player player = null;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                int type = map[y][x];
                if (type == 254) {
                    player = new Player(((x + xOffset) * TILE_SIZE), ((map.length - 1) - y) * TILE_SIZE);
                }
            }
        }
        return player;
    }

    public void draw(SpriteBatch batch) {
        for (Tile tile : mapTiles) {
            tile.sprite.draw(batch);
        }
    }
}
