package de.htwberlin.f4.ai.ma.indoor_graph;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

public class EdgeImplementation implements Edge{

    private int id;
    private String nodeA;
    private String nodeB;
    private boolean accessibly;


    public EdgeImplementation(int id, String nodeA, String nodeB, boolean accessibly) {
        this.id = id;
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.accessibly = accessibly;
    }

    public EdgeImplementation() {}


    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public String getNodeA() {
        return this.nodeA;
    }

    @Override
    public void setNodeA(String nodeA) {
        this.nodeA = nodeA;
    }

    @Override
    public String getNodeB() {
        return this.nodeB;
    }

    @Override
    public void setNodeB(String nodeB) {
        this.nodeB = nodeB;
    }

    @Override
    public boolean getAccessibly() {
        return this.accessibly;
    }

    @Override
    public void setAccessibly(boolean accessibly) {
        this.accessibly = accessibly;
    }


}
