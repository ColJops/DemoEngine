package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.logic.MapLoader;
import com.demo.game.dungeoncrawl.model.item.HealthPotion;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
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
    private Canvas canvas;
    public static Main instance;

    //HUD
    private Label hpLabel;
    private Label attackLabel;
    private Label defenseLabel;
    private Label killLabel;
    private int currentLevel = 1;
    private ProgressBar hpBar;
    private VBox inventoryBox;
    private Label weaponLabel;
    private Label shieldLabel;
    private TextArea logArea;

    //Viewport
    private final int VIEW_WIDTH = 20;
    private final int VIEW_HEIGHT = 15;
    private double cameraX = 0;
    private double cameraY = 0;

    @Override
    public void start(Stage stage) {

        instance = this;
        map = MapLoader.loadMap("map1.txt");
        engine = new GameEngine(map);

        int canvasWidth = VIEW_WIDTH * Tiles.TILE_WIDTH;
        int canvasHeight = VIEW_HEIGHT * Tiles.TILE_HEIGHT;

        Scene scene = new Scene(createContent(), canvasWidth + 260, canvasHeight);

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
        stage.setResizable(false);
        stage.sizeToScene();
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

        int canvasWidth = VIEW_WIDTH * Tiles.TILE_WIDTH;
        int canvasHeight = VIEW_HEIGHT * Tiles.TILE_HEIGHT;

        canvas = new Canvas(canvasWidth, canvasHeight);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        VBox ui = createUI();
        ui.setPrefWidth(260);
        root.setStyle("-fx-background-color: black;");
        root.setRight(ui);

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
        hpBar.setPrefWidth(200);
        hpBar.setPrefHeight(18);
        hpBar.setStyle("-fx-accent: red;");
        hud.setMinWidth(260);
        hud.setMaxWidth(260);

        hpLabel = new Label();
        attackLabel = new Label();
        defenseLabel = new Label();
        killLabel = new Label();

        VBox statsBox = new VBox(5, hpBar, hpLabel, attackLabel, defenseLabel, killLabel);
        statsBox.setFillWidth(true);
        VBox.setVgrow(hpBar, javafx.scene.layout.Priority.NEVER);
        Label equipmentTitle = new Label("EQUIPMENT");
        equipmentTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        weaponLabel = new Label();
        shieldLabel = new Label();

        VBox equipmentBox = new VBox(5, weaponLabel, shieldLabel);
        weaponLabel.setStyle("-fx-text-fill: #ff9933;");
        shieldLabel.setStyle("-fx-text-fill: #66ccff;");
        Label inventoryTitle = new Label("INVENTORY");
        inventoryTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        inventoryBox = new VBox(5);
        ScrollPane inventoryScroll = new ScrollPane(inventoryBox);
        inventoryScroll.setStyle("""
              -fx-background: #1e1e1e;
              -fx-background-color: #1e1e1e;
               \s""");

        inventoryBox.setStyle("-fx-background-color: #1e1e1e;");
        inventoryScroll.setPrefHeight(150);
        inventoryScroll.setFitToWidth(true);

        Label logTitle = new Label("COMBAT LOG");
        logTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(200);

        hud.getChildren().addAll(
                title,
                statsBox,
                equipmentTitle,
                equipmentBox,
                inventoryTitle,
                inventoryScroll,
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

        Player player = map.getPlayer();

        double targetX = player.getX() - VIEW_WIDTH / 2.0;
        double targetY = player.getY() - VIEW_HEIGHT / 2.0;
        cameraX += (targetX - cameraX) * 0.1;
        cameraY += (targetY - cameraY) * 0.1;


        // clamp (żeby nie wyjść poza mapę)
        double maxX = Math.max(0, map.getWidth() - VIEW_WIDTH);
        double maxY = Math.max(0, map.getHeight() - VIEW_HEIGHT);

        cameraX = Math.max(0, Math.min(cameraX, maxX));
        cameraY = Math.max(0, Math.min(cameraY, maxY));

        GraphicsContext context = canvas.getGraphicsContext2D();

        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

// 🔥 RENDER TYLKO VIEWPORTU
        for (int y = 0; y < VIEW_HEIGHT; y++) {
            for (int x = 0; x < VIEW_WIDTH; x++) {

                int mapX = x + (int) cameraX;
                int mapY = y + (int) cameraY;

                Cell cell = map.getCell(mapX, mapY);
                if (cell == null) continue;

                // tło
                Tiles.drawTile(context, cell, x, y, map);

                // item
                if (cell.getItem() != null) {
                    Tiles.drawTile(context, (Drawable) cell.getItem(), x, y, map);
                }

                // actor
                if (cell.getActor() instanceof Drawable) {
                    Tiles.drawTile(context, (Drawable) cell.getActor(), x, y, map);
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

        // EQUIPMENT
        if (player.getEquippedWeapon() != null) {
            weaponLabel.setText("Weapon: " + player.getEquippedWeapon().getName());
        } else {
            weaponLabel.setText("Weapon: none");
        }

        if (player.getEquippedShield() != null) {
            shieldLabel.setText("Shield: " + player.getEquippedShield().getName());
        } else {
            shieldLabel.setText("Shield: none");
        }

        inventoryBox.getChildren().clear();

        int index = 1;
        for (Item item : player.getInventory()) {
            Label itemLabel = new Label(index + ". " + item.getName());

            if (player.isEquipped(item)) {
                itemLabel.setStyle("-fx-text-fill: #66ff66; -fx-font-weight: bold;");
                itemLabel.setText(index + ". " + item.getName() + " (E)");

            } else {
                itemLabel.setStyle("-fx-text-fill: gold;");
            }
            inventoryBox.getChildren().add(itemLabel);
            index++;
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

        // inventory
        newPlayer.getInventory().addAll(oldPlayer.getInventory());

        // equipment (poprawnie!)
        if (oldPlayer.getEquippedWeapon() != null) {
            newPlayer.equipWeapon(oldPlayer.getEquippedWeapon());
        }

        if (oldPlayer.getEquippedShield() != null) {
            newPlayer.equipShield(oldPlayer.getEquippedShield());
        }

        // staty
        newPlayer.setKills(oldPlayer.getKills());

        // HP
        newPlayer.takeDamage(-(oldPlayer.getHp() - newPlayer.getHp()));

        // jeśli masz system statów:
        newPlayer.recalculateStats();

        this.map = newMap;
        this.engine = new GameEngine(map);

        resizeCanvas();
        refresh();

        if (instance != null) {
            log("Entered level " + currentLevel);
        }
    }

    private void resizeCanvas() {
        canvas.setWidth(VIEW_WIDTH * Tiles.TILE_WIDTH);
        canvas.setHeight(VIEW_HEIGHT * Tiles.TILE_HEIGHT);
    }



    public static void main(String[] args) {
        launch(args);
    }
}
