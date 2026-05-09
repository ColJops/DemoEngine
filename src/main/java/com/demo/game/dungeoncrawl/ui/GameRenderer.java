package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Objects;

public class GameRenderer {

    private final Canvas canvas;
    private final int viewWidth;
    private final int viewHeight;

    public GameRenderer(Canvas canvas, int viewWidth, int viewHeight) {
        this.canvas = Objects.requireNonNull(canvas, "canvas");
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public void render(GameMap map, double cameraX, double cameraY) {
        GraphicsContext context = canvas.getGraphicsContext2D();

        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int y = 0; y < viewHeight; y++) {
            for (int x = 0; x < viewWidth; x++) {

                int mapX = x + (int) cameraX;
                int mapY = y + (int) cameraY;

                Cell cell = map.getCell(mapX, mapY);
                if (cell == null) continue;

                renderCell(context, cell, x, y, map);
            }
        }
    }

    private void renderCell(GraphicsContext context, Cell cell, int screenX, int screenY, GameMap map) {
        Tiles.drawTile(context, cell, screenX, screenY, map);

        if (cell.getItem() != null) {
            Tiles.drawTile(context, cell.getItem(), screenX, screenY, map);
        }

        if (cell.getActor() instanceof Drawable drawable) {
            Tiles.drawTile(context, drawable, screenX, screenY, map);
        }
    }
}
