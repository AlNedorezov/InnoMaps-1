package com.innopolis.maps.innomaps.app;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.Graph;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Wrapper for JGraphT library. Creating graphs, adding vertices and edges, searching for
 * the shortest paths and so on.
 */
public class JGraphTWrapper {
    private SimpleGraph<LatLng, LatLngGraphEdge> graph;

    public JGraphTWrapper() {
        graph = new SimpleGraph<>(LatLngGraphEdge.class);
    }

    /**
     * Adds new vertex.
     * @param v - vertex to add
     */
    public void addVertex(LatLng v) {
        graph.addVertex(v);
    }

    /**
     * Adds new edge of given type.
     * @param v1 - vertex edge begins
     * @param v2 - vertex edge ends
     * @param edgeType - edge type (see LatLngGraphEdge.EdgeType)
     */
    public void addEdge(LatLng v1, LatLng v2, LatLngGraphEdge.EdgeType edgeType) {
        graph.addEdge(v1, v2, new LatLngGraphEdge(edgeType));
    }

    /**
     * Shortest path using all edges.
     * @param v1 - start LatLng
     * @param v2 - end LatLng
     * @return sequential list of LatLng objects
     */
    public ArrayList<LatLng> shortestPath(LatLng v1, LatLng v2) {
        return shortestPathForGraph(v1, v2, graph);
    }

    private ArrayList<LatLng> shortestPathForGraph(LatLng v1, LatLng v2, Graph<LatLng, LatLngGraphEdge> g) {
        List<LatLngGraphEdge> foundPath = DijkstraShortestPath.findPathBetween(g, v1, v2);
        ArrayList<LatLng> pointsList = new ArrayList<>();
        for (LatLngGraphEdge edge :foundPath) {
            pointsList.add((edge).getV1());
        }
        pointsList.add(((foundPath.get(foundPath.size() - 1))).getV2());
        return pointsList;
    }

    /**
     * Shortest path with only default edges.
     * @param v1 - start LatLng
     * @param v2 - end LatLng
     * @return sequential list of LatLng objects
     */
    public ArrayList<LatLng> defaultShortestPath(LatLng v1, LatLng v2) {
        Set<LatLngGraphEdge> oldEdges = graph.edgeSet();
        Set<LatLngGraphEdge> defaultEdges = new HashSet<>();
        Iterator<LatLngGraphEdge> iteratorOld = oldEdges.iterator();
        while (iteratorOld.hasNext()) {
            LatLngGraphEdge edge = iteratorOld.next();
            if (edge.getEdgeType() == LatLngGraphEdge.EdgeType.DEFAULT) {
                defaultEdges.add(edge);
            }
        }
        UndirectedSubgraph<LatLng, LatLngGraphEdge> defaultEdgesGraph = new UndirectedSubgraph<>(graph, null, defaultEdges);
        return shortestPathForGraph(v1, v2, defaultEdgesGraph);
    }
}