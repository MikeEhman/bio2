package bio2;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Graph implements Serializable{
    
    public final boolean directed;
    
    public ArrayList<Node> nodes = new ArrayList<Node>();
    public ArrayList<Edge> edges = new ArrayList<Edge>();
    public int NodeIndex = 0;
    public int EdgeIndex = 0;
    
    public HashMap<Integer, Node> numNodeMap = new HashMap<>();
    public HashMap<String, Node> strNodeMap = new HashMap<>();
    
    public Graph() {
        this.directed = false;
    }
    
    public Graph(boolean d){
        this.directed = d;
    }
    
    public Node MakeNewNode(){
        Node newNode = new Node(NodeIndex++);
        this.nodes.add(newNode);
        return newNode;
    }
    
    public void node_set_numlabel(Node n, int i) {
            n.numlabel = i;
            this.numNodeMap.put(i,n);
    }
    
    public void set_strlabel(Node n, String str) {
            n.label = str;
            this.strNodeMap.put(str,n);
    }
    
    public Edge MakeNewEdge(){
        Edge newEdge = new Edge(EdgeIndex++, this.directed);
        this.edges.add(newEdge);
        return newEdge;
    }
    
    public Node AddNode(Node n){
        this.nodes.add(n);
        return n;
    }
    
    public Node CherryPickNode(Node n){
        this.nodes.add(n);
        n.e_ins.clear();
        n.e_outs.clear();
        n.ingoing.clear();
        n.outgoing.clear();
        return n;
    }
    
    public Edge AddEdge(Edge e){
        this.edges.add(e);
        return e;
    }
    
    public void d_connect(Edge e, Node n, Node m){
        // directed graph connect
        e.src = n;
        e.dst = m;
        
        n.outgoing.add(m);
        m.ingoing.add(n);
        n.e_outs.add(e);
        m.e_ins.add(e);
    }
    
    public void d_biconnect(Edge e, Node n, Node m){
        // directed graph connect
        d_connect(e,n,m);
        Edge ee = this.MakeNewEdge();
        ee.weight = e.weight;
        d_connect(ee,m,n);
    }
    
    public void d_disconnect(Edge e, Node n, Node m) {
        this.edges.remove(e);
        e = null;
        n.outgoing.remove(m);
        m.ingoing.remove(n);
        n.e_outs.remove(e);
        m.e_ins.remove(e);
    }
    
    public void PrintRaw(){
        System.out.println("Nodes: " + nodes.toString());
        System.out.println("Edges: " + edges.toString());
    }
    
    public void PrintAdjList(){
        // assume directed for now
        for(int i = 0; i < nodes.size(); i++){
            Node n = nodes.get(i);
            for (int j=0 ; j<n.outgoing.size(); j++) {
                Node m = n.outgoing.get(j);
                System.out.println(n.label + " -> " + m.label);
            }
        }
    }
    
    public void u_connect(Edge e, Node n, Node m){
    
        // undirected graph connect
        e.nodes.add(n);
        e.nodes.add(m);
        n.edges.add(e);
        n.nbrs.add(m);
        m.edges.add(e);
        m.nbrs.add(n);
        
        if (!this.edges.contains(e)) {
            this.edges.add(e);
        }
    }
    
    public void u_disconnect(Edge e, Node n, Node m) {
        this.edges.remove(e);
        e.nodes.remove(n);
        e.nodes.remove(m);
        n.nbrs.remove(m);
        n.edges.remove(e);
        m.nbrs.remove(n);
        m.edges.remove(e);
    }
    
    public Edge getEdge (Node n, Node m) {
        for(int i=0; i<n.e_outs.size(); i++){
            Edge e = n.e_outs.get(i);
            if (e.nodes.contains(m)){
                return e;
            }
            if (e.src.equals(n) && e.dst.equals(m)) {
                return e;
            }
        }
        return null;
    }
    
    public static ArrayList<Edge> d_getPath(Node n, Node m) {
        // DFS to search for path connecting n and m
        HashMap<Node,ArrayList<Edge>> pathMap = new HashMap<>();
        ArrayDeque<Node> Q = new ArrayDeque<>();
        Set<Node> visited = new HashSet<>();
        Q.add(n);
        visited.add(n);
        pathMap.put(n,new ArrayList<>());
        while (Q.size()>0) {
            Node v = Q.removeFirst();
            visited.add(v);
            
            Iterator<Edge> edgeIt = v.e_outs.iterator();
            while (edgeIt.hasNext()) {
                Edge vw = edgeIt.next();
                Node w = vw.dst;
                if (!visited.contains(w)) {
                    Q.addFirst(w);
                    ArrayList<Edge> oldPath = new ArrayList<>(pathMap.get(v));
                    oldPath.add(vw);
                    pathMap.put(w, oldPath);
                }
                if (w.equals(m)) {
                    return pathMap.get(w);
                }
            }
        }
        return null;
    }
    
    public static ArrayList<Edge> u_getPath(Node n, Node m) {
        // DFS to search for path connecting n and m
        HashMap<Node,ArrayList<Edge>> pathMap = new HashMap<>();
        ArrayDeque<Node> Q = new ArrayDeque<>();
        Set<Node> visited = new HashSet<>();
        Q.add(n);
        visited.add(n);
        pathMap.put(n,new ArrayList<>());
        while (Q.size()>0) {
            Node v = Q.removeFirst();
            visited.add(v);
            for(int i=0; i<v.edges.size(); i++){
                Edge curEdge = v.edges.get(i);
                for(int j=0; j<curEdge.nodes.size(); j++){
                    Node w = curEdge.nodes.get(j);
                    if (!v.equals(w) && !visited.contains(w)) {
                        Q.addFirst(w);
                        ArrayList<Edge> oldPath = new ArrayList<>(pathMap.get(v));
                        oldPath.add(curEdge);
                        pathMap.put(w, oldPath);
                    }
                }
            }
            if (v.equals(m)) {
                return pathMap.get(v);
            }
        }
        return null;
    }
    
}