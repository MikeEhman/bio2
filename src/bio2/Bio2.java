/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author USER1
 */
public class Bio2 {

    /**
     * @param args the command line arguments
     */
    public static Graph TestGraph() {
        Graph G;
        G = new Graph(true);
        Node n = G.MakeNewNode();
        n.label = "n";
        Node m = G.MakeNewNode();
        m.label = "m";
        Node l = G.MakeNewNode();
        l.label = "l";
        Node o = G.MakeNewNode();
        o.label = "o";
        Edge e = G.MakeNewEdge();
        G.d_connect(e,n,m);
        return G;
    }
    
    public static String PrefixTrieMatching(String text, Trie T){
        int i = 0;
        String symbol = ""+text.charAt(0);
        Node v = T.root;
        String pattern = "";
        while(i<text.length()){
           
            if(v.outgoing.size()==0){ // a.k.a. if v is a leaf
                return pattern;
                
            } else {
                boolean hasTraceableEdge = false;
                Iterator<Edge> EdgeIt = v.e_outs.iterator();
                while(EdgeIt.hasNext()){ //for each edge
                    Edge e = EdgeIt.next();
                    if(e.label.equals(symbol)){
                        hasTraceableEdge = true;
                        v = e.dst;
                        pattern += e.label;
                        if(i<text.length()-1) {
                            symbol = ""+text.charAt(++i);
                        }
                        break;
                    }
                }
                
                if(!hasTraceableEdge){
                    return null;
                }
                
                
            }       
        }
        return null;
    }
    
