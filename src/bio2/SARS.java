/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author USER1
 */
public class SARS {
    
    public static HashMap<Integer,int[]> coordMap = new HashMap<>(); 
    
    public static int LimbLength(int n, int j, HashMap<Integer,HashMap<Integer,Integer>>DMat){
        CombinationGenerator CG = new CombinationGenerator(n,2);
        ArrayList<int[]> combs = new ArrayList<>();
        while (CG.hasMore()) {
            int[] newComb = new int[2];
            int[] c = CG.getNext();
            newComb[0] = c[0];
            newComb[1] = c[1];
            combs.add(newComb);
        }
        int ll = Integer.MAX_VALUE;
        HashMap<Integer,Integer> row = DMat.get(j);
        for(int a=0; a<combs.size(); a++){
            int[] curComb = combs.get(a);
            int i = curComb[0];
            int k = curComb[1];
            if (j!=i && j!=k ){
                int lll = DMat.get(i).get(j) + DMat.get(j).get(k) - DMat.get(i).get(k);
                lll = lll/2;
                if (lll<ll) {
                    ll = lll;
                }
            }
        }
        
        return ll;
    }
    
    public static void problem1 () throws IOException {  
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10329_11.txt");
        int nn = Integer.parseInt(list.remove(0));
        int jj = Integer.parseInt(list.remove(0));
        HashMap<Integer,HashMap<Integer,Integer>> D = Parser.IntoDMat(list);
        int l = SARS.LimbLength(nn, jj, D);
    }
    
    public static PhyloTree AdditivePhylogeny(HashMap<Integer,HashMap<Integer,Integer>> DMat, int n, int original_size) {
        // 0 index world for the lols
        int nn = n+1; // number of nodes
        
        
        // BASE CASE: number of nodes = 2
        if (nn<=2) {
            // Return base tree
            PhyloTree T = new PhyloTree(true);
            Node i = T.MakeNewNode();
            T.node_set_numlabel(i,0);
            Node j = T.MakeNewNode();
            T.node_set_numlabel(j,1);
            Edge e = T.MakeNewEdge();
            e.weight = DMat.get(0).get(1);
            T.u_connect(e,i,j);
            
            Collections.sort(T.nodes);
            
            return T;
        }
        int limbLength = LimbLength(nn,n,DMat);
        HashMap<Integer,Integer> nData = DMat.get(n);
        
        
        // Make D-bald
        for (int j=0; j<n; j++) {
            int newLength = DMat.get(j).get(n) - limbLength;
            DMat.get(j).put(n,newLength);
            DMat.get(n).put(j,newLength);
        }
        
        
        int x = -1;
        int i = -2;
        int k = -3;
        
        // for each combination of leaves, try to compute x if applicable.
        // if x returns -1, D should be non-additive
        CombinationGenerator CG = new CombinationGenerator(n,2);
        while (CG.hasMore()) {
            int[] comb = CG.getNext();
            i = comb[0];
            k = comb[1];
            if (i!=n && k!=n) {
                
                int i_to_n = DMat.get(i).get(n);
                int n_to_k = DMat.get(n).get(k);
                int i_to_k = DMat.get(i).get(k);
                
                if (i_to_k == i_to_n + n_to_k) {
                   x = i_to_n;
                   break;
                }
                
            }
            
        }
        // Remove n from D
        DMat.put(n, null);
        Iterator<Integer> intIt = DMat.keySet().iterator();
        while (intIt.hasNext()) {
            int q = intIt.next();
            if (DMat.get(q)!=null){
                DMat.get(q).put(n,null);
            }
        }
        
        // Recurse
        PhyloTree T = AdditivePhylogeny(DMat, n-1, original_size);
        
        // When we get tree, create nodes to attach to tree
        Node v = null; // attachment point node
        Node Ni = T.numNodeMap.get(i);
        Node Nk = T.numNodeMap.get(k);
        Node Nn = T.MakeNewNode(); // new leaf
        T.node_set_numlabel(Nn, n);
        
        // See if any internal node satisfies the attachment point criteria
        Edge candEdge = null; //first edge in path would be always internal  
        
        
        ArrayList<Edge> path = Graph.u_getPath(Ni, Nk);
        int dist = 0;
        int min = Integer.MAX_VALUE;
        Node head = Ni;
        Node tail = null;
        Node leaf = null;
        Node inter = null;
        if (path!=null) {
            for(int b=0; b<path.size(); b++){
                
                Edge curEdge = path.get(b);
                dist += curEdge.weight;
                
                if (curEdge.nodes.get(0).equals(head)) {
                    tail = head;
                    head = curEdge.nodes.get(1);
                    
                } else {
                    tail = head;
                    head = curEdge.nodes.get(0);
                }
                
                
                if (dist < x) {
                    candEdge = curEdge;
                }

                else if (dist==x) {
                    v = head;
                    candEdge = curEdge;
                    break;
                    
                } else if (dist > x) {
                    candEdge = curEdge;
                    break;
                }
            }
        }
        
        if (dist > x) {
            inter = head;
            leaf = tail;
        } else if (dist < x) {
            inter = head;
            leaf = tail;
        } else if (dist == x) {
            inter = null;
            leaf = null;
        }
        
        // if there is no such internal node, make a new node
        if (v==null) {
            v = T.MakeNewNode();
            T.node_set_numlabel(v,DMat.size()+n-2);
        } else if (candEdge==null) {
            int bravo = -99;
        }
        
        // rebuild D
        DMat.put(n, nData);
        intIt = DMat.keySet().iterator();
        while (intIt.hasNext()) {
            int q = intIt.next();
            if (DMat.get(q)!=null){
                DMat.get(q).put(n,DMat.get(n).get(q));
            }
        }
        
        // Insert
        
        int oldWgt = candEdge.weight;
        if (dist != x) {
            T.u_disconnect(candEdge, candEdge.nodes.get(0), candEdge.nodes.get(1));
            Edge leafEdge = T.MakeNewEdge();
            Edge interEdge = T.MakeNewEdge();
            int newWgt = dist - x;
            
            if (oldWgt < newWgt) {
                interEdge.weight = oldWgt;
                leafEdge.weight = newWgt - oldWgt;
            } else {
                interEdge.weight = newWgt;
                leafEdge.weight = oldWgt - newWgt;
            }
            
            if (leafEdge.weight<0 || interEdge.weight<0) {
                int xxx = 0;
            }
            
            T.u_connect(leafEdge,leaf,v);
            T.u_connect(interEdge,inter,v);
        }
            // Make a limb
        Edge ee = T.MakeNewEdge();
        ee.weight = limbLength;
        T.u_connect(ee,Nn,v);
            
            
       
        return T;
    }
    
