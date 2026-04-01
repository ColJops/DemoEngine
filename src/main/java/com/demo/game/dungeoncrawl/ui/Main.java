package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.logic.MapLoader;
import com.demo.game.dungeoncrawl.model.item.HealthPotion;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import com.demo.game.dungeoncrawl.engine.GameEngine;
import javafx.stage.StageStyle;

import static com.demo.game.dungeoncrawl.model.map.CellType.WALL;

public class Main extends Application {

    private GameMap map;
    private GameEngine engine;
    private Canvas canvas;
    private Canvas minimap;
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

        int uiHeight = 200; // minimapa + log
        int topBarHeight = 30;

        Scene scene = new Scene(
                createContent(stage),
                canvasWidth + 260,
                canvasHeight + uiHeight + topBarHeight
        );

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
        stage.initStyle(StageStyle.UNDECORATED);
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

    private Parent createContent(Stage stage) {

        int canvasWidth = VIEW_WIDTH * Tiles.TILE_WIDTH;
        int canvasHeight = VIEW_HEIGHT * Tiles.TILE_HEIGHT;

        canvas = new Canvas(canvasWidth, canvasHeight);
        minimap = new Canvas(150, 150);

        BorderPane root = new BorderPane();

        // 🎮 środek (gra)
        root.setCenter(canvas);

        // 👉 prawa strona (HUD)
        VBox ui = createUI();
        ui.setPrefWidth(260);
        root.setRight(ui);

        // 👉 dół (minimapa + log)
        HBox bottomBar = new HBox(10, minimap, logArea);
        VBox centerBox = new VBox(canvas);
        root.setCenter(centerBox);
        bottomBar.setStyle("-fx-background-color: #111; -fx-padding: 10;");
        bottomBar.setPrefHeight(160);

        root.setBottom(bottomBar);
        root.setTop(createTopBar(stage));
        root.setStyle("-fx-background-color: black;");

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

        logArea = new TextArea();
        logArea.setEditable(false);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(140);
        logArea.setStyle("""
             -fx-control-inner-background: #1e1e1e;
             -fx-text-fill: #cccccc;
            """);

        hud.getChildren().addAll(
                title,
                statsBox,
                equipmentTitle,
                equipmentBox,
                inventoryTitle,
                inventoryScroll
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
        drawMinimap();
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

    private void drawMinimap() {

        GraphicsContext g = minimap.getGraphicsContext2D();

        g.clearRect(0, 0, minimap.getWidth(), minimap.getHeight());

        double scaleX = minimap.getWidth() / map.getWidth();
        double scaleY = minimap.getHeight() / map.getHeight();

        Cell cell = null;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {

                cell = map.getCell(x, y);
                if (cell == null) continue;

                if (cell.getType() == WALL) {
                    g.setFill(javafx.scene.paint.Color.DARKGRAY);
                } else {
                    g.setFill(javafx.scene.paint.Color.BLACK);
                }

                g.fillRect(x * scaleX, y * scaleY, scaleX, scaleY);
            }
        }

        // 🔴 player
        Player p = map.getPlayer();
        g.setFill(javafx.scene.paint.Color.RED);
        g.fillOval(
                p.getX() * scaleX,
                p.getY() * scaleY,
                scaleX,
                scaleY
        );
    }

    private HBox createTopBar(Stage stage) {

        final double[] xOffset = {0};
        final double[] yOffset = {0};

        Label title = new Label("Dungeon Crawl");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Button restartBtn = new Button("Restart");
        Button levelBtn = new Button("Restart Level");
        Button saveBtn = new Button("Save");
        Button loadBtn = new Button("Load");
        Button closeBtn = new Button("X");

        closeBtn.setOnAction(e -> stage.close());

        // styl
        String btnStyle = "-fx-background-color: #333; -fx-text-fill: white;";
        String hoverStyle = """
                -fx-background-color: #555;
                -fx-text-fill: white;
            """;
        styleButton(levelBtn);
        styleButton(saveBtn);
        styleButton(loadBtn);
        styleButton(restartBtn);
        closeBtn.setStyle("-fx-background-color: #662222; -fx-text-fill: white;");
        //Działające przyciski
        restartBtn.setOnAction(e -> {
            currentLevel = 1;

            this.map = MapLoader.loadMap("map1.txt");
            this.engine = new GameEngine(map);

            refresh();
            log("Game restarted");
        });
        levelBtn.setOnAction(e -> {
            String mapName = "map" + currentLevel + ".txt";

            this.map = MapLoader.loadMap(mapName);
            this.engine = new GameEngine(map);

            refresh();
            log("Level restarted");
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(10,
                title,
                restartBtn,
                levelBtn,
                saveBtn,
                loadBtn,
                spacer,
                closeBtn
        );
        bar.setPadding(new Insets(5, 10, 5, 10));

        bar.setStyle("-fx-background-color: #222; -fx-padding: 5;");
        bar.setOnMousePressed(e -> {
            xOffset[0] = e.getSceneX();
            yOffset[0] = e.getSceneY();
        });

        bar.setOnMouseDragged(e -> {
            Stage stageRef = (Stage) bar.getScene().getWindow();
            stageRef.setX(e.getScreenX() - xOffset[0]);
            stageRef.setY(e.getScreenY() - yOffset[0]);
        });
        return bar;
    }

    private void styleButton(Button btn) {

        String normal = "-fx-background-color: #333; -fx-text-fill: white;";
        String hover = "-fx-background-color: #555; -fx-text-fill: white;";

        btn.setStyle(normal);

        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
