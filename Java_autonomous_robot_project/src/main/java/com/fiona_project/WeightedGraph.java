package com.fiona_project;

public class WeightedGraph extends Graph {
    private int[][] weights; // adjacency matrix for weights

    public WeightedGraph(int V) {
        super(V);
        weights = new int[V][V];
    }

    public void addEdge(int v, int w, int weight) {
        super.addEdge(v, w);
        weights[v][w] = weight;
        weights[w][v] = weight; // assuming undirected graph
    }

    public int getWeight(int v, int w) {
        return weights[v][w];
    }
}
