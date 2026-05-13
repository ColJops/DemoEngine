package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.SaveData;
import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import com.demo.game.dungeoncrawl.engine.GameEngine;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;

import static com.demo.game.dungeoncrawl.model.map.CellType.WALL;

public class Main extends Application {

    private Stage primaryStage;
    private AnimationTimer gameLoop;
    private boolean gameOver = false;
    private long gameStartNano = 0;
    private long pausedStartedNano = 0;
    private long totalPausedNano = 0;
    private long finalSurvivalNano = 0;
    private static final double GAME_OVER_KILLS_X = 590;
    private static final double GAME_OVER_KILLS_Y = 305;

    private static final double GAME_OVER_LEVEL_X = 590;
    private static final double GAME_OVER_LEVEL_Y = 350;

    private static final double GAME_OVER_TIME_X = 545;
    private static final double GAME_OVER_TIME_Y = 400;

    private GameMap map;
    private GameEngine engine;
    private GameSession session;

    private Canvas canvas;
    private Canvas minimap;
    private GameRenderer gameRenderer;

    public static Main instance;

    private boolean minimapDirty = true;
    private boolean paused = false;
    private Label helpLabel;

    private Label hpLabel;
    private Label attackLabel;
    private Label defenseLabel;
    private Label killLabel;
    private ProgressBar hpBar;
    private VBox inventoryBox;
    private Label weaponLabel;
    private Label shieldLabel;
    private TextArea logArea;

