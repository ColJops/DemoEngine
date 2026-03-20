package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.logic.MapLoader;
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
    private int currentLevel = 1;

    public static Main instance;

    private ProgressBar hpBar;

    private VBox inventoryBox;
    private TextArea logArea;

    @Override
    public void start(Stage stage) {

        instance = this;
        map = MapLoader.loadMap("map1.txt");
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
                //Używanie przedmiotów w inventory
                case DIGIT1: useItem(0); break;
                case DIGIT2: useItem(1); break;
                case DIGIT3: useItem(2); break;
                case DIGIT4: useItem(3); break;
                case DIGIT5: useItem(4); break;
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

                // item
                if (cell.getItem() instanceof Drawable) {
                    Tiles.drawTile(context,
                            (Drawable) cell.getItem(),
                            x, y);
                }

                // actor
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
        //hpLabel.setStyle("-fx-text-fill: #ff4d4d;");
        if (hp > 10) {
            hpLabel.setStyle("-fx-text-fill: #66ff66;");
        } else if (hp > 5) {
            hpLabel.setStyle("-fx-text-fill: #ffcc00;");
        } else {
            hpLabel.setStyle("-fx-text-fill: #ff4d4d;");
        }

        attackLabel.setText("ATK: " + player.getAttack());
        attackLabel.setStyle("-fx-text-fill: #ff9933;");

        defenseLabel.setText("DEF: " + player.getDefense());
        defenseLabel.setStyle("-fx-text-fill: #66ccff;");

        killLabel.setText("KILLS: " + player.getKills());
        killLabel.setStyle("-fx-text-fill: #cc99ff;");

        inventoryBox.getChildren().clear();

        for (Item item : player.getInventory()) {
            Label itemLabel = new Label(item.getName());
            itemLabel.setStyle("-fx-text-fill: gold;");
            inventoryBox.getChildren().add(itemLabel);
        }
    }

    private void useItem(int index) {

        Player player = map.getPlayer();

        if (player.getInventory().size() <= index) {
            Main.log("Empty slot.");
            return; // pusty slot → nic się nie dzieje
        }

        Item item = player.getInventory().get(index);

        item.use(player);

        if (item instanceof HealthPotion) {
            player.getInventory().remove(index);
        }
    }

    public void nextLevel() {

        currentLevel++;

        String nextMap = "map" + currentLevel + ".txt";

        GameMap newMap = MapLoader.loadMap(nextMap);

        // przenosimy gracza
        Player oldPlayer = map.getPlayer();
        Player newPlayer = newMap.getPlayer();

        newPlayer.getInventory().addAll(oldPlayer.getInventory());

        // zachowujemy staty
        newPlayer.takeDamage(-(oldPlayer.getHp() - newPlayer.getHp()));

        this.map = newMap;
        this.engine = new GameEngine(map);

        if (instance != null) {
            log("Entered level " + currentLevel);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