    public static ArrayList<Integer> TrieMatching(String text, Trie T) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        int i=0;
        while(text.length()>1) {
            if(PrefixTrieMatching(text, T)!=null){
                indexList.add(i);
            }
            text = text.substring(1);
            i++;
        }
        return indexList;
    }
    
    public static String LongestRepeat(String t){
        String longestRepeat="fuck you";
        String text = ""+t;
        int original_length = text.length();
        ArrayList<String> p = new ArrayList<String>();
        p.add(text);
        for (int i=0; i<original_length-1; i++){
            text = text.substring(1);
            p.add(text);
        }
        Trie T = new Trie(true);
        T.BuildOnPatterns2(p);
        SuffixTree TT = new SuffixTree(true);
        TT = TT.EatTrie(T);
        System.out.println("Suffix Tree Built");
        System.out.println(TT.nodes.toString());
        System.out.println(TT.edges.toString());
        // NOTE: 0 - start position; 1 - finish position
        
        
        
        // traverse Suffix Tree by DFS and find
        // candidates for longest repeat (root to last branching)
        ArrayDeque<Node> Q = new ArrayDeque<>();
        Q.addFirst(TT.root);
        Set<Node> candidates = new HashSet<>();
        while (Q.size()>0) {
        // DFS -> Trace through Suffix Tree
        // Non-leafs have -1 as numlabel
            Node v = Q.removeFirst();
            // identify if fits criteria: "LAST BRANCHING"
            
            boolean lastbranching = true;
            for (int i=0; i<v.e_outs.size(); i++) {
                Node w = v.e_outs.get(i).dst;
                Q.addFirst(w);
                // if any child is NOT leaf
                if (w.e_outs.size()>0) lastbranching=false;
            }
            // also shouldn't be a leaf (means concat has only one occurrence)
            
            if (v.e_outs.size()<=0) lastbranching=false;
            if (v.equals(TT.root)) lastbranching =false;
            if (lastbranching) candidates.add(v);
        }
        
        // Among the last-branching nodes,
        // which one has the longest path from root?
        
        // NOTE: since it's a legit tree, only one e_in
        
        Iterator<Node> nodeIt = candidates.iterator();
        ArrayDeque<Node> R = new ArrayDeque<>(); //queue to backtrack from candidates
        Node chosenOne = null;
        int max_len = 0;
        int max_start = -1;
        Edge max_rootedge = null;
        longestRepeat = "";
        while(nodeIt.hasNext()){
            // backtrack each candidate
            Node n = nodeIt.next();
            R.addFirst(n);
            String currentStr ="";
            int currentLen = 0;
            int currentStart = n.e_ins.get(0).position;
            Edge e_from_root = null;
            while(R.size()>0) {
                Node w = R.removeFirst();
                Edge e = w.e_ins.get(0);
                Node v = e.src;
                currentLen += e.len;
                currentStr = t.substring(e.position, e.position+e.len) + currentStr;
                if (!v.equals(TT.root)) {
                    R.addFirst(v);
                }
                else {
                    e_from_root = e;
                    break;
                }
                // add length
                
            }
            
            if (currentLen>max_len) {
                max_len = currentLen;
                max_start = currentStart;
                max_rootedge = e_from_root;
                longestRepeat = currentStr;
                
                
            }
            
        }
        
        
        return longestRepeat;
    
    }
    
    
    public static String[] BurrowsWheeler(String text) {
        ArrayList<String> cyc = new ArrayList<>();
        for (int i=0; i<text.length(); i++) {
            cyc.add(text);
            text = text.substring(1) + text.charAt(0);
        }
        Collections.sort(cyc);
        String[] BWarr = {"",""};
        String BW = "";
        for (int i=0; i<cyc.size(); i++) {
            BWarr[0] += cyc.get(i).charAt(0);
            BWarr[1] += cyc.get(i).charAt(text.length()-1);
        }
        
        return BWarr;
    }
    
    public static int[][] ParseBW (String[] BWarr) {
        HashMap<String, Integer> firstMap = new HashMap<>();
        HashMap<String, Integer> lastMap = new HashMap<>();
        
        // initialize maps
        for (int i=0; i<BWarr[0].length(); i++) {
            firstMap.put(""+BWarr[0].charAt(i),1);
            lastMap.put(""+BWarr[1].charAt(i),1);
        }
        
        int[] firstArr = new int[BWarr[0].length()]; 
        int[] lastArr = new int[BWarr[1].length()];
        
        // fill maps
        for (int i=0; i<BWarr[0].length(); i++) {
            String str = ""+BWarr[0].charAt(i);
            int num = firstMap.get(str);
            firstArr[i] = num++;
            firstMap.put(str, num);
            
            str = ""+BWarr[1].charAt(i);
            num = lastMap.get(str);
            lastArr[i] = num++;
            lastMap.put(str, num);
        }
        
        
        int[][] occArr = {firstArr, lastArr};
        
        return occArr;
    }
    
    public static String InvertBW(String last) {
        String oText = "";
        
        String first = "";
        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<last.length(); i++) {
            list.add(""+last.charAt(i));
        }
        
        Collections.sort(list);
        
        for (int i=0; i<list.size(); i++){
            first += list.get(i);
        }
        
        String[] arr = {first,last};
        int[][] occArr = ParseBW(arr);
        
        int[] firstArr = occArr[0];
        int[] lastArr = occArr[1];
        
        // PROCEDURE
        // fOcr = first String's occurrence number
        // lOcr = last String's occurrence number
        
        ArrayDeque<Integer> Q = new ArrayDeque<>();
        // Store queue of indices
        Set<Integer> visited = new HashSet<Integer>();
        Q.addFirst(0);
        visited.add(0);
        while (Q.size()>0) {
            int i = Q.removeFirst();
            String currentSym = ""+first.charAt(i);
            int currentOcr = firstArr[i];
            int j=-1;
            String nextSym = "";
            int nextOcr=-1;
            for (int t=0; t<last.length(); t++) {
                nextSym = ""+last.charAt(t);
                nextOcr = lastArr[t];
                if (currentSym.equals(nextSym) && currentOcr==nextOcr) {
                    j=t;
                    break;
                }
            }
            
            String addSym = ""+first.charAt(j);
            oText = oText + addSym;
            if (!visited.contains(j)) {
                Q.addFirst(j);
                visited.add(j);
            }
        }
        
        return oText;
    }
    
    public static ArrayList<String> BWLnameize(String str) {
        ArrayList<String> nameList = new ArrayList<>();
        HashMap<String, Integer> countMap = new HashMap<>();
        for (int i=0; i<str.length(); i++) {
            countMap.put(""+str.charAt(i),0);
        }
        for (int i=0; i<str.length(); i++) {
            String curSym = "" + str.charAt(i);
            int num = countMap.get(curSym);
            countMap.put(curSym,++num);
            nameList.add(curSym+num);
        }
        return nameList;
    }
    
    public static ArrayList<BWLetter> BWLize(String str) {
        ArrayList<BWLetter> BWLList = new ArrayList<>();
        HashMap<String, Integer> countMap = new HashMap<>();
        for (int i=0; i<str.length(); i++) {
            countMap.put(""+str.charAt(i),1);
        }
        for (int i=0; i<str.length(); i++) {
            String curSym = ""+str.charAt(i);
            int num = countMap.get(curSym);
            BWLetter newBWL = new BWLetter();
            newBWL.str = curSym;
            newBWL.rep = num;
            newBWL.index = i;
            BWLList.add(newBWL);
            
            countMap.put(curSym,++num);
            
        }
        return BWLList;
    }
    
    public static HashMap<String,BWLetter> BWLMap (ArrayList<BWLetter> BWLList) {
        HashMap<String,BWLetter> name_to_BWL = new HashMap<>();
        // scan through arraylist, produce name, put object references
        for (int i=0; i<BWLList.size(); i++) {
            String name = "";
            BWLetter BWL = BWLList.get(i);
            name += BWL.str + BWL.rep;
            name_to_BWL.put(name,BWL);
        }
        return name_to_BWL;
    }
    
    public static int BWMatching(String first, String last, String pattern){
        try {
        ArrayList<String> lastNames = BWLnameize(last);
        ArrayList<BWLetter> firstBWLs = BWLize(first);
        HashMap<String,BWLetter> map = BWLMap(firstBWLs);
        
        ArrayDeque<String> patternDeque = new ArrayDeque<>();
        for (int i=0; i<pattern.length(); i++) {
            patternDeque.addLast(""+pattern.charAt(i));
        }
        int top = 0;
        int bottom = last.length() - 1;
        String symbol;
        
        while (top<=bottom) {
            if (patternDeque.size()>0) {
                symbol = patternDeque.removeLast();
                int topindex=top;
                int bottomindex=bottom;
                
                String sub = last.substring(top,bottom+1); 
                if (!sub.contains(symbol)) return 0;
                
                for (int i=top;i<=bottom;i++){
                    if(symbol.equals(""+last.charAt(i))){
                        topindex=i;
                        break;
                    }
                }
                for (int i=bottom;i>=top;i--){
                    if(symbol.equals(""+last.charAt(i))){
                        bottomindex=i;
                        break;
                    }
                }
                top = map.get(lastNames.get(topindex)).index;
                bottom = map.get(lastNames.get(bottomindex)).index;
            }
            else return bottom-top+1;
        }
        return bottom-top+1;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("fuck");
        }
        return 0;
    }
    
    public static HashMap<String,ArrayList<Integer>> MakeCountArray(String text) {
        HashMap<String, ArrayList<Integer>> countArrMap = new HashMap<>();
        ArrayList<String> syms = new ArrayList<>();
        for (int i=0; i<text.length(); i++) {
            String curSym = ""+text.charAt(i);
            if(!syms.contains(curSym)) {
                syms.add(curSym);
                ArrayList<Integer> newList = new ArrayList<Integer>();
                //placehold with bunch of 0s
                for(int j=0; j<text.length()+1; j++){
                    newList.add(0);
                }
                countArrMap.put(curSym, newList);
            }
        }
        
        for(int i=0; i<text.length(); i++){
            String curSym = ""+text.charAt(i);
            ArrayList<Integer> curList = countArrMap.get(curSym);
            int curNum = curList.get(i)+1;
            for(int j=i; j<curList.size()-1; j++){
                curList.set(j+1,curNum);
            }
        }
        return countArrMap;
    }
    
    public static HashMap<String,HashMap<Integer,Integer>> MakeCPMap(String text,int c) {
        HashMap<String,ArrayList<Integer>> CountArrMap = MakeCountArray(text);
        HashMap<String,HashMap<Integer,Integer>> CPMap = new HashMap<String,HashMap<Integer,Integer>>();
        
        Iterator<String> strIt = CountArrMap.keySet().iterator();
        while (strIt.hasNext()) {
            String curSym = strIt.next();
            HashMap<Integer,Integer> curMap = new HashMap<Integer,Integer>();
            ArrayList<Integer> curList = CountArrMap.get(curSym);
            for(int i=0; i<curList.size(); i++){
                if (i%c==0) {
                    curMap.put(i,curList.get(i));
                }
            }
            CPMap.put(curSym, curMap);
            
        }
        
        
        return CPMap;
    }
    
    public static HashMap<String,Integer> MakeFOMap (String text) {
        HashSet<String> visited = new HashSet<>();
        HashMap<String,Integer> FOMap = new HashMap<>();
        for(int i=0; i<text.length(); i++){
            String curSym = ""+text.charAt(i);
            if (!visited.contains(curSym)) {
                visited.add(curSym);
                FOMap.put(curSym,i);
            }
        }
        return FOMap;
    }
    
        public static String sortedStr(String oText) {
        String first = "";
        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<oText.length(); i++) {
            list.add(""+oText.charAt(i));
        }
        
        Collections.sort(list);
        
        for (int i=0; i<list.size(); i++){
            first += list.get(i);
        }
        
        return first;
    }
    
    public static HashMap<Integer,Integer> MakePSMap(String text, int k) {
        HashMap<Integer,Integer> PSMap = new HashMap<>();
        SuffixArray SA = new SuffixArray(text);
        for(int i=0; i<SA.size(); i++){
            if (SA.get(i)%k==0) {
                PSMap.put(i, SA.get(i));
            }
        }
        return PSMap;
    } 
    
    
        
        
    public static ArrayList<Integer> BetterBWMatching(String last,ArrayList<String> patterns) {
        ArrayList<Integer> ans = new ArrayList<>();
        String first = sortedStr(last);
        HashMap<String,Integer> FOMap = MakeFOMap(first);
        HashMap<String,ArrayList<Integer>> CountArrMap = MakeCountArray(last);
        
        for(int t=0; t<patterns.size(); t++){
            int top = 0;
            int bottom = last.length()-1;
            String pattern = patterns.get(t);
        
            String[] O = pattern.split("");
            ArrayDeque<String> Q = new ArrayDeque(Arrays.asList(O));

            while (top<=bottom) {
                if (Q.size()>0) {
                    String curSym = Q.removeLast();
                    String sub = last.substring(top,bottom+1);
                    if (sub.contains(curSym)) {
                        top = FOMap.get(curSym) + CountArrMap.get(curSym).get(top);
                        bottom = FOMap.get(curSym) + CountArrMap.get(curSym).get(bottom+1) - 1;
                    }
                    else{ 
                        ans.add(0); 
                        break;
                    }
                }
                else{ 
                    ans.add(bottom - top + 1); 
                    break;
                }
            }
        }
        return ans;
    }
    
    public static ArrayList<Integer> EvenBetterBWMatching(String text,ArrayList<String> patterns, int k) {
        ArrayList<Integer> ans = new ArrayList<>();
        String[] BW = BurrowsWheeler(text);
        String first = BW[0];
        String last = BW[1];
        ArrayList<BWLetter> FirstBWList = BWLize(first);
        ArrayList<String> LastBWNames = BWLnameize(last);
        HashMap<String, BWLetter> BWLM = BWLMap(FirstBWList);
        
        HashMap<String,Integer> FOMap = MakeFOMap(first);
        HashMap<String,HashMap<Integer,Integer>> CPMap = MakeCPMap(last, k);
        PartialSuffixMap PSMap = new PartialSuffixMap(text, k);
        
        for(int t=0; t<patterns.size(); t++){
            int top = 0;
            int bottom = last.length()-1;
            String pattern = patterns.get(t);
        
            String[] O = pattern.split("");
            ArrayDeque<String> Q = new ArrayDeque(Arrays.asList(O));

            while (top<=bottom) {
                if (Q.size()>0) {
                    String curSym = Q.removeLast();
                    String sub = last.substring(top,bottom+1);
                    if (sub.contains(curSym)) {
                       
                        HashMap<Integer,Integer> curMap = CPMap.get(curSym);
                        
                        int td = top%k;
                        int bd = bottom%k;
                        
                        // look at CPMap at floor index
                        int ti = top-td; //top floor index
                        int bi = bottom-bd; //bottom floor index
                        
                        // figure out how many there at the floor index
                        int tc = curMap.get(ti); //top count
                        int bc = curMap.get(bi); //bottom count
                        
                        for(int tii=ti;tii<top;tii++) {
                            if (curSym.equals(""+last.charAt(tii))) {
                                tc++;
                            }
                        }
                        
                        for(int bii=bi;bii<=bottom;bii++) {
                            if (curSym.equals(""+last.charAt(bii))) {
                                bc++;
                            }
                        }            
                        
                        top = FOMap.get(curSym) + tc; // top final number
                        bottom = FOMap.get(curSym) + bc - 1; // bottom final number
                        
                        
                        
                    }
                    else{ 
                        break;
                    }
                }
                else{ 
                    // use PSMap to backwalk and find out original indices
                    for(int i=top; i<=bottom; i++){
                        int index = -1;
                        int backwalk = 0;
                        
                        BWLetter curFirstBWL = FirstBWList.get(i);
                        String lastLetterName = LastBWNames.get(i);
                        BWLetter curLastBWL = BWLM.get(lastLetterName);
                        int curInd = curLastBWL.index;
                       
                        String curStr = curFirstBWL.str + curFirstBWL.rep;
                       
                        while (!PSMap.keySet().contains(curStr)) {
                            backwalk++;
                            
                            curInd = curLastBWL.index;
                            curFirstBWL = FirstBWList.get(curInd);
                            lastLetterName = LastBWNames.get(curInd);
                            curLastBWL = BWLM.get(lastLetterName);
                            
                            curStr = curFirstBWL.str + curFirstBWL.rep;
                            
                        }
                        index = PSMap.get(curStr) + backwalk;
                        ans.add(index);
                    }
                    break;
                }
            }
        }
        return ans;
    }
    
    public static ArrayList<BWLetter> BWLReconstruct (String text) {
        String[] BW = Bio2.BurrowsWheeler(text);
        
        ArrayList<BWLetter> firstBWLs = BWLize(BW[0]);
        ArrayList<String> lastBWLNames = BWLnameize(BW[1]);
        HashMap<String,BWLetter> BWLM = BWLMap(firstBWLs);
        
        ArrayDeque<BWLetter> Q = new ArrayDeque<>(firstBWLs);
        ArrayDeque<BWLetter> reconst = new ArrayDeque<>();
        
        BWLetter u = Q.removeFirst(); // assume $ sign comes first always
        reconst.addFirst(u);
        while (Q.size()>0) {
            u = BWLM.get(lastBWLNames.get(u.index));
            reconst.addFirst(u);
            Q.remove(u);
        }
        
        ArrayList<BWLetter> reconst2 = new ArrayList<BWLetter>(reconst);
        reconst = null;
        
        for(int i=0; i<reconst2.size()-1; i++){
            u = reconst2.get(i);
            BWLetter v = reconst2.get(i+1);
            u.tail = v;
            u.tindex = i;
            v.head = u;
            v.tindex = i+1;
        }
        
        return reconst2;
    }
    
    public static ArrayList<Integer> MismatchBWMatching(String text,ArrayList<String> patterns, int k, int d) {
        ArrayList<Integer> ans = new ArrayList<>();
        String[] BW = BurrowsWheeler(text);
        String first = BW[0];
        String last = BW[1];
        
        // power of reconstruct shines
        ArrayList<BWLetter> rTextBWLs = BWLReconstruct(text);
        ArrayList<String> FirstBWNames = BWLnameize(first);
        ArrayList<String> LastBWNames = BWLnameize(last);
        HashMap<String, BWLetter> BWLM = BWLMap(rTextBWLs);
        
        for(int t=0; t<patterns.size(); t++){
            // for each pattern
            // create new match process objects
            // |text| processes at most (worst case)
            String pattern = patterns.get(t);
            
            // begin process by making new process for each of last column BWL
            MatchProcess newProc;
            ArrayDeque<MatchProcess> Procs = new ArrayDeque<MatchProcess>();
            
            // below initiates |text| number of match processes
            for(int i=0; i<LastBWNames.size(); i++){
                String curName = LastBWNames.get(i);
                BWLetter curBWL = BWLM.get(curName);
                newProc = new MatchProcess(curBWL, pattern);
                Procs.add(newProc);
            }
            
            // for each match process, do the matching
            while (Procs.size()>0) {
                MatchProcess curProc = Procs.removeFirst();
                Procs.addFirst(curProc); //STC
                
                // if mismatch exceeds limit, eliminate process
                if (curProc.mismatch > d) {
                    Procs.remove(curProc);
                    curProc = null;
                } else {
                    boolean flag = curProc.proceed();
                    if (flag) {
                        if (curProc.mismatch <= d){
                            ans.add(curProc.q.peekFirst().tindex);
                        }
                        Procs.remove(curProc);
                        curProc = null;
                    }
                }
            }
                
        System.out.println(t);        
        }
        return ans;
    }
    
    public static ArrayList<Integer> DistanceBetweenLeaves(Graph G){
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Node> leaves = new ArrayList<>();
        for(int i=0; i<G.nodes.size(); i++){
            Node n = G.nodes.get(i);
            if (n.e_outs.size()<=1) {
                leaves.add(n);
            }
        }        
        Collections.sort(leaves);
        Iterator<Node> nodeIt = leaves.iterator();
        while (nodeIt.hasNext()) {
            HashMap<Node, Integer> distanceTo = new HashMap<>();
            Node n = nodeIt.next();
            
            distanceTo.put(n,0);
            ArrayDeque<Node> Q = new ArrayDeque<>();
            Q.addFirst(n);
            Set<Node> visited = new HashSet<>();
            visited.add(n);
            while (Q.size()>0) {
                Node v = Q.removeFirst();
                for(int i=0; i<v.e_outs.size(); i++){
                    Edge vw = v.e_outs.get(i);
                    Node w = vw.dst;
                    if (!visited.contains(w)) {
                        Q.addLast(w);
                        visited.add(w);
                        distanceTo.put(w, distanceTo.get(v)+vw.weight);
                    }
                }
            }
            
            for(int i=0; i<leaves.size(); i++){
                Node l = leaves.get(i);
                list.add(distanceTo.get(l));
            }
            
        }
        return list;
    }
    
    public static void test1() {
    
        HashMap<String,ArrayList<Integer>> CountArrMap = MakeCountArray("shititreddit$");
        HashMap<String,Integer> FOMap = MakeFOMap("shititreddit$");
        System.out.println(CountArrMap.toString());
        
    }
    
    public static void test2() throws IOException {
        ArrayList<String> P = Parser.ParseTextIntoList("src/bio2/dataset_301_7.txt");
        String last = P.remove(0);
        String a = P.remove(0);
        String[] arr = a.split(" ");
        P = new ArrayList<>(Arrays.<String>asList(arr));
        ArrayList<Integer> indices = BetterBWMatching(last, P);
        
        
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        for(int i=0; i<indices.size(); i++){
            writer.print(indices.get(i)+" ");
        }
        writer.close();
    }
    
    public static void test3() {
        //HashMap<String,HashMap<Integer,Integer>> CPMap = MakeCPMap("shititreddit$",5);
//        HashMap<Integer,Integer> PSMap = MakePSMap("panamabananas$",5);
        PartialSuffixMap PSMap = new PartialSuffixMap("panamabananas$",5);
        ArrayList<String> ps = new ArrayList<>();
        ps.add("ana");
        ArrayList<Integer> results = EvenBetterBWMatching("panamabananas$",ps,5);
//        HashMap CPMap = MakeCPMap("smnpbnnaaaaa$a",5);
        int i = 0;
    }

    public static void test4() throws IOException {
        ArrayList<String> ps = Parser.ParseTextIntoList("src/bio2/dataset_304_6.txt");
        String p = ps.remove(0)+"$";
        int d = Integer.parseInt(ps.remove(ps.size()-1));
        ArrayList<String> pp = new ArrayList<String>(Arrays.<String>asList(ps.get(0).split(" ")));
        ArrayList<Integer> is = MismatchBWMatching(p, pp, 5, d);
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        Collections.sort(is);
        for(int i=0; i<is.size(); i++){
            writer.print(is.get(i) + " ");
        }
        writer.close();
    }
    
    public static void test5() throws IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10328_11.txt");
        int numLeaves = Integer.parseInt(list.remove(0));
        Set<Integer> visited = new HashSet<>();
        Graph G = new Graph(true);
        for(int i=0; i<list.size(); i++){
            String line = list.get(i);
            String[] line2 = line.split(":");
            String[] line3 = line2[0].split("->");
            
            int srcNum = Integer.parseInt(line3[0]);
            int dstNum = Integer.parseInt(line3[1]);
            int wgt = Integer.parseInt(line2[1]);
            if (!visited.contains(srcNum)) {
                visited.add(srcNum);
                Node src = G.MakeNewNode();
                G.node_set_numlabel(src, srcNum);
            }
            
            if (!visited.contains(dstNum)) {
                visited.add(dstNum);
                Node dst = G.MakeNewNode();
                dst.numlabel = dstNum;
                G.node_set_numlabel(dst, dstNum);
            }
            
            Edge newEdge = G.MakeNewEdge();
            G.d_connect(newEdge, G.numNodeMap.get(srcNum), G.numNodeMap.get(dstNum));
            newEdge.weight = wgt;
            
        }
        
        System.out.println(G.toString());
        
        ArrayList<Integer> result = DistanceBetweenLeaves(G);
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        for(int i=0; i<result.size(); i++){
            if(i%numLeaves==0) {
                writer.print("\n");
            }
            writer.print(result.get(i) + " ");
        }
        writer.close();
    }
    
    
    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        
        Yeast.quiz1();
        
    }
    
}