    public static void problem2 () throws IOException {  
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10330_6.txt");
        int nn = Integer.parseInt(list.remove(0))-1;
        HashMap<Integer,HashMap<Integer,Integer>> D = Parser.IntoDMat(list);
        PhyloTree T = SARS.AdditivePhylogeny(D,nn,D.size());
        Collections.sort(T.nodes);
        Collections.sort(T.edges);
        
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        ArrayList<Integer> intList = new ArrayList<Integer>();
        Set<Integer> visited = new HashSet<>();
        ArrayList<String> strList = new ArrayList<String>();
        
        ArrayList<Edge> edges = new ArrayList<Edge>(T.edges);
        
        for(int i=0; i<T.edges.size(); i++){
            Edge curEdge = T.edges.get(i);
            Edge revEdge = new Edge(Integer.MAX_VALUE,false);
            revEdge.weight = curEdge.weight;
            revEdge.nodes.add(curEdge.nodes.get(1));
            revEdge.nodes.add(curEdge.nodes.get(0));
            edges.add(revEdge);
        }
        
        Collections.sort(edges);
        
        for(int i=0; i<edges.size(); i++){
            Edge curEdge = edges.get(i);
            writer.println(curEdge.nodes.get(0).numlabel+"->"+curEdge.nodes.get(1).numlabel + ":" + curEdge.weight);
            
        }
        writer.close();
    }
    
    public static PhyloTree UPGMA(HashMap<Integer,HashMap<Integer,Float>> DMat, int n){
        PhyloTree T = new PhyloTree(false);
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
        for(int i=0; i<n; i++){
            Node no = T.MakeNewNode();
            no.age = 0;
            no.numlabel = i;
            ArrayList<Node> leaves = new ArrayList<Node>();
            leaves.add(no);
            Cluster c = new Cluster(no,leaves);
            clusters.add(c);
        }
        while(clusters.size()>1) {
            CombinationGenerator CG = new CombinationGenerator(clusters.size(),2);
            float min_dist = Float.MAX_VALUE;
            Cluster[] closest = new Cluster[2];
            while (CG.hasMore()) {
                int[] comb = CG.getNext();
                Cluster c1 = clusters.get(comb[0]);
                Cluster c2 = clusters.get(comb[1]);
                if (!c1.equals(c2)){
                    float dist = c1.distanceTo(c2, DMat);
                    if (dist < min_dist) {
                        min_dist = dist;
                        closest[0] = c1;
                        closest[1] = c2;
                    }
                }
            }
            
            // merging of two clusters
            Cluster close1 = closest[0];
            Cluster close2 = closest[1];
            
            Node newHead = T.MakeNewNode();
            newHead.age = min_dist/2;
            newHead.numlabel = 2*n-clusters.size();
            ArrayList<Node> newLeaves = new ArrayList<>(close1.leaves);
            newLeaves.addAll(close2.leaves);
            Cluster newC = new Cluster(newHead,newLeaves);
            clusters.add(newC);
            
            Edge nEdge1 = T.MakeNewEdge();
            T.u_connect(nEdge1, close1.head, newHead);
            
            Edge nEdge2 = T.MakeNewEdge();
            T.u_connect(nEdge2, close2.head, newHead);
            
            clusters.remove(close1);
            clusters.remove(close2);
            
            // add new row and column
            DMat.put(newC.head.numlabel,new HashMap<Integer,Float>());
            DMat.get(newC.head.numlabel).put(newC.head.numlabel, 0f);
            for(int i=0; i<clusters.size(); i++){
                Cluster curCluster = clusters.get(i);
                if (!curCluster.equals(newC)) {
                    float cDist = T.newClusterDistance(close1, close2, curCluster, DMat);
                    DMat.get(newC.head.numlabel).put(curCluster.head.numlabel,cDist);
                    DMat.get(curCluster.head.numlabel).put(newC.head.numlabel,cDist);
                }
            }
            
            // modifying DMat
            Iterator<Integer> hashIt = DMat.keySet().iterator();
            while (hashIt.hasNext()) {
                int num = hashIt.next();
                DMat.get(num).remove(close1.head.numlabel);
                DMat.get(num).remove(close2.head.numlabel);
            }
            DMat.remove(close1.head.numlabel);
            DMat.remove(close2.head.numlabel);
            
            
            
        }
        
        T.root = clusters.get(0).head;
        T.updateEdgeLength();
        
        return T;
    }
    
