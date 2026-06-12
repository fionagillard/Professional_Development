package com.fiona_project;

public class Agent{
    private Coords start, current, end;
    private java.util.List<Coords> path;
    private int pathIndex = 0;

    // find path with the lowest amount of radiation
    public Agent(Coords start, Coords end){
        this.start = start;
        this.current = start;
        this.end = end;
        this.path = new java.util.ArrayList<>();
        this.pathIndex = 0;
    }

    public Coords getCurrent() {
        return current;
    }

    public Coords getEnd() {
        return end;
    }

    public java.util.List<Coords> getPath() {
        return path;
    }

    public void computePath(Grid grid) {
        if (grid == null) return;
        this.path = grid.findPathAStar(start, end);
        this.pathIndex = 0;
        if (!path.isEmpty()) {
            // ensure current is at start
            this.current = path.get(0);
        }
    }

    // advance one step along computed path; returns true if moved
    public boolean step() {
        if (path == null || pathIndex >= path.size() - 1) return false;
        pathIndex++;
        current = path.get(pathIndex);
        return true;
    }

    public void resetToStart() {
        this.pathIndex = 0;
        if (this.path != null && !this.path.isEmpty()) {
            this.current = this.path.get(0);
        } else {
            this.current = this.start;
        }
    }
}
