/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author USER1
 */
@SuppressWarnings("serial")
public class UnrootedPhyloTree extends PhyloTree implements Serializable{
    
    public Node root = null;
    
    public UnrootedPhyloTree() {
        this.root = super.root;
    }
    
    @Override
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
    
    @Override
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