    public static void problem3 () throws IOException {  
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10332_8.txt");
        int n = Integer.parseInt(list.remove(0));
        HashMap<Integer,HashMap<Integer,Float>> D = Parser.IntoFDMat(list);
        PhyloTree T = UPGMA(D, n);
        Collections.sort(T.nodes);
        Collections.sort(T.edges);
        
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        ArrayList<Integer> intList = new ArrayList<Integer>();
        Set<Integer> visited = new HashSet<>();
        ArrayList<String> strList = new ArrayList<String>();
        
        ArrayList<Edge> edges = new ArrayList<Edge>(T.edges);
        
        for(int i=0; i<T.edges.size(); i++){
            Edge curEdge = T.edges.get(i);
            Edge revEdge = new Edge(Integer.MAX_VALUE,false);
            revEdge.fweight = curEdge.fweight;
            revEdge.nodes.add(curEdge.nodes.get(1));
            revEdge.nodes.add(curEdge.nodes.get(0));
            edges.add(revEdge);
        }
        
        Collections.sort(edges);
        
        for(int i=0; i<edges.size(); i++){
            Edge curEdge = edges.get(i);
            writer.println(curEdge.nodes.get(0).numlabel+"->"+curEdge.nodes.get(1).numlabel + ":" + (float)Math.round(curEdge.fweight*1000)/1000);
            
        }
        writer.close();
    }

    public static HashMap<Integer,HashMap<Integer,Float>> NJDMat(HashMap<Integer,HashMap<Integer,Float>> DMat) {
        int n = DMat.size();
        HashMap<Integer,HashMap<Integer,Float>> njd = new HashMap<>();
        Iterator<Integer> intIt = DMat.keySet().iterator();
        while (intIt.hasNext()) {
            int i = intIt.next();
            njd.put(i,new HashMap<Integer,Float>());
            if (DMat.get(i)!=null){
                Iterator<Integer> jntIt = DMat.get(i).keySet().iterator();
                while (jntIt.hasNext()) {
                    int j = jntIt.next();
                    if (DMat.get(i)!=null) {
                        if (DMat.get(i).get(j)!=null) {
                            if (i==j) {
                                njd.get(i).put(j, 0f);
                            } else {
                                float njd_val = (n-2) * DMat.get(i).get(j) - TotalDistance(DMat, i) - TotalDistance(DMat, j);
                                njd.get(i).put(j, njd_val);
                                int arr[] = new int[2];
                                arr[0] = i;
                                arr[1] = j;
                            }
                        }
                    }
                }
            }
        }
        
        return njd;
    }
    
    public static float TotalDistance(HashMap<Integer,HashMap<Integer,Float>> DMat, int t) {
        float dist = 0;
        Iterator<Integer> intIt = DMat.keySet().iterator();
        while (intIt.hasNext()) {
            int u = intIt.next();
            if (DMat.get(t)!=null){
                if (DMat.get(t).get(u)!=null){
                    dist += DMat.get(t).get(u);
                }
            }
        }
        return dist;
    }
    
    public static void symCheck(HashMap<Integer,HashMap<Integer,Float>> D) {
        Iterator<Integer> intIt = D.keySet().iterator();
        ArrayList<Integer> is = new ArrayList<>(D.keySet());
        for(int c=0; c<is.size(); c++){
            int i = is.get(c);
            for(int d=0; d<is.size(); d++){
                int j = is.get(d);
                
                if (D.containsKey(j)) {
                    D.get(j).put(i, D.get(i).get(j));
                } else {
                    D.put(j, new HashMap<Integer,Float>());
                    D.get(j).put(i, D.get(i).get(j));
                }
                
            }
        }
    }
    
    public static void rmvRowCol (HashMap<Integer,HashMap<Integer,Float>> D, int r) {
        Iterator<Integer> intIt = D.keySet().iterator();
        while (intIt.hasNext()) {
            int i = intIt.next();
            D.get(i).remove(r);
        }
        D.remove(r);
    }
    
