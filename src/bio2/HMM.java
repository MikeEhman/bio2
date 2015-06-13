/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author nam
 */
public class HMM {
    
    public HashMap<String,HashMap<String,Double>> transition;
    public HashMap<String,HashMap<String,Double>> emission;
    public Graph diagram;
    public ArrayList<Integer> underCut = new ArrayList<Integer>();
    public HashMap<String,HashMap<String,Integer>> spell = new HashMap<>();
    
    public HMM(ArrayList<String> alignment, double threshold, ArrayList<String> sigma) {
        
        int alen = alignment.get(0).length();
        
        HashMap<String,HashMap<String,Double>> T = new HashMap<>();
        HashMap<String,HashMap<String,Double>> E = new HashMap<>();
        HashMap<String,ArrayList<Double>> P = new HashMap<>();
        
        for (String alph : sigma) {
            P.put(alph, new ArrayList<Double>());
        }
        
        for (int i=0 ; i<alen ; i++) {

            // initialize a count map
            HashMap<String,Integer> count = new HashMap<>();
            for (String alph : sigma) {
                count.put(alph, 0);
            }

            // count
            for (String aStr : alignment) {
                String curStr = ""+aStr.charAt(i);
                int incremented = count.get(curStr)+1;
                count.put(curStr,incremented);
            }
            
            // divide by total
            for (String alph : sigma) {
                double num = (double)count.get(alph) / alignment.size();
                P.get(alph).add(num);
            }
            
        }
        
        ArrayList<Integer> remIndices = new ArrayList<>();
        for (int i=0; i<alen; i++) {
            double frac = P.get("-").get(i);
            if (frac > threshold) {
                remIndices.add(i);
            }
        }
        
        this.underCut = remIndices;
        
        // there we have our Profile.
        
        int newLength = P.get(sigma.get(0)).size() - remIndices.size();
        
        // making it a state machine
        Graph G = new Graph(true);
        Node S = G.MakeNewNode();
        G.set_strlabel(S, "S");
        Node e = G.MakeNewNode();
        G.set_strlabel(e, "E");
        
        ArrayList<Node> Ms = new ArrayList<>();
        ArrayList<Node> Ds = new ArrayList<>();
        ArrayList<Node> Is = new ArrayList<>();
        
        ArrayList<Edge> Medges = new ArrayList<>();
        ArrayList<Edge> Dedges = new ArrayList<>();
        ArrayList<Edge> Iedges = new ArrayList<>();
        
        for (int i=0 ; i<newLength ; i++) {
            String M_num = "M" + (i+1);
            Node m = G.MakeNewNode();
            G.set_strlabel(m, M_num);
            Ms.add(m);
            
            String D_num = "D" + (i+1);
            Node d = G.MakeNewNode();
            G.set_strlabel(d, D_num);
            Ds.add(d);
            
            String I_num = "I" + i;
            Node ii = G.MakeNewNode();
            G.set_strlabel(ii, I_num);
            Is.add(ii);
        }
        
        // one extra node for I
        String I_num = "I" + newLength;
        Node ii = G.MakeNewNode();
        G.set_strlabel(ii, I_num);
        Is.add(ii);
        
        Node tail;
        Node head;
        Edge newEdge;
        
        // connection I->DM, DM->I
        for (int i=0 ; i<Is.size() ; i++) {
            // I's self loop (applies to all)
            Node I = Is.get(i);
            newEdge = G.MakeNewEdge();
            G.d_connect(newEdge, I, I);
            Iedges.add(newEdge);
            
            if (i<Is.size()-1) {
                // I->D, I->M
                Node D = Ds.get(i); // starting with D1
                newEdge = G.MakeNewEdge();
                G.d_connect(newEdge, I, D);
                Iedges.add(newEdge);
                
                Node M = Ms.get(i); // starting with M1
                newEdge = G.MakeNewEdge();
                G.d_connect(newEdge, I, M);
                Iedges.add(newEdge);
                
                Node I_ = Is.get(i+1); // starting with I1
                newEdge = G.MakeNewEdge();
                G.d_connect(newEdge, M, I_);
                Iedges.add(newEdge);
                
                newEdge = G.MakeNewEdge();
                G.d_connect(newEdge, D, I_);
                Iedges.add(newEdge);
            }
            
            
            // Le Last One
            if (i==Is.size()-1) {
                
                Node I_ = Is.get(i); //I_n, last I
                // I->E
                newEdge = G.MakeNewEdge();
                G.d_connect(newEdge, I_, e);
                Iedges.add(newEdge);
            }
            
        }
        
        // connection D->D, M->M
        for (int i=1 ; i<Ms.size() ; i++) {
            // M->M
            tail = Ms.get(i-1);
            head = Ms.get(i);
            newEdge = G.MakeNewEdge();
            G.d_connect(newEdge, tail, head);
            
            // D->D
            tail = Ds.get(i-1);
            head = Ds.get(i);
            newEdge = G.MakeNewEdge();
            G.d_connect(newEdge, tail, head);
        
        // connection D->M, M->D crisscross
            // M->D
            tail = Ms.get(i-1);
            head = Ds.get(i);
            newEdge = G.MakeNewEdge();
            G.d_connect(newEdge, tail, head);
            
            // D->M
            tail = Ds.get(i-1);
            head = Ms.get(i);
            newEdge = G.MakeNewEdge();
            G.d_connect(newEdge, tail, head);
        }
        
        // connecting S and E to M
        tail = Ms.get(0);
        head = Ms.get(Ms.size()-1);
        newEdge = G.MakeNewEdge();
        G.d_connect(newEdge, S, tail);
        newEdge = G.MakeNewEdge();
        G.d_connect(newEdge, head, e);
        
        // connecting S and E to D
        tail = Ds.get(0);
        head = Ds.get(Ds.size()-1);
        newEdge = G.MakeNewEdge();
        G.d_connect(newEdge, S, tail);
        newEdge = G.MakeNewEdge();
        G.d_connect(newEdge, head, e);
        
        Node firstI = Is.get(0);
        newEdge = G.MakeNewEdge();
        G.d_connect(newEdge, S, firstI);
        Iedges.add(newEdge);
        
        
        // there we have our profile HMM diagram
        this.diagram = G;
        
        ArrayList<String> sigma_sans = new ArrayList<>(sigma);
        sigma_sans.remove("-");
        
        // initialize spell matrix
        for (Node node : this.diagram.nodes){
            this.spell.put(node.label, new HashMap<String,Integer>());
            E.put(node.label, new HashMap<String,Double>());
            for (String alph : sigma_sans){
                this.spell.get(node.label).put(alph, 0);
                E.get(node.label).put(alph, 0d);
            }
        }
        
        // Figure out the hidden path
        ArrayList<ArrayList<Edge>> paths = new ArrayList<>();
        for (String aStr : alignment) {
            paths.add(this.HiddenPath(aStr));
        }
        
        // build transition matrix
        for (Node n : this.diagram.nodes) {
            T.put(n.label,new HashMap<String,Double>());
        }
        
        HashMap<String,Integer> srcCount = new HashMap<>();
        HashMap<String,HashMap<String,Integer>> dstCount = new HashMap<>();
        
        for (Node n : this.diagram.nodes) {
            dstCount.put(n.label, new HashMap<String, Integer>());
            srcCount.put(n.label, 0);
            for (Node m : this.diagram.nodes){
                T.get(n.label).put(m.label, 0d);
                dstCount.get(n.label).put(m.label, 0);
            }
            
        }
        
        // look at all edges
        for (ArrayList<Edge> path : paths) {
            for (Edge ee : path) {
                if (srcCount.containsKey(ee.src.label)){
                    srcCount.put(ee.src.label,srcCount.get(ee.src.label)+1);
                } else {
                    srcCount.put(ee.src.label,1);
                }
                
                dstCount.get(ee.src.label).put(ee.dst.label,dstCount.get(ee.src.label).get(ee.dst.label)+1);
            }
        }

        for (Edge ee : this.diagram.edges){
            Node n = ee.src;
            Node m = ee.dst;
            if (srcCount.get(n.label)>0){
                T.get(n.label).put(m.label, (double)dstCount.get(n.label).get(m.label)/srcCount.get(n.label));
            }
        }
        
        for (Node node : this.diagram.nodes) {
            for (String alph : sigma_sans) {
                HashMap<String,Integer> c = this.spell.get(node.label); 
                HashMap<String,Double> cc = E.get(node.label);
                if (srcCount.get(node.label)>0){
                    cc.put(alph,(double)c.get(alph)/srcCount.get(node.label));
                }
            }
        }
        
        this.transition = T;
        this.emission = E;
    }
    
