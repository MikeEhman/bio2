/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author USER1
 */
class PhyloTree extends Graph {

    public Node root;
    
    public ArrayList<Node> leaves = new ArrayList<>();
    
    public PhyloTree() {
    
    }
    
    public PhyloTree(boolean d) {
        super(d);
    }
    
    public void updateEdgeLength() {
        for(int i=0; i<this.edges.size(); i++){
            Edge curEdge = this.edges.get(i);
            curEdge.fweight = Math.abs(curEdge.nodes.get(0).age - curEdge.nodes.get(1).age);
        }
    }
    
    public float newClusterDistance(Cluster c1, Cluster c2, Cluster cm, HashMap<Integer,HashMap<Integer,Float>> DMat){
        float dist = DMat.get(c1.head.numlabel).get(cm.head.numlabel) * c1.leaves.size();
        dist += DMat.get(c2.head.numlabel).get(cm.head.numlabel) * c2.leaves.size();
        dist = dist / (c1.leaves.size() + c2.leaves.size());
        return dist;
    
    }
    
    public ArrayList<Node> topoSortedNodes () {
        // BFS-based reverse-toposort
        ArrayList<Node> nodeList = new ArrayList<>();
        ArrayDeque<Node> TN = new ArrayDeque<>();
        TN.addFirst(this.root);
        Set<Node> visited = new HashSet<>();
        while (TN.size()>0) {
            Node cN = TN.removeFirst();
            nodeList.add(cN);
            if (!cN.isLeaf() && !visited.contains(cN)){
                visited.add(cN);
                TN.addLast(cN.son);
                TN.addLast(cN.daughter);
            }
        }
        
        Collections.reverse(nodeList);
        return nodeList;
        
    }
    
    public ArrayList<Node> getRipeNodes() {
        ArrayList<Node> topo = this.topoSortedNodes();
        ArrayList<Node> list = new ArrayList<>();
        for(int i=0; i<topo.size(); i++){
            Node curNode = topo.get(i);
            if (curNode.tag==0) {
                list.add(curNode);
            }
        }
        return list;
    }
    
}
