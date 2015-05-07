/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author USER1
 */
public class SuffixTree extends Graph {
    
    public final boolean directed;
    public Node root;
    public HashMap<Node, ArrayDeque<Edge>> pathMap;
    public HashMap<Node, Node> branchParent;
    
    public SuffixTree(boolean d) {
        super(d);
        this.directed=d;
        Node r = this.MakeNewNode();
        this.root = r;
    }
    
    public SuffixTree BuildOnPattern(String pattern) {
        
        
        return this;
    }
    
    public void EatRawEdge(Edge e) {
        if (!this.edges.contains(e)) this.edges.add(e);
        if (!this.nodes.contains(e.src)) this.nodes.add(e.src);
        if (!this.nodes.contains(e.dst)) this.nodes.add(e.dst);
    }
    
    public SuffixTree EatTrie(Trie T){
        
        // assume formless
        this.nodes.clear();
        this.edges.clear();
        this.root = null;
        
        ArrayDeque<Edge> currentPath;
        ArrayList<ArrayDeque<Edge>> paths;
        ArrayList<Edge> v_branch;
        Iterator<Edge> edgeIt;
        Node n;
        
         
        this.pathMap = new HashMap<Node, ArrayDeque<Edge>>();
        
        // find non-branching paths
        // perform dfs
        n = T.root;
        
        ArrayDeque<Node> Q = new ArrayDeque<Node>();
        Q.addFirst(n);
        
        //record all branching nodes and leaves
        currentPath = new ArrayDeque<Edge>(); 
        paths = new ArrayList<ArrayDeque<Edge>>();
        Set<Node> bNodes = new HashSet<Node>();
        
        while(Q.size()>0){
            Node v = Q.removeFirst();
            v_branch = v.e_outs;
           if (v_branch.size()>1 || v_branch.size()<=0) {
                bNodes.add(v);
            }
            // branching & non-branching node all go through
            edgeIt = v_branch.iterator();
            while (edgeIt.hasNext()) {
                Edge e = edgeIt.next();
                Node w = e.dst;
                Q.addFirst(w);
                
            }
        }
        System.out.println(bNodes.toString());
        // traverse parents to find out bNodes's path reversal
        Iterator<Node> bNodeIt = bNodes.iterator();
        while(bNodeIt.hasNext()) {
            ArrayDeque<Node> R = new ArrayDeque<>();
            ArrayDeque<Edge> path = new ArrayDeque<Edge>();
            Node bNode = bNodeIt.next();
            R.addFirst(bNode);
            while (R.size()>0) {
                Node w = R.removeFirst();
                if (w==T.root) {
                    this.root = w;
                    break;
                }
                Edge edge_to_parent = w.e_ins.get(0);
                path.addFirst(edge_to_parent);
                Node parent = edge_to_parent.src;
                if (!bNodes.contains(parent)) R.addFirst(edge_to_parent.src);
            }
            this.pathMap.put(bNode, new ArrayDeque<>(path));
        }
        
        
        
        bNodeIt = bNodes.iterator();
        while(bNodeIt.hasNext()) {
            Node bNode = bNodeIt.next();
            if (!bNode.equals(this.root)){
                ArrayDeque<Edge> nbPath = this.pathMap.get(bNode);

                // hope the fetched nbPath is correct...
                
                Edge fEdge = nbPath.getFirst();
                Edge lEdge = nbPath.getLast();

                Node nSrc = fEdge.src;
                Node nDst = lEdge.dst;
                
                if (!this.nodes.contains(nSrc)) this.CherryPickNode(nSrc);
                if (!this.nodes.contains(nDst)) this.CherryPickNode(nDst);

                Edge nEdge = this.MakeNewEdge();
                nEdge.position = fEdge.position;
                nEdge.len = nbPath.size();
                this.d_connect(nEdge, nSrc, nDst);

                //nEdge.label=edgeLabel;


            }        
        }
            
        return this;
    }
}
    