    public ArrayList<Edge> HiddenPath (String alignstr) {
        ArrayList<Edge> path = new ArrayList<>();
        
        
        // NOTE TO SELF: i can be out of index, since alignstr is longer than newLength.
        int j=0;
        for (int i=0 ; i<alignstr.length(); i++) {
            // j is more flexible. i -> track string, j -> track diagram
            String curStr = ""+alignstr.charAt(i);
            Node n1 = null;
            Node n2 = null;
            if (j==0) {
                // S
                n1 = this.diagram.strNodeMap.get("S");
            } else {
                // last node in the path
                n1 = path.get(path.size()-1).dst;
            }
            if (this.underCut.contains(i)) {
                // this index had been cut out -> insertion needed
                // fix j
                if (curStr.equals("-")) {
                    //Do nothing
                } else {
                    // insertion
                    String n2name = "I" + (j);
                    n2 = this.diagram.strNodeMap.get(n2name);
                    Edge e = this.diagram.getEdge(n1,n2);
                    path.add(e);
                    HashMap<String,Integer> c = spell.get(n2name);
                    c.put(curStr, c.get(curStr)+1);
                }
            } else {
                // allowed to increment j by one
                j++;
                if (curStr.equals("-")) {
                    // deletion
                    String n2name = "D" + j;
                    n2 = this.diagram.strNodeMap.get(n2name);
                    
                } else {
                    // maintain
                    String n2name = "M" + j;
                    n2 = this.diagram.strNodeMap.get(n2name);
                }
                Edge e = this.diagram.getEdge(n1, n2);
                path.add(e);
                if (!curStr.equals("-")){
                    HashMap<String,Integer> c = spell.get(n2.label);
                    c.put(curStr, c.get(curStr)+1);
                }
            }
            
        }
        
        Node end = this.diagram.strNodeMap.get("E");
        Node n1 = path.get(path.size()-1).dst;
        Edge e = this.diagram.getEdge(n1,end);
        path.add(e);
        
        return path;
    }
    
