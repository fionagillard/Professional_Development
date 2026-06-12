package com.fiona_project;

import java.util.ArrayList;

public class Graph{
    private int V; // number of vertices
    private int E; // number of edges
    private ArrayList<ArrayList<Integer>> adj; // adjacency lists

    public Graph(int V) {
        this.V = V;
        this.E = 0;
        adj = new ArrayList<>();
        for(int i = 0; i < V; i++){
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int v, int w){
        adj.get(w).add(v);
        adj.get(v).add(w);
        E++;
    }

    public Iterable<Integer> adj(int v){
        return adj.get(v);
    }

    public int V(){
        return V;
    }

    public int E(){
        return E;
    }

}
