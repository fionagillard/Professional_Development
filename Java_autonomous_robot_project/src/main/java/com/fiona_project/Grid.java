package com.fiona_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Grid {
    private int width;
    private int height;
    private Cell[][] cells;

    public Grid(int width, int height){
        this.width = width;
        this.height = height;
        this.cells = new Cell[height][width];
        initializeCells();
    }

    private void initializeCells() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cells[i][j] = new Cell(new Coords(j, i));
            }
        }
    }

    public Cell getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height){
            return cells[y][x];
        }
        
        throw new IndexOutOfBoundsException("Cell coordinates are out of bounds.");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // A* pathfinding on the grid using 8-directional movement.
    // Considers `Cell.obstacle` as impassable and weights move cost by `terrainCost + hazardCost`.
    // Returns a list of Coords from start (inclusive) to goal (inclusive), or empty list if no path.
    public List<Coords> findPathAStar(Coords start, Coords goal) {
        List<Coords> empty = new ArrayList<>();
        if (start == null || goal == null) return empty;
        if (!inBounds(start) || !inBounds(goal)) return empty;

        Cell startCell = getCell(start.x, start.y);
        Cell goalCell = getCell(goal.x, goal.y);
        if (startCell.obstacle || goalCell.obstacle) return empty;

        // Hazard influence multiplier (tunable)
        final double HAZARD_WEIGHT = 1.0;

        // For strict admissibility use a safe lower bound for per-step cost.
        // The absolute minimum cost for a single move is moving one cell orthogonally with zero hazard and zero terrain.
        final double minStepCost = 1.0;

        Map<Coords, Double> gScore = new HashMap<>();
        Map<Coords, Coords> cameFrom = new HashMap<>();
        Set<Coords> closed = new HashSet<>();

        // Track best known f-value for nodes in the open queue to reduce stale duplicates
        Map<Coords, Double> openBestF = new HashMap<>();

        PriorityQueue<Node> open = new PriorityQueue<>((a, b) -> Double.compare(a.f, b.f));

        gScore.put(start, 0.0);
        double startF = heuristic(start, goal, minStepCost);
        open.add(new Node(start, startF));
        openBestF.put(start, startF);

        while (!open.isEmpty()) {
            Node current = open.poll();
            Coords cur = current.coords;

            // Skip stale queue entries: ensure this popped f matches the best-known f for this coord
            Double bestF = openBestF.get(cur);
            if (bestF == null || Double.compare(current.f, bestF) != 0) continue;
            // If already closed, skip
            if (closed.contains(cur)) continue;
            closed.add(cur);
            openBestF.remove(cur);

            if (cur.equals(goal)) {
                return reconstructPath(cameFrom, cur);
            }

            for (Coords neighbor : neighbors(cur)) {
                Cell nCell = getCell(neighbor.x, neighbor.y);
                if (nCell.obstacle) continue; // skip impassable cells

                int dx = neighbor.x - cur.x;
                int dy = neighbor.y - cur.y;
                double base = (Math.abs(dx) == 1 && Math.abs(dy) == 1) ? Math.sqrt(2) : 1.0;

                // Conservative corner cutting prevention: don't allow diagonal if either adjacent orthogonal is blocked
                if (Math.abs(dx) == 1 && Math.abs(dy) == 1) {
                    Cell c1 = getCell(cur.x + dx, cur.y);
                    Cell c2 = getCell(cur.x, cur.y + dy);
                    if (c1.obstacle || c2.obstacle) continue;
                }

                double hazard = Math.max(0, nCell.hazardCost);
                double terrain = Math.max(0, nCell.terrainCost);

                // Exposure scales with distance moved and hazard intensity.
                // Keep base movement cost separate so exposure represents additional risk per unit distance.
                double exposureCost = base * HAZARD_WEIGHT * hazard; // proportional to distance and hazard

                // Total move cost: base movement cost + exposure + terrain penalty
                double moveCost = base + exposureCost + terrain;

                double tentativeG = gScore.getOrDefault(cur, Double.POSITIVE_INFINITY) + moveCost;

                if (tentativeG < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, cur);
                    gScore.put(neighbor, tentativeG);
                    double f = tentativeG + heuristic(neighbor, goal, minStepCost);
                    open.add(new Node(neighbor, f));
                    openBestF.put(neighbor, f);
                }
            }
        }

        return empty; // no path
    }

    private boolean inBounds(Coords c) {
        return c.x >= 0 && c.x < width && c.y >= 0 && c.y < height;
    }

    private List<Coords> neighbors(Coords c) {
        List<Coords> list = new ArrayList<>();
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int i = 0; i < 8; i++) {
            int nx = c.x + dx[i];
            int ny = c.y + dy[i];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                list.add(new Coords(nx, ny));
            }
        }
        return list;
    }

    private double heuristic(Coords a, Coords b, double minStepCost) {
        // Scaled Euclidean: distance * minimal per-step cost (keeps heuristic admissible)
        double dist = Math.hypot(b.x - a.x, b.y - a.y);
        return dist * minStepCost;
    }

    private List<Coords> reconstructPath(Map<Coords, Coords> cameFrom, Coords current) {
        List<Coords> path = new ArrayList<>();
        Coords cur = current;
        while (cur != null) {
            path.add(0, cur);
            cur = cameFrom.get(cur);
        }
        return path;
    }

    private static class Node {
        final Coords coords;
        final double f;

        Node(Coords coords, double f) {
            this.coords = coords;
            this.f = f;
        }
    }
}
