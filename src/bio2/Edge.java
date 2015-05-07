package bio2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Edge implements Comparable<Edge>, Serializable{
    
    public final int index;
    public final boolean directed;
    public String label;
    
    
    //undirected
    public ArrayList<Node> nodes = new ArrayList<Node>();
	
	//directed
    public Node src;
    public Node dst;
    
    //weighted
    public int weight = -1;
    public float fweight = -1f;
    
    
    // For Trie
    
    // For SuffixTree
    public int position=-1;
    public String edgeStr = "";
    public int len=0;
    
    public Edge(int ind, boolean d){
	// new instance
        this.label = "";
        this.index = ind;
	this.directed = d;
        
        if (!d) {
            this.nodes = new ArrayList<Node>();
        }
        
    }
    
    public int compareTo(Edge e) {
        return this.nodes.get(0).compareTo(e.nodes.get(0));
    }
    
    @Override public String toString(){
        String name = "";
        name += nodes.get(0).toString() + "<->" + nodes.get(1).toString()+":"+this.weight;
        
        
        
        return name;
        
    }
	
}