    public static PhyloTree NeighborJoining(HashMap<Integer,HashMap<Integer,Float>> DMat, int n, int lnum) {
    
        if (n<=2) {
            PhyloTree T = new PhyloTree(true);
            ArrayList<Integer> is = new ArrayList<>(DMat.keySet());
            int inum = is.get(0);
            int jnum = is.get(1);
            Node i = T.MakeNewNode();
            T.node_set_numlabel(i,inum);
            Node j = T.MakeNewNode();
            T.node_set_numlabel(j,jnum);
            Edge e = T.MakeNewEdge();
            e.fweight = DMat.get(inum).get(jnum);
            T.u_connect(e,i,j);
            return T;
        }
        
        HashMap<Integer,HashMap<Integer,Float>> NJD = NJDMat(DMat);
        float min_el = Float.MAX_VALUE;
        int[] min_ij = new int[2];
        Iterator<Integer> intIt = NJD.keySet().iterator();
        while (intIt.hasNext()) {
            int i = intIt.next();
            Iterator<Integer> jntIt = NJD.get(i).keySet().iterator();
            while (jntIt.hasNext()) {
                int j = jntIt.next();
                
                if (i!=j){
                    float njd_val = NJD.get(i).get(j);
                
                    if (njd_val < min_el) {
                        min_el = njd_val;
                        min_ij[0] = i;
                        min_ij[1] = j;
                    }
                }
                
            }
        }
        
        int i = min_ij[0];
        int j = min_ij[1];
        
        float delta = TotalDistance(DMat, i) - TotalDistance(DMat, j);
        delta = delta / (n-2);
        
        float limbLength_i = 0.5f * (DMat.get(i).get(j) + delta);
        float limbLength_j = 0.5f * (DMat.get(i).get(j) - delta);
        
        int newNum = lnum;
        
        DMat.put(newNum, new HashMap<Integer,Float>());
        DMat.get(newNum).put(newNum, 0f);
        intIt = DMat.keySet().iterator();
        while (intIt.hasNext()) {
            int k = intIt.next();
            if (k!=newNum){
                float a = DMat.get(k).get(i);
                float b = DMat.get(k).get(j);
                float c = DMat.get(i).get(j);
                float value = 0.5f * (a + b - c);
                DMat.get(k).put(newNum, value);
                DMat.get(newNum).put(k,value);
            }
        }
        symCheck(DMat);
        rmvRowCol(DMat, i);
        rmvRowCol(DMat, j);
        
        PhyloTree T = NeighborJoining(DMat, n-1, lnum+1);
        
        Node mNode = T.MakeNewNode();
        T.node_set_numlabel(mNode, newNum);
        
        Node iNode = T.MakeNewNode();
        T.node_set_numlabel(iNode,i);
        Node jNode = T.MakeNewNode();
        T.node_set_numlabel(jNode,j);
        
        
        Edge imEdge = T.MakeNewEdge();
        imEdge.fweight = limbLength_i;
        Edge jmEdge = T.MakeNewEdge();
        jmEdge.fweight = limbLength_j;
        
        T.u_connect(imEdge,iNode,mNode);
        T.u_connect(jmEdge,jNode,mNode);
        
        
        return T;
    }
    
    public static void problem4() throws FileNotFoundException, IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10333_6.txt");
        int n = Integer.parseInt(list.remove(0));
        HashMap<Integer,HashMap<Integer,Float>> D = Parser.IntoFDMat(list);        
        PhyloTree T = NeighborJoining(D, n, n);
        Collections.sort(T.nodes);
        Collections.sort(T.edges);
        
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        ArrayList<Integer> intList = new ArrayList<Integer>();
        Set<Integer> visited = new HashSet<>();
        ArrayList<String> strList = new ArrayList<String>();
        
        ArrayList<Edge> edges = new ArrayList<Edge>(T.edges);
        
        for(int i=0; i<T.edges.size(); i++){
            Edge curEdge = T.edges.get(i);
            Edge revEdge = new Edge(Integer.MAX_VALUE,false);
            revEdge.fweight = curEdge.fweight;
            revEdge.nodes.add(curEdge.nodes.get(1));
            revEdge.nodes.add(curEdge.nodes.get(0));
            edges.add(revEdge);
        }
        
        Collections.sort(edges);
        
