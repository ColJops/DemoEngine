package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.Cell;
import com.demo.game.dungeoncrawl.model.GameMap;
import com.demo.game.dungeoncrawl.logic.MapLoader;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.demo.game.dungeoncrawl.engine.GameEngine;

public class Main extends Application {

    private GameMap map;
    private GameEngine engine;
    private GridPane gridPane;
    private Canvas canvas;

    @Override
    public void start(Stage stage) {
        map = MapLoader.loadMap();
        engine = new GameEngine(map);

        Scene scene = new Scene(createContent());

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    engine.handlePlayerMove(0, -1);
                    break;
                case DOWN:
                    engine.handlePlayerMove(0, 1);
                    break;
                case LEFT:
                    engine.handlePlayerMove(-1, 0);
                    break;
                case RIGHT:
                    engine.handlePlayerMove(1, 0);
                    break;
            }
            refresh();
        });

        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                engine.update(now);
                refresh();
            }
        };

        timer.start();
    }

    private Parent createContent() {

        canvas = new Canvas(
                map.getWidth() * Tiles.TILE_WIDTH,
                map.getHeight() * Tiles.TILE_WIDTH
        );

        refresh();

        return new Pane(canvas);
    }

    private void refresh() {

        GraphicsContext context = canvas.getGraphicsContext2D();

        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {

                Cell cell = map.getCell(x, y);

                // najpierw tło (floor / wall)
                Tiles.drawTile(context, cell.getType(), x, y);

                // potem aktor jeśli jest
                if (cell.getActor() instanceof Drawable) {
                    Tiles.drawTile(context,
                            (Drawable) cell.getActor(),
                            x, y);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
