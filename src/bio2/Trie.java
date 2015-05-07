/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author USER1
 */
public class Trie extends Graph {
    public Node root;
    
    public Trie(boolean d) {
        super(d);
        Node r = this.MakeNewNode();
        this.root = r;
    }
    
    public ArrayList<Node> getLeaves(){
        ArrayList<Node> leaves = new ArrayList<Node>();
        Iterator<Node> nodeIt = this.nodes.iterator();
        Node n;
        while(nodeIt.hasNext()){
            n = nodeIt.next();
            if(n.e_outs.size()<=0) leaves.add(n);
        }
        return leaves;
    }
    
    public Trie BuildOnPatterns(ArrayList<String> patterns){
    // returns itself
        //for each pattern, trace trie
        //if new input found, branch off
        
    // NOTE TO MYSELF:
        // Single Letters Are Strings For Simplicity's Sake
        
        
        // iterate through all patterns
        for (String pattern : patterns) {
            Node currentNode = this.root;
            // iterate through all characters
            for(int i=0; i<pattern.length(); i++){
                String currentSymbol = ""+pattern.charAt(i);
                // check if there's edge from that node with same symbol
                Edge traceableEdge=null;
                Iterator<Edge> EdgeIt;
                EdgeIt = currentNode.e_outs.iterator();
                while (EdgeIt.hasNext()) {
                    Edge e = EdgeIt.next();
                    if(e.label.equals(currentSymbol)) {
                        traceableEdge = e;
                        break;
                    }
                }
                
                // then decide on the trie formation
                if(traceableEdge!=null) {
                    currentNode = traceableEdge.dst;
                    
                } else {
                    // connect new node
                    Node newNode = this.MakeNewNode();
                    Edge newEdge = this.MakeNewEdge();
                    newEdge.label = currentSymbol;
                    this.d_connect(newEdge, currentNode, newNode);
                    currentNode = newNode;
                }
                
            }
        }
        
        return this;
    }
    
    public Trie BuildOnPatterns2(ArrayList<String> patterns){
    // Make Trie with label for each node so SuffixTree can eat it.
    // NOTE: gonna need 2 pointers...
        Node currentNode;
        
        // pointers
        int i;
        i=0;
        int j;
        j=0;
        
        // variables
        String str;
        Iterator<Edge> edgeIt;
        Edge e;
        Node n;
        boolean hasTraceableEdge;
        String pattern;
        
        if(this.nodes.size()>1) {
            System.out.println("Wrong Trie! Need to have only root.");
            return null;
        }
        
        // first iterate through patterns
        for(i=0;i<patterns.size();i++){
            pattern = patterns.get(i);
            currentNode = this.root;
            for(j=0;j<pattern.length();j++){
                // for each character, see if an outgoing edge has matching label
                str = ""+pattern.charAt(j);
                edgeIt = currentNode.e_outs.iterator();
                hasTraceableEdge = false;
                
                while(edgeIt.hasNext()){
                    //for each edge, check label
                    e = edgeIt.next();
                    
                    if(str.equals(e.label)){
                        hasTraceableEdge = true;
                        currentNode = e.dst;
                        break;
                    }
                }
                    
                if (!hasTraceableEdge){
                    // make a new node and give label & position
                    n = this.MakeNewNode();
                    e = this.MakeNewEdge();
                    this.d_connect(e, currentNode, n);
                    e.label = str;
                    e.position = j+i;
                    currentNode = n;           
                }
                
                if (j==pattern.length()-1) currentNode.numlabel=i;
                    
            }
                
                
        }
        return this;
    }
}
