package bio2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Node implements Comparable<Node>, Serializable {
        // undirected
        public ArrayList<Node> nbrs = new ArrayList<Node>();
        public ArrayList<Edge> edges = new ArrayList<Edge>();
        

        // directed
        public ArrayList<Node> ingoing = new ArrayList<Node>();
        public ArrayList<Node> outgoing= new ArrayList<Node>();
        
        public ArrayList<Edge> e_ins = new ArrayList<Edge>();
        public ArrayList<Edge> e_outs = new ArrayList<Edge>(); 
        
        public String label;
        public int numlabel;
        public final int index;
        
        public float age;
        
        // parsimony
        
        public Node daughter=null;
        public Node son=null;
        public Node cousin = null;
        public Node parent = null;
        public int tag=-1;
        public String character;
        public HashMap<String,Integer> s = new HashMap<>();
        
        public Node() {
            this.index = -1;
            this.label = "";
            this.numlabel = -1;
        }
        
	public Node(int index){
	// initiation of instance
            this.index = index;
            this.label = "";
            this.numlabel = -1;
	}

        
        
	public void print(){
	// print the node's properties
	
	}
        
        public int compareTo(Node n) {
            if (this.numlabel>n.numlabel) {
                return 1;
            }
            
            else if (this.numlabel==n.numlabel) {
                return 0;
            }
            
            else if (this.numlabel<n.numlabel) {
                return -1;
            }
            return -1;
        }
        
        public boolean isLeaf(){
            if (this.son==null && this.daughter==null) {
                return true;
            } else {
                return false;
            }
        }
        
        public int hamming(Node n) {
            int ham = 0;
            for(int i=0; i<this.label.length(); i++){
                char a = this.label.charAt(i);
                char b = n.label.charAt(i);
                if (a!=b) {
                    ham += 1;
                }
            }
            return ham;
        }
        
        @Override public String toString(){
            String name="";
            if(!"".equals(this.label)){
                name+= this.label;
            } else {
                name+= this.index;
            }
            
            if(this.numlabel!=-1) name+="("+this.numlabel+")";
            
            return name;
        }
	
}