    private final int VIEW_WIDTH = 20;
    private final int VIEW_HEIGHT = 15;
    private double cameraX = 0;
    private double cameraY = 0;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        instance = this;

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);

        showTitleScreen();
    }

    private void showTitleScreen() {
        stopGameLoop();

        Image bgImage = new Image(Objects.requireNonNull(getClass().getResource("/images/title_background.png")).toExternalForm());

        ImageView background = new ImageView(bgImage);
        background.setFitWidth(900);
        background.setFitHeight(700);
        background.setPreserveRatio(false);

        Label title = new Label("DUNGEON CRAWL");
        title.setStyle("""
            -fx-text-fill: #f5d27a;
            -fx-font-size: 44px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, black, 8, 0.8, 3, 3);
            """);

        Button newGameBtn = createMenuButton("NEW GAME");
        Button loadBtn = createMenuButton("LOAD GAME");
        Button optionsBtn = createMenuButton("OPTIONS");
        Button quitBtn = createMenuButton("QUIT");

        newGameBtn.setOnAction(_ -> showGameScreen());

        loadBtn.setOnAction(_ -> {
            showGameScreen();
            showLoadDialog();
        });

        optionsBtn.setOnAction(_ -> showOptionsScreen());
        quitBtn.setOnAction(_ -> primaryStage.close());

        VBox menu = new VBox(15, title, newGameBtn, loadBtn, optionsBtn, quitBtn);
        menu.setAlignment(javafx.geometry.Pos.CENTER);
        menu.setTranslateY(40);

        StackPane root = new StackPane(background, menu);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);

        String normal = """
            -fx-background-color: rgba(20,20,20,0.85);
            -fx-text-fill: #f5d27a;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-border-color: #8a6a2a;
            -fx-border-width: 2;
            -fx-padding: 10 35 10 35;
            """;

        String hover = """
            -fx-background-color: rgba(80,40,20,0.95);
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-border-color: #f5d27a;
            -fx-border-width: 2;
            -fx-padding: 10 35 10 35;
            """;

        btn.setMinWidth(240);
        btn.setStyle(normal);

        btn.setOnMouseEntered(_ -> btn.setStyle(hover));
        btn.setOnMouseExited(_ -> btn.setStyle(normal));

        return btn;
    }

    private void showOptionsScreen() {
        Label title = new Label("OPTIONS");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px;");

        Button backBtn = new Button("Back");
        styleButton(backBtn);

        backBtn.setOnAction(_ -> showTitleScreen());

        VBox layout = new VBox(20, title, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #111; -fx-padding: 60;");

        Scene scene = new Scene(layout, 900, 700);
        primaryStage.setScene(scene);
    }

    private void showGameScreen() {
        stopGameLoop();

        paused = false;
        minimapDirty = true;
        cameraX = 0;
        cameraY = 0;

        session = new GameSession();
        syncFromSession();
        startGameTimer();

        int canvasWidth = VIEW_WIDTH * Tiles.TILE_WIDTH;
        int canvasHeight = VIEW_HEIGHT * Tiles.TILE_HEIGHT;

        Scene scene = new Scene(
                createContent(primaryStage),
                canvasWidth + 260,
                canvasHeight + 230
        );

        setupInput(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getRoot().requestFocus();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!paused && !gameOver) {
                    engine.update(now);
                }

                refresh();
            }
        };

        gameLoop.start();
    }

    private void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
    }

    private Parent createContent(Stage stage) {
        int canvasWidth = VIEW_WIDTH * Tiles.TILE_WIDTH;
        int canvasHeight = VIEW_HEIGHT * Tiles.TILE_HEIGHT;

        canvas = new Canvas(canvasWidth, canvasHeight);
        minimap = new Canvas(150, 150);
        gameRenderer = new GameRenderer(canvas, VIEW_WIDTH, VIEW_HEIGHT);

        BorderPane root = new BorderPane();

        VBox ui = createUI();
        ui.setPrefWidth(260);
        root.setRight(ui);

        helpLabel = new Label();
        helpLabel.setStyle("""
                -fx-text-fill: #cccccc;
                -fx-font-size: 12px;
                """);
        updateHelpText();

        VBox helpBox = new VBox(helpLabel);
        helpBox.setStyle("-fx-background-color: #111; -fx-padding: 10;");

        HBox bottomBar = new HBox(10, minimap, logArea, helpBox);
        bottomBar.setStyle("-fx-background-color: #111; -fx-padding: 10;");
        bottomBar.setPrefHeight(160);

        VBox centerBox = new VBox(canvas);

        root.setCenter(centerBox);
        root.setRight(ui);
        root.setBottom(bottomBar);
        root.setTop(createTopBar(stage));
        root.setStyle("-fx-background-color: black;");

        refresh();

        return root;
    }

    private VBox createUI() {
        VBox hud = new VBox(15);
        hud.setPrefWidth(260);
        hud.setMinWidth(260);
        hud.setMaxWidth(260);
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

        hpLabel = new Label();
        attackLabel = new Label();
        defenseLabel = new Label();
        killLabel = new Label();

        VBox statsBox = new VBox(5, hpBar, hpLabel, attackLabel, defenseLabel, killLabel);

        Label equipmentTitle = new Label("EQUIPMENT");
        equipmentTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        weaponLabel = new Label();
        shieldLabel = new Label();

        VBox equipmentBox = new VBox(5, weaponLabel, shieldLabel);

        Label inventoryTitle = new Label("INVENTORY");
        inventoryTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

        inventoryBox = new VBox(5);
        inventoryBox.setStyle("-fx-background-color: #1e1e1e;");

        ScrollPane inventoryScroll = new ScrollPane(inventoryBox);
        inventoryScroll.setPrefHeight(150);
        inventoryScroll.setFitToWidth(true);
        inventoryScroll.setStyle("""
                -fx-background: #1e1e1e;
                -fx-background-color: #1e1e1e;
                """);

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

        return hud;
    }

    private void setupInput(Scene scene) {
        scene.setOnKeyPressed(event -> {

            if (gameOver) {
                return;
            }

            if (paused
                    && event.getCode() != javafx.scene.input.KeyCode.P
                    && event.getCode() != javafx.scene.input.KeyCode.Q) {
                return;
            }

            switch (event.getCode()) {
                case UP:
                    engine.handlePlayerMove(0, -1);
                    minimapDirty = true;
                    break;

                case DOWN:
                    engine.handlePlayerMove(0, 1);
                    minimapDirty = true;
                    break;

                case LEFT:
                    engine.handlePlayerMove(-1, 0);
                    minimapDirty = true;
                    break;

                case RIGHT:
                    engine.handlePlayerMove(1, 0);
                    minimapDirty = true;
                    break;

                case DIGIT1:
                    useItem(0);
                    break;

                case DIGIT2:
                    useItem(1);
                    break;

                case DIGIT3:
                    useItem(2);
                    break;

                case DIGIT4:
                    useItem(3);
                    break;

                case DIGIT5:
                    useItem(4);
                    break;

                case F5:
                    SaveManager.save(map, session.getCurrentLevel(), 0);
                    log("Quick saved");
                    break;

                case F9:
                    SaveData data = SaveManager.load(0);
                    if (data != null && session.load(data)) {
                        syncFromSession();
                        minimapDirty = true;
                        refresh();
                        log("Quick loaded");
                    }
                    break;

                case P:
                    paused = !paused;

                    if (paused) {
                        pauseGameTimer();
                    } else {
                        resumeGameTimer();
                    }

                    log(paused ? "Game paused" : "Game resumed");
                    updateHelpText();
                    break;

                case Q:
                    pauseAndConfirmQuit(primaryStage);
                    break;
            }

            refresh();
        });
    }

    private void refresh() {
        if (map == null || canvas == null || gameRenderer == null) return;

        Player player = map.getPlayer();

        updateCamera(player);
        renderMap();
        refreshUI();
    }

    private void updateCamera(Player player) {
        double targetX = player.getX() - VIEW_WIDTH / 2.0;
        double targetY = player.getY() - VIEW_HEIGHT / 2.0;

        cameraX += (targetX - cameraX) * 0.1;
        cameraY += (targetY - cameraY) * 0.1;

        double maxX = Math.max(0, map.getWidth() - VIEW_WIDTH);
        double maxY = Math.max(0, map.getHeight() - VIEW_HEIGHT);

        cameraX = Math.clamp(cameraX, 0, maxX);
        cameraY = Math.clamp(cameraY, 0, maxY);
    }

    private void renderMap() {
        gameRenderer.render(map, cameraX, cameraY);
        canvas.requestFocus();
    }

    private void refreshUI() {
        updateHUD();

        if (minimapDirty) {
            drawMinimap();
            minimapDirty = false;
        }
    }

    private void updateHUD() {
        Player player = map.getPlayer();

        int hp = player.getHp();
        int maxHp = player.getMaxHp();

        double hpProgress = maxHp > 0 ? (double) hp / maxHp : 0;
        hpBar.setProgress(Math.clamp(hpProgress, 0, 1));

        hpLabel.setText("HP: " + hp + "/" + maxHp);
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

        if (player.getEquippedWeapon() != null) {
            weaponLabel.setText("Weapon: " + player.getEquippedWeapon().getName());
        } else {
            weaponLabel.setText("Weapon: none");
        }
        weaponLabel.setStyle("-fx-text-fill: #ff9933;");

        if (player.getEquippedShield() != null) {
            shieldLabel.setText("Shield: " + player.getEquippedShield().getName());
        } else {
            shieldLabel.setText("Shield: none");
        }
        shieldLabel.setStyle("-fx-text-fill: #66ccff;");

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
            log("Empty slot.");
            return;
        }

        Item item = player.getInventory().get(index);
        boolean consumed = item.use(player);

        if (consumed) {
            player.getInventory().remove(index);
        }
        if (!consumed) {
            log("Nothing happened.");
        }
    }

    public void nextLevel() {
        if (!session.nextLevel()) {
            log("You reached the end of available maps.");
            return;
        }

        syncFromSession();
        minimapDirty = true;

        resizeCanvas();
        refresh();

        log("Entered level " + session.getCurrentLevel());
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

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Cell cell = map.getCell(x, y);
                if (cell == null) continue;

                if (cell.getType() == WALL) {
                    g.setFill(javafx.scene.paint.Color.DARKGRAY);
                } else {
                    g.setFill(javafx.scene.paint.Color.BLACK);
                }

                g.fillRect(x * scaleX, y * scaleY, scaleX, scaleY);
            }
        }

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

        styleButton(restartBtn);
        styleButton(levelBtn);
        styleButton(saveBtn);
        styleButton(loadBtn);

        closeBtn.setStyle("-fx-background-color: #662222; -fx-text-fill: white;");
        closeBtn.setOnAction(_ -> stage.close());

        restartBtn.setOnAction(_ -> {
            session.restartGame();
            syncFromSession();
            minimapDirty = true;
            refresh();
            log("Game restarted");
        });

        levelBtn.setOnAction(_ -> {
            session.restartLevel();
            syncFromSession();
            minimapDirty = true;
            refresh();
            log("Level restarted");
        });

        saveBtn.setOnAction(_ -> showSaveDialog());
        loadBtn.setOnAction(_ -> showLoadDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(10, title, restartBtn, levelBtn, saveBtn, loadBtn, spacer, closeBtn);
        bar.setPadding(new Insets(5, 10, 5, 10));
        bar.setStyle("-fx-background-color: #222;");

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

    private void showSaveDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Save Game");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-background-color: #222; -fx-padding: 15;");

        for (int i = 1; i <= 5; i++) {
            int slot = i;

            boolean exists = SaveManager.exists(slot);
            SaveData data = exists ? SaveManager.load(slot) : null;

            String label;

            if (data != null) {
                label = "Overwrite " + slot +
                        " (Lv " + data.level +
                        ", HP " + data.player.hp +
                        ", " + SaveManager.formatTime(data.timestamp) + ")";
            } else {
                label = "Save Slot " + slot + " (Empty)";
            }

            Button saveBtn = new Button(label);
            styleButton(saveBtn);

            Button deleteBtn = new Button("X");
            deleteBtn.setStyle("-fx-background-color: #662222; -fx-text-fill: white;");
            deleteBtn.setDisable(!exists);

            saveBtn.setOnAction(_ -> {
                SaveManager.save(map, session.getCurrentLevel(), slot);
                log("Saved to slot " + slot);
                dialog.close();
            });

            deleteBtn.setOnAction(_ -> {
                SaveManager.delete(slot);
                log("Deleted slot " + slot);
                dialog.close();
            });

            layout.getChildren().add(new HBox(10, saveBtn, deleteBtn));
        }

        Scene scene = new Scene(layout, 350, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showLoadDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Load Game");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-background-color: #222; -fx-padding: 15;");

        for (int i = 1; i <= 5; i++) {
            int slot = i;

            SaveData data = SaveManager.exists(slot) ? SaveManager.load(slot) : null;

            String label;

            if (data != null) {
                label = "Slot " + slot +
                        " (Lv " + data.level +
                        ", HP " + data.player.hp +
                        ", " + SaveManager.formatTime(data.timestamp) + ")";
            } else {
                label = "Empty Slot " + slot;
            }

            Button loadBtn = new Button(label);
            styleButton(loadBtn);

            Button deleteBtn = new Button("X");
            deleteBtn.setStyle("-fx-background-color: #662222; -fx-text-fill: white;");
            deleteBtn.setDisable(data == null);

            loadBtn.setOnAction(_ -> {
                if (data == null || !session.load(data)) {
                    log("Empty slot " + slot);
                    return;
                }

                syncFromSession();
                minimapDirty = true;
                refresh();
                log("Loaded slot " + slot);
                dialog.close();
            });

            deleteBtn.setOnAction(_ -> {
                SaveManager.delete(slot);
                log("Deleted slot " + slot);
                dialog.close();
            });

            layout.getChildren().add(new HBox(10, loadBtn, deleteBtn));
        }

        Scene scene = new Scene(layout, 350, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void pauseAndConfirmQuit(Stage owner) {
        paused = true;
        pauseGameTimer();
        updateHelpText();

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Quit Game");

        Label text = new Label("Quit game?");
        text.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");

        styleButton(yesBtn);
        styleButton(noBtn);

        yesBtn.setOnAction(_ -> owner.close());

        noBtn.setOnAction(_ -> {
            dialog.close();
            paused = false;
            resumeGameTimer();
            updateHelpText();
            log("Game resumed");
        });

        HBox buttons = new HBox(10, yesBtn, noBtn);
        VBox layout = new VBox(15, text, buttons);
        layout.setStyle("-fx-background-color: #222; -fx-padding: 20;");

        Scene scene = new Scene(layout, 220, 120);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void updateHelpText() {
        if (helpLabel == null) return;

        helpLabel.setText("""
                Controls:
                Arrows - move
                1-5 - use item
                F5 - quick save
                F9 - quick load
                P - pause
                Q - quit
                
                Status: %s
                """.formatted(paused ? "PAUSED" : "RUNNING"));
    }

    private void syncFromSession() {
        this.map = session.getMap();
        this.engine = session.getEngine();
    }

    private void styleButton(Button btn) {
        String normal = "-fx-background-color: #333; -fx-text-fill: white;";
        String hover = "-fx-background-color: #555; -fx-text-fill: white;";

        btn.setStyle(normal);
        btn.setOnMouseEntered(_ -> btn.setStyle(hover));
        btn.setOnMouseExited(_ -> btn.setStyle(normal));
    }

    public void addLog(String text) {
        if (logArea == null) return;
        logArea.appendText(text + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public static void log(String text) {
        if (instance != null) {
            instance.addLog(text);
        }
    }

    public static void gameOver() {
        if (instance != null) {
            instance.handleGameOver();
        }
    }

    private void handleGameOver() {
        if (gameOver) {
            return;
        }

        gameOver = true;
        paused = true;
        finalSurvivalNano = getSurvivalNano();

        log("Game over.");
        updateHelpText();

        showGameOverScreen();
    }

    private void showGameOverScreen() {
        stopGameLoop();

        Player player = map != null ? map.getPlayer() : null;

        int kills = player != null ? player.getKills() : 0;
        int level = session != null ? session.getCurrentLevel() : 1;
        String survivedTime = formatDuration(finalSurvivalNano);

        Image bgImage = new Image(Objects.requireNonNull(
                getClass().getResource("/images/game_over_background.png")
        ).toExternalForm());

        ImageView background = new ImageView(bgImage);
        background.setFitWidth(900);
        background.setFitHeight(700);
        background.setPreserveRatio(false);

        Label killsValue = createGameOverOverlayValue(String.valueOf(kills));
        Label levelValue = createGameOverOverlayValue(String.valueOf(level));
        Label timeValue = createGameOverOverlayValue(survivedTime);

        // Pozycje pod grafikę 1536x1024 przeskalowaną do 900x700.
        // Możliwe, że trzeba będzie lekko dostroić X/Y po odpaleniu.
        killsValue.setLayoutX(GAME_OVER_KILLS_X);
        killsValue.setLayoutY(GAME_OVER_KILLS_Y);

        levelValue.setLayoutX(GAME_OVER_LEVEL_X);
        levelValue.setLayoutY(GAME_OVER_LEVEL_Y);

        timeValue.setLayoutX(GAME_OVER_TIME_X);
        timeValue.setLayoutY(GAME_OVER_TIME_Y);

        Button loadHotspot = createInvisibleHotspot(70, 585, 245, 70);
        Button menuHotspot = createInvisibleHotspot(330, 585, 245, 70);
        Button quitHotspot = createInvisibleHotspot(590, 585, 245, 70);

        loadHotspot.setOnAction(_ -> {
            gameOver = false;
            paused = false;
            showGameScreen();
            showLoadDialog();
        });

        menuHotspot.setOnAction(_ -> {
            gameOver = false;
            paused = false;
            showTitleScreen();
        });

        quitHotspot.setOnAction(_ -> primaryStage.close());

        Pane overlay = new Pane();
        overlay.setPrefSize(900, 700);
        overlay.getChildren().addAll(
                killsValue,
                levelValue,
                timeValue,
                loadHotspot,
                menuHotspot,
                quitHotspot
        );

        StackPane root = new StackPane(background, overlay);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGameTimer() {
        gameStartNano = System.nanoTime();
        pausedStartedNano = 0;
        totalPausedNano = 0;
        finalSurvivalNano = 0;
    }

    private long getSurvivalNano() {
        if (gameStartNano == 0) {
            return 0;
        }

        long now = System.nanoTime();
        long currentlyPausedNano = pausedStartedNano > 0 ? now - pausedStartedNano : 0;

        return now - gameStartNano - totalPausedNano - currentlyPausedNano;
    }

    private String formatDuration(long nano) {
        long totalSeconds = nano / 1_000_000_000L;

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return "%02d:%02d:%02d".formatted(hours, minutes, seconds);
    }

    private void pauseGameTimer() {
        if (pausedStartedNano == 0) {
            pausedStartedNano = System.nanoTime();
        }
    }

    private void resumeGameTimer() {
        if (pausedStartedNano > 0) {
            totalPausedNano += System.nanoTime() - pausedStartedNano;
            pausedStartedNano = 0;
        }
    }

    private Label createGameOverOverlayValue(String text) {
        Label label = new Label(text);
        label.setStyle("""
        -fx-text-fill: #f5b642;
        -fx-font-size: 20px;
        -fx-font-weight: bold;
        -fx-effect: dropshadow(gaussian, black, 4, 0.8, 2, 2);
        """);
        return label;
    }

    private Button createInvisibleHotspot(double x, double y, double width, double height) {
        Button button = new Button();

        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefSize(width, height);

        button.setStyle("""
        -fx-background-color: transparent;
        -fx-border-color: transparent;
        -fx-cursor: hand;
        """);

        button.setFocusTraversable(false);

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}