        for(int i=0; i<edges.size(); i++){
            Edge curEdge = edges.get(i);
            writer.println(curEdge.nodes.get(0).numlabel+"->"+curEdge.nodes.get(1).numlabel + ":" + (float)Math.round(curEdge.fweight*1000)/1000);
            
        }
        writer.close();
    
    }
    
    public static int SmallParsimony(UnrootedPhyloTree T, int ii) {
        
        
        
        String Symbols = "ATCG";
        HashMap<String,HashMap<String,Integer>> mat = new HashMap<>();
        
        for(int i=0; i<Symbols.length(); i++){
            String curStr = ""+Symbols.charAt(i);
            mat.put(curStr, new HashMap<String,Integer>());
        }
        
        for(int i=0; i<Symbols.length(); i++){
            String str1 = ""+Symbols.charAt(i);
            for(int j=0; j<Symbols.length(); j++){
                String str2 = ""+Symbols.charAt(j);
                
                if (str1.equals(str2)) {
                    mat.get(str1).put(str2, 0);
                } else {
                    mat.get(str1).put(str2, 1);
                }
                
            }
        }
        
        for(int i=0; i<T.nodes.size(); i++){
            Node v = T.nodes.get(i);
            if (v.isLeaf()) {
                v.tag = 1;
                v.character = ""+v.label.charAt(ii);
                
                for(int j=0; j<Symbols.length(); j++){
                    String k = ""+Symbols.charAt(j);
                    if (v.character.equals(k)){
                        v.s.put(k,0);
                    } else {
                        v.s.put(k,9999);
                    }
                }
                
            } else {
                v.tag = 0;
            }
        }
        Node v = null;
        while (T.getRipeNodes().size()>0) {
            ArrayList<Node> ripeNodes = T.getRipeNodes();
            for(int i=0; i<ripeNodes.size(); i++){
                v = ripeNodes.get(i);
                v.tag = 1;
                
                
                
                for(int j=0; j<Symbols.length(); j++){
                    String k = ""+Symbols.charAt(j);
                    v.s.put(k,MOAS2(v,mat,k,Symbols));
                }
            }
        }
        int result = MOAS1(v,Symbols);
        
        // before returning, work on backtracking
        
        int mini = 9999;
        String minKey = "";
        
        ArrayList<String> keys = new ArrayList<>(T.root.s.keySet());
        for(int i=0; i<keys.size(); i++){
            if (T.root.s.get(keys.get(i)) < mini) {
                mini = T.root.s.get(keys.get(i));
                minKey = keys.get(i);
            }
        }
        
        T.root.character = minKey;
        
        ArrayDeque<Node> Q = new ArrayDeque<>();
        Q.addFirst(T.root);
        HashSet<Node> visited = new HashSet<>();
        
        while (Q.size()>0){
            
            Node curNode = Q.removeFirst();
            if (!curNode.isLeaf() && !visited.contains(curNode)){
                
                visited.add(curNode);
                
                Q.addFirst(curNode.son);
                Q.addFirst(curNode.daughter);
                
                if (!curNode.equals(T.root)){
                    int min_val = 9999;
                    String min_str = "";

                    String kk = curNode.parent.character;
                    for(int j=0; j<Symbols.length(); j++){
                        String i_ = ""+Symbols.charAt(j);
                        int val = curNode.s.get(i_)+mat.get(i_).get(kk);
                        if (val<min_val) {
                            min_str = i_;
                            min_val = val;
                        }
                    }
                    
                    
                    curNode.character = min_str;
                    curNode.label += min_str;
                    
                    if (curNode.numlabel==24) {
                        int asdf = 000;
                    }
                }
            }
        }
        
        return result;
    }
    
    public static int MOAS2(Node v, HashMap<String,HashMap<String,Integer>> mat, String k, String Symbols) {
        int min_int_i = 9999;
        for(int c=0; c<Symbols.length(); c++){
            String i = ""+Symbols.charAt(c);
            int val = v.son.s.get(i) + mat.get(i).get(k);
            if (val<min_int_i) {
                min_int_i = val;
            }
            
        }
        
        int min_int_j = 9999;
        for(int c=0; c<Symbols.length(); c++){
            String j = ""+Symbols.charAt(c);
            int val = v.daughter.s.get(j) + mat.get(j).get(k);
            if (val<min_int_j) {
                min_int_j = val;
            }
            
        }
        
        
        return min_int_i + min_int_j;
    }
    
    public static int MOAS1(Node v, String Symbols) {
        int min_int = Integer.MAX_VALUE;
        for(int j=0; j<Symbols.length(); j++){
            String k = ""+Symbols.charAt(j);
            if (v.s.get(k)<min_int) {
                min_int = v.s.get(k);
            }
        }
        
        return min_int;
    }
    
    
    
    public static void problem5 () throws IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10335_12.txt");
        int n = Integer.parseInt(list.remove(0));
        
        PhyloTree T = new PhyloTree(false);
        
        int count = 0;
        int len = -1;
        for(int i=0; i<list.size(); i++){
            String line = list.get(i);
            String[] data = line.split("->");
            
            if (data[0].contains("A") || data[0].contains("T") || data[0].contains("C") || data[0].contains("G")) {
                continue;
            }
            
            int num0 = Integer.parseInt(data[0]);
            if (!T.numNodeMap.containsKey(num0)) {
                Node moreNode = T.MakeNewNode();
                T.node_set_numlabel(moreNode, num0);
            }
            
            if (data[1].contains("A") || data[1].contains("T") || data[1].contains("C") || data[1].contains("G")) {
                Node curNode = T.numNodeMap.get(num0);
                
                Node newNode = T.MakeNewNode();
                newNode.label = data[1];
                len = data[1].length();
                newNode.character = ""+data[1].charAt(0);
                T.node_set_numlabel(newNode,count++);
                
                Edge newEdge = T.MakeNewEdge();
                T.u_connect(newEdge, curNode, newNode);
                
            } else {
                int num1 = Integer.parseInt(data[1]);
                
                if (!T.numNodeMap.containsKey(num1)) {
                    Node newNode = T.MakeNewNode();
                    T.node_set_numlabel(newNode, num1);
                }
                
                Node src = T.numNodeMap.get(num0);
                Node dst = T.numNodeMap.get(num1);
                
                if (!src.nbrs.contains(dst) && !dst.nbrs.contains(src)){
                    Edge newEdge = T.MakeNewEdge();
                    T.u_connect(newEdge, src, dst);
                }
                
            }
            
        }
        
        Node newRoot = T.MakeNewNode();
        T.node_set_numlabel(newRoot,999);
        T.root = newRoot;
               
        Edge victim = T.edges.get(T.edges.size()-1);
        Node src = victim.nodes.get(0);
        Node dst = victim.nodes.get(1);
        
        T.u_disconnect(victim, src, dst);
        
        src.parent = newRoot;
        newRoot.son = src;
        Edge newEdge = T.MakeNewEdge();
        T.u_connect(newEdge, src, newRoot);
                
        dst.parent = newRoot;
        newRoot.daughter = dst;
        newEdge = T.MakeNewEdge();
        T.u_connect(newEdge, dst, newRoot);
        
        ArrayDeque<Node> Q = new ArrayDeque<>();
        Set<Node> visited = new HashSet<>();
        Q.addFirst(T.root);
        while (Q.size()>0) {
            Node curNode = Q.removeFirst();
            if (curNode.edges.size()>1 && !visited.contains(curNode)) {
                visited.add(curNode);
                for(int i=0; i<curNode.nbrs.size(); i++){
                    Node curNbr = curNode.nbrs.get(i);
                    if (!curNbr.equals(curNode.parent)) {
                        if (curNode.son==null) {
                            curNode.son = curNbr;
                            curNbr.parent = curNode;
                        } else if (curNode.daughter==null) {
                            curNode.daughter = curNbr;
                            curNbr.parent = curNode;
                        }
                    }
                }
                Q.addFirst(curNode.son);
                Q.addFirst(curNode.daughter);
                
                
            }
        }
                        
                        
        
        int sum = 0;
        for(int i=0; i<len; i++){
       //     sum += SmallParsimony(T,i);
        }
        
        // remove root
        T.nodes.remove(T.root);
        T.edges.remove(T.root.edges.get(0));
        T.edges.remove(T.root.edges.get(1));
        
        newEdge = T.MakeNewEdge();
        newEdge.weight = T.root.edges.get(0).weight+T.root.edges.get(1).weight;
        T.u_connect(newEdge,T.root.son,T.root.daughter);
        
        T.root = null;
        
        
        for(int i=0; i<T.edges.size(); i++){
            Edge curEdge = T.edges.get(i);
            if (i==58) {
                int c = 00;
            }
            curEdge.weight = curEdge.nodes.get(0).hamming(curEdge.nodes.get(1));
        }
        
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        ArrayList<Edge> edges = new ArrayList<Edge>(T.edges);
        
        writer.println(sum);
        
        for(int i=0; i<T.edges.size(); i++){
            Edge curEdge = T.edges.get(i);
            Edge revEdge = new Edge(Integer.MAX_VALUE,false);
            revEdge.weight = curEdge.weight;
            revEdge.nodes.add(curEdge.nodes.get(1));
            revEdge.nodes.add(curEdge.nodes.get(0));
            edges.add(revEdge);
        }
        
        Collections.sort(edges);
        
        for(int i=0; i<edges.size(); i++){
            Edge curEdge = edges.get(i);
            writer.println(curEdge.nodes.get(0).label+"->"+curEdge.nodes.get(1).label + ":" + curEdge.weight);
            
        }
        writer.close();
        
        int v =9;
    }
    
                /**
     * This method makes a "deep clone" of any Java object it is given.
     */
     public static Object deepClone(Object object) {
       try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(object);
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bais);
         return ois.readObject();
       }
       catch (Exception e) {
         e.printStackTrace();
         return null;
       }
     }
    
    public static ArrayList<UnrootedPhyloTree> NearestNeighbor(UnrootedPhyloTree T, Edge e) throws CloneNotSupportedException {
        
        ArrayList<UnrootedPhyloTree> nbrs = new ArrayList<>();
        ArrayList<ArrayList<String>> edgeSnapShot = new ArrayList<>();
        
        ArrayList<Edge> shuffles = new ArrayList<>();
        
        Node left = e.nodes.get(0);
        Node right = e.nodes.get(1);
        
        int[][] cs = new int[][] {new int[] {0,0}, new int[] {0,1}};
        
        for (int c=0; c<cs.length; c++) {
            
            T.u_disconnect(e,left,right);
            
            int[] comb = cs[c];
            Edge leftShuffle = left.edges.get(comb[0]);
            Edge rightShuffle = right.edges.get(comb[1]);
            
            Node lefttail=null;
            Node righttail = null;
            
            if (leftShuffle.nodes.get(0).equals(left)) {
                lefttail = leftShuffle.nodes.get(1);
            } else {
                lefttail = leftShuffle.nodes.get(0);
            }
            
            
            if (rightShuffle.nodes.get(0).equals(right)) {
                righttail = rightShuffle.nodes.get(1);
            } else {
                righttail = rightShuffle.nodes.get(0);
            }
            T.u_disconnect(leftShuffle, left, lefttail);
            T.u_disconnect(rightShuffle, right, righttail);
            
            T.u_connect(leftShuffle, left, righttail);
            T.u_connect(rightShuffle, right, lefttail);
            T.u_connect(e,left,right);
            
            // snapshot
            ArrayList<String> curSnap = new ArrayList<>();
            for(int i=0; i<T.edges.size(); i++){
                Edge curEdge = T.edges.get(i);
                String str = curEdge.nodes.get(0).numlabel+"->"+curEdge.nodes.get(1).numlabel;
                curSnap.add(str);
                
                str = curEdge.nodes.get(1).numlabel+"->"+curEdge.nodes.get(0).numlabel;
                curSnap.add(str);
            }
            
            
            edgeSnapShot.add(curSnap);
            
            UnrootedPhyloTree newT = (UnrootedPhyloTree)deepClone(T);
            nbrs.add(newT);
            
        }
        
        return nbrs;
        
    }


    
    public static void problem6() throws IOException, CloneNotSupportedException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10336_6.txt");
        
        
        String n = list.remove(0);
        String[] nodeArr = n.split(" ");
        int num0 = Integer.parseInt(nodeArr[0]);
        int num1 = Integer.parseInt(nodeArr[1]);
        
        UnrootedPhyloTree T = new UnrootedPhyloTree();
        
        for(int i=0; i<list.size(); i=i+2){
            String line = list.get(i);
            String[] lArr = line.split("->");
            
            int n0 = Integer.parseInt(lArr[0]);
            int n1 = Integer.parseInt(lArr[1]);
            
            Node node0 = null;
            if (!T.numNodeMap.containsKey(n0)) {
                node0 = T.MakeNewNode();
                T.node_set_numlabel(node0, n0);
            } else {
                node0 = T.numNodeMap.get(n0);
            }
            
            Node node1 = null;
            if (!T.numNodeMap.containsKey(n1)) {
                node1 = T.MakeNewNode();
                T.node_set_numlabel(node1, n1);
            } else {
                node1 = T.numNodeMap.get(n1);
            }
            
            Edge newEdge = T.MakeNewEdge();
            T.u_connect(newEdge, node0, node1);
            }
        
        Node node0 = T.numNodeMap.get(num0);
        Node node1 = T.numNodeMap.get(num1);
        Edge fix = null;
        for(int i=0; i<node0.edges.size(); i++){
            Edge curEdge = node0.edges.get(i);
            if (curEdge.nodes.contains(node0) && curEdge.nodes.contains(node1)) {
                fix = curEdge;
            }
        }
        
        //ArrayList<ArrayList<String>> edgeSnapShots = NearestNeighbor(T, fix);
        