    public void ApplyPseudocount(double pseudocount){
        HashMap<String,HashMap<String,Double>> T = this.transition;
        HashMap<String,HashMap<String,Double>> E = this.emission;
        
        // since E is easier I'll do E.
        for (String key1 : E.keySet()) {
            if (key1.equals("S") || key1.equals("E") || key1.charAt(0)=='D') {
                // Do nothing
            }
            else {
                double normalizer = 0d;
                for (String key2 : E.get(key1).keySet()) {
                    HashMap<String,Double> c = E.get(key1);
                    double num = c.get(key2);
                    num += pseudocount;
                    normalizer += num;
                }
                for (String key2 : E.get(key1).keySet()) {
                    HashMap<String,Double> c = E.get(key1);
                    double num = c.get(key2);
                    num += pseudocount;
                    c.put(key2, (double)num/normalizer);
                }
            }
        }
        
        // T's turn
        for (String key1 : T.keySet()) {
            ArrayList<String> perm = this.permitted(key1);
            double normalizer = 0d;
            for (String key2 : perm) {
                HashMap<String,Double> c = T.get(key1);
                    double num = c.get(key2);
                    num += pseudocount;
                    normalizer += num;
            }
            for (String key2 : perm) {
                HashMap<String,Double> c = T.get(key1);
                double num = c.get(key2);
                num += pseudocount;
                c.put(key2, (double)num/normalizer);
            }
        }
    }
        
    
    
    public ArrayList<String> permitted(String srcName) {
        ArrayList<String> perm = new ArrayList<>();
        String srcStt = ""+srcName.charAt(0);
        if (srcStt.equals("S")) {
            perm.add("I0");
            perm.add("M1");
            perm.add("D1");
        }

        if (srcStt.equals("E")) {
            //do nothing
        }

        if (srcName.length()>1){
            int srcNum = Integer.parseInt(srcName.substring(1));


            if (srcStt.equals("I")) {
                perm.add(srcName);
                if (this.diagram.strNodeMap.containsKey(("M"+(srcNum+1)))) {
                    // M, D are permitted
                    perm.add("M"+(srcNum+1));
                    perm.add("D"+(srcNum+1));
                } else {
                    perm.add("E");
                }
            }

            if (srcStt.equals("M") || srcStt.equals("D")){
                if (this.diagram.strNodeMap.containsKey(("M"+(srcNum+1)))) {
                    // M, D, I are permitted
                    perm.add("M"+(srcNum+1));
                    perm.add("D"+(srcNum+1));
                    perm.add("I"+(srcNum));
                } else {
                    perm.add("E");
                    perm.add("I"+(srcNum));
                }
            }
        }
        
        
        return perm;
    }
}
