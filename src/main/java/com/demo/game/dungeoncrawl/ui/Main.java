package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.Cell;
import com.demo.game.dungeoncrawl.model.GameMap;
import com.demo.game.dungeoncrawl.logic.MapLoader;
import com.demo.game.dungeoncrawl.model.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import com.demo.game.dungeoncrawl.engine.GameEngine;

public class Main extends Application {

    private GameMap map;
    private GameEngine engine;
    private GridPane gridPane;
    private Canvas canvas;
    private Label hpLabel;
    private Label attackLabel;
    private Label defenseLabel;
    private Label killLabel;

    public static Main instance;

    private ProgressBar hpBar;

    private VBox inventoryBox;
    private TextArea logArea;

    @Override
    public void start(Stage stage) {

        instance = this;
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
        scene.getRoot().requestFocus();

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

        BorderPane root = new BorderPane();

        root.setCenter(canvas);
        root.setRight(createUI());

        refresh();

        return root;
    }

    private VBox createUI() {

        VBox hud = new VBox(15);
        hud.setPrefWidth(260);
        hud.setStyle("""
        -fx-background-color: #1e1e1e;
        -fx-padding: 15;
        -fx-border-color: #444;
        -fx-border-width: 0 0 0 2;
    """);

        Label title = new Label("PLAYER");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        hpBar = new ProgressBar(1);
        hpBar.setStyle("-fx-accent: red;");
        hpBar.setPrefWidth(200);

        hpLabel = new Label();
        attackLabel = new Label();
        defenseLabel = new Label();
        killLabel = new Label();

        VBox statsBox = new VBox(5, hpBar, hpLabel, attackLabel, defenseLabel, killLabel);

        Label inventoryTitle = new Label("INVENTORY");
        inventoryTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        inventoryBox = new VBox(5);

        Label logTitle = new Label("COMBAT LOG");
        logTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(200);

        hud.getChildren().addAll(
                title,
                statsBox,
                inventoryTitle,
                inventoryBox,
                logTitle,
                logArea
        );

        for (var node : hud.getChildren()) {
            if (node instanceof Label l) {
                l.setStyle("-fx-text-fill: white;");
            }
        }

        return hud;
    }

    public void addItem(String name) {

        Label item = new Label(name);
        item.setStyle("-fx-text-fill: gold;");

        inventoryBox.getChildren().add(item);
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
        canvas.requestFocus();
        updateHUD();
    }

    public void addLog(String text) {
        logArea.appendText(text + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public static void log(String text) {
        if (instance != null) {
            instance.addLog(text);
        }
    }

    private void updateHUD() {

        Player player = map.getPlayer();

        int hp = player.getHp();
        int maxHp = 20;

        hpBar.setProgress((double) hp / maxHp);

        hpLabel.setText("HP: " + hp);
        attackLabel.setText("ATK: " + player.getAttack());
        defenseLabel.setText("DEF: " + player.getDefense());
        killLabel.setText("KILLS: " + player.getKills());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