//        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
//        for(int i=0; i<edgeSnapShots.size(); i++){
//            for(int j=0; j<edgeSnapShots.get(i).size(); j++){
//                writer.println(edgeSnapShots.get(i).get(j));
//            }
//            writer.println("");
//        }
//        writer.close();
    }
    
    public static void rootify(UnrootedPhyloTree T) {
        Node newRoot = T.MakeNewNode();
        T.node_set_numlabel(newRoot,999);
        T.root = newRoot;
               
        Edge victim = T.edges.get(T.edges.size()-1);
        Node src = victim.nodes.get(0);
        Node dst = victim.nodes.get(1);
        
        T.u_disconnect(victim, src, dst);
        
        src.parent = newRoot;
        newRoot.son = src;
        Edge newEdge = T.MakeNewEdge();
        T.u_connect(newEdge, src, newRoot);
                
        dst.parent = newRoot;
        newRoot.daughter = dst;
        newEdge = T.MakeNewEdge();
        T.u_connect(newEdge, dst, newRoot);
        
        ArrayDeque<Node> Q = new ArrayDeque<>();
        Set<Node> visited = new HashSet<>();
        Q.addFirst(T.root);
        while (Q.size()>0) {
            Node curNode = Q.removeFirst();
            if (curNode.edges.size()>1 && !visited.contains(curNode)) {
                visited.add(curNode);
                for(int i=0; i<curNode.nbrs.size(); i++){
                    Node curNbr = curNode.nbrs.get(i);
                    if (!curNbr.equals(curNode.parent)) {
                        if (curNode.son==null) {
                            curNode.son = curNbr;
                            curNbr.parent = curNode;
                        } else if (curNode.daughter==null) {
                            curNode.daughter = curNbr;
                            curNbr.parent = curNode;
                        }
                    }
                }
                Q.addFirst(curNode.son);
                Q.addFirst(curNode.daughter);
            }
        }
    }
    
    public static void derootify(UnrootedPhyloTree T) {
        // remove root
        T.nodes.remove(T.root);
        T.edges.remove(T.root.edges.get(0));
        T.edges.remove(T.root.edges.get(1));
        
        Edge newEdge = T.MakeNewEdge();
        newEdge.weight = T.root.edges.get(0).weight+T.root.edges.get(1).weight;
        T.u_connect(newEdge,T.root.son,T.root.daughter);
        
        T.root = null;
    }
    
    public static void problem7() throws IOException, CloneNotSupportedException {
        
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10336_8.txt");
        int n = Integer.parseInt(list.remove(0));
        UnrootedPhyloTree T = new UnrootedPhyloTree();
        
        int count = 0;
        int len = -1;
        for(int i=0; i<list.size(); i++){
            String line = list.get(i);
            String[] data = line.split("->");
            
            if (data[0].contains("A") || data[0].contains("T") || data[0].contains("C") || data[0].contains("G")) {
                continue;
            }
            
            int num0 = Integer.parseInt(data[0]);
            if (!T.numNodeMap.containsKey(num0)) {
                Node moreNode = T.MakeNewNode();
                T.node_set_numlabel(moreNode, num0);
            }
            
            if (data[1].contains("A") || data[1].contains("T") || data[1].contains("C") || data[1].contains("G")) {
                Node curNode = T.numNodeMap.get(num0);
                
                Node newNode = T.MakeNewNode();
                newNode.label = data[1];
                len = data[1].length();
                newNode.character = ""+data[1].charAt(0);
                T.node_set_numlabel(newNode,count++);
                
                Edge newEdge = T.MakeNewEdge();
                T.u_connect(newEdge, curNode, newNode);
                
            } else {
                int num1 = Integer.parseInt(data[1]);
                
                if (!T.numNodeMap.containsKey(num1)) {
                    Node newNode = T.MakeNewNode();
                    T.node_set_numlabel(newNode, num1);
                }
                
                Node src = T.numNodeMap.get(num0);
                Node dst = T.numNodeMap.get(num1);
                
                if (!src.nbrs.contains(dst) && !dst.nbrs.contains(src)){
                    Edge newEdge = T.MakeNewEdge();
                    T.u_connect(newEdge, src, dst);
                }
                
            }
            
        }
        
        int score = 99999;
        boolean virgin = true;
        
        while (true) {
            if (virgin) {
                virgin = false;
                UnrootedPhyloTree flexT = (UnrootedPhyloTree)deepClone(T);
                rootify(flexT);
                int sum = 0;
                for(int i=0; i<len; i++){
                    sum += SmallParsimony(flexT,i);
                }
                derootify(flexT);
                score = sum;
                
                ArrayList<Edge> edges = new ArrayList<Edge>(flexT.edges);

                writer.println(score);

                for(int i=0; i<flexT.edges.size(); i++){
                    Edge curEdge = flexT.edges.get(i);
                    Edge revEdge = new Edge(Integer.MAX_VALUE,false);
                    revEdge.weight = curEdge.weight;
                    revEdge.nodes.add(curEdge.nodes.get(1));
                    revEdge.nodes.add(curEdge.nodes.get(0));
                    edges.add(revEdge);
                }

                Collections.sort(edges);

                for(int i=0; i<edges.size(); i++){
                    Edge curEdge = edges.get(i);
                    writer.println(curEdge.nodes.get(0).label+"->"+curEdge.nodes.get(1).label + ":" + curEdge.weight);

                }
                writer.println("");
            }
            
            ArrayList<Edge> inters = new ArrayList<>();
            UnrootedPhyloTree flexT = (UnrootedPhyloTree)deepClone(T);
            boolean flag = false;
            
            for (Edge e : flexT.edges) {
                boolean internal = true;
                for (Node nn : e.nodes) {
                    if (nn.nbrs.size()<3) {
                        internal = false;
                        break;
                    }
                }
                if (internal) {
                    inters.add(e);
                }
            }
            for (Edge fix : inters) {
                UnrootedPhyloTree copyT = (UnrootedPhyloTree)deepClone(flexT);
                Edge copyFix=null;
                
                for (Edge cE : copyT.edges) {
                    boolean found1 = false;
                    boolean found2 = false;
                    if (cE.nodes.get(0).numlabel==fix.nodes.get(0).numlabel || cE.nodes.get(0).numlabel==fix.nodes.get(1).numlabel) {
                        found1 = true;
                    }
                    if (cE.nodes.get(1).numlabel==fix.nodes.get(0).numlabel || cE.nodes.get(1).numlabel==fix.nodes.get(1).numlabel) {
                        found2 = true;
                    }
                    if (found1 && found2) {
                        copyFix = cE;
                    }
                    
                }
                
                ArrayList<UnrootedPhyloTree> Ts = NearestNeighbor(copyT, copyFix);

                
                for (UnrootedPhyloTree t : Ts){
                    UnrootedPhyloTree unlabeledt = (UnrootedPhyloTree)deepClone(t);
                    rootify(t);
                    int sum = 0;
                    for(int i=0; i<len; i++){
                        sum += SmallParsimony(t,i);
                    }
                    derootify(t);

                    if (sum<score) {
                        flag = true;
                        score = sum;
                        
                        // MOVE ON
                        T = unlabeledt;
                        
                        
                        for (Edge e : t.edges) {
                            e.weight = e.nodes.get(0).hamming(e.nodes.get(1));
                        }


                        
                        ArrayList<Edge> edges = new ArrayList<Edge>(t.edges);

                        writer.println(score);

                        for(int i=0; i<t.edges.size(); i++){
                            Edge curEdge = t.edges.get(i);
                            Edge revEdge = new Edge(Integer.MAX_VALUE,false);
                            revEdge.weight = curEdge.weight;
                            revEdge.nodes.add(curEdge.nodes.get(1));
                            revEdge.nodes.add(curEdge.nodes.get(0));
                            edges.add(revEdge);
                        }

                        Collections.sort(edges);

                        for(int i=0; i<edges.size(); i++){
                            Edge curEdge = edges.get(i);
                            writer.println(curEdge.nodes.get(0).label+"->"+curEdge.nodes.get(1).label + ":" + curEdge.weight);

                        }
                        writer.println("");
                    }
                        
                        
                        

                }
            }
            
            
            
            if (!flag) {
                break;
            }
        }
    
    writer.close();
    }
    
    public static void quiz() throws IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/test.txt");
        HashMap<Integer,HashMap<Integer,Float>> D = Parser.IntoFDMat(list);
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        writer.println(NJDMat(D));
        writer.close();
    }
}
      

