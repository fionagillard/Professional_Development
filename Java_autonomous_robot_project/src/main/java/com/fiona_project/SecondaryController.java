package com.fiona_project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class SecondaryController {

    @FXML
    private Canvas canvas;

    @FXML
    private Button startButton;

    @FXML
    private Button stepButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button backButton;

    private Grid grid;
    private Agent agent;
    private Timeline timeline;

    private final int GRID_W = 100;
    private final int GRID_H = 100;

    @FXML
    public void initialize() {
        // create grid and populate demo obstacles/hazards
        grid = new Grid(GRID_W, GRID_H);
        populateDemo(grid);

        // agent from top-left to bottom-right
        agent = new Agent(new Coords(0, 0), new Coords(GRID_W - 1, GRID_H - 1));
        // ensure a valid path exists (retry a few times if random obstacles block it)
        int tries = 0;
        agent.computePath(grid);
        while ((agent.getPath() == null || agent.getPath().isEmpty()) && tries < 6) {
            populateDemo(grid);
            agent.computePath(grid);
            tries++;
        }

        draw();

        timeline = new Timeline(new KeyFrame(Duration.millis(160), e -> {
            boolean moved = agent.step();
            draw();
            if (!moved) {
                timeline.stop();
                startButton.setText("Start");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        // auto-start simulation
        timeline.play();
        startButton.setText("Pause");
    }

    private void populateDemo(Grid g) {
        // simple random-ish pattern for obstacles and hazards
        for (int y = 0; y < g.getHeight(); y++) {
            for (int x = 0; x < g.getWidth(); x++) {
                Cell c = g.getCell(x, y);
                // create border obstacles
                if (x == 0 || y == 0 || x == g.getWidth() - 1 || y == g.getHeight() - 1) {
                    c.obstacle = false;
                }
                // some fixed obstacles
                if ((x % 7 == 0) && y > 2 && y < g.getHeight() - 3) {
                    c.obstacle = (Math.random() > 0.6);
                }
                // terrain cost small
                c.terrainCost = (int) (Math.random() * 2);
                // hazard cost 0..4
                c.hazardCost = (int) (Math.random() * 5);
            }
        }
        // clear start and goal
        g.getCell(0, 0).obstacle = false; g.getCell(0, 0).hazardCost = 0;
        g.getCell(g.getWidth()-1, g.getHeight()-1).obstacle = false; g.getCell(g.getWidth()-1, g.getHeight()-1).hazardCost = 0;
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double cw = canvas.getWidth();
        double ch = canvas.getHeight();
        int w = grid.getWidth();
        int h = grid.getHeight();
        double cellW = cw / w;
        double cellH = ch / h;

        // background
        gc.setFill(Color.web("#222"));
        gc.fillRect(0, 0, cw, ch);

        // find max hazard for normalization
        int maxHaz = 1;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) maxHaz = Math.max(maxHaz, grid.getCell(x,y).hazardCost);

        // draw cells
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Cell c = grid.getCell(x, y);
                double px = x * cellW;
                double py = y * cellH;
                if (c.obstacle) {
                    gc.setFill(Color.DARKGRAY);
                } else {
                    // Discrete hazard levels: 0=safe,1=low,2=medium,3=high,4=critical
                    Color baseColor;
                    int level = Math.max(0, Math.min(4, c.hazardCost));
                    switch (level) {
                        case 0: baseColor = Color.web("#E8F5E9"); break; // safe
                        case 1: baseColor = Color.web("#FFF9C4"); break; // low
                        case 2: baseColor = Color.web("#FFCC80"); break; // medium
                        case 3: baseColor = Color.web("#FF8A80"); break; // high
                        case 4: default: baseColor = Color.web("#B71C1C"); break; // critical
                    }
                    // slightly darken based on terrain cost
                    double terrainDarken = Math.min(0.5, c.terrainCost * 0.08);
                    Color shaded = baseColor.interpolate(Color.BLACK, terrainDarken);
                    gc.setFill(shaded);
                }
                gc.fillRect(px, py, cellW, cellH);
            }
        }

        // draw path
        List<Coords> path = agent.getPath();
        if (path != null) {
            gc.setFill(Color.YELLOW);
            for (Coords p : path) {
                gc.fillRect(p.x * cellW, p.y * cellH, cellW, cellH);
            }
        }

        // draw goal
        Coords goal = agent.getEnd();
        gc.setFill(Color.LIME);
        gc.fillOval(goal.x * cellW + cellW*0.2, goal.y * cellH + cellH*0.2, cellW*0.6, cellH*0.6);

        // draw agent (high-contrast color with outline)
        Coords cur = agent.getCurrent();
        double pad = 0.05; // small padding so agent circle nearly fills the cell
        double ax = cur.x * cellW + cellW * pad;
        double ay = cur.y * cellH + cellH * pad;
        double aw = cellW * (1 - 2 * pad);
        double ah = cellH * (1 - 2 * pad);
        gc.setFill(Color.web("#00FFFF")); // bright cyan stands out against reds/greens/yellows
        gc.fillOval(ax, ay, aw, ah);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(Math.max(1.0, Math.min(cellW, cellH) * 0.04));
        gc.strokeOval(ax, ay, aw, ah);

        // grid lines
        gc.setStroke(Color.rgb(40,40,40,0.6));
        gc.setLineWidth(0.5);
        for (int i = 0; i <= w; i++) gc.strokeLine(i*cellW,0,i*cellW,ch);
        for (int i = 0; i <= h; i++) gc.strokeLine(0,i*cellH,cw,i*cellH);
    }

    @FXML
    private void onStart() {
        if (timeline == null) return;
        if (timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.stop();
            startButton.setText("Start");
        } else {
            timeline.play();
            startButton.setText("Pause");
        }
    }

    @FXML
    private void onStep() {
        agent.step();
        draw();
    }

    @FXML
    private void onReset() {
        if (timeline != null) timeline.stop();
        startButton.setText("Start");
        populateDemo(grid);
        agent = new Agent(new Coords(0,0), new Coords(grid.getWidth()-1, grid.getHeight()-1));
        agent.computePath(grid);
        draw();
    }

    @FXML
    private void switchToPrimary() throws IOException {
        if (timeline != null) timeline.stop();
        App.setRoot("primary");
    }
}