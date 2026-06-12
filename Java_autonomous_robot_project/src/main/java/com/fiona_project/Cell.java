package com.fiona_project;

public class Cell {
    Coords coords;
    boolean obstacle;
    int terrainCost;
    int hazardCost;

    public Cell() {
        this.coords = null;
        this.obstacle = false;
        this.terrainCost = 0;
        this.hazardCost = 0;
    }

    public Cell(Coords coords) {
        this.coords = coords;
        this.obstacle = false;
        this.terrainCost = 0;
        this.hazardCost = 0;
    }
}
