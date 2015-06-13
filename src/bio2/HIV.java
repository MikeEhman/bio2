/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author nam
 */
public class HIV {
    public static double HiddenPathProbability(String path, HashMap<String,HashMap<String,Double>> TMat){
        double p = 0.5f;
        for (int i=1; i<path.length(); i++) {
            String pastStr = ""+path.charAt(i-1);
            String nowStr = ""+path.charAt(i);
            double prob = TMat.get(pastStr).get(nowStr);
            p *= prob;
        }
        
        return p;
        
    }
    
    public static void problem1() throws IOException{
        ArrayList<String> list =  Parser.ParseTextIntoList("src/bio2/dataset_11594_2.txt");
        String path = list.get(0);
        String rawStates = list.get(2);
        ArrayList<String> states = new ArrayList<>(Arrays.asList(rawStates.split(" ")));
        
        HashMap<String,HashMap<String,Double>> TMat = new HashMap<>();
        for (String state : states) {
            TMat.put(state, new HashMap<String,Double>());
        }
        for (int i=5; i<list.size(); i++) {
            String curStr = list.get(i);
            ArrayList<String> splitlist = new ArrayList<String>(Arrays.asList(curStr.split("\t")));
            String curStt = splitlist.get(0);
            for (int j=1; j<splitlist.size(); j++) {
                double num = Double.parseDouble(splitlist.get(j));
                TMat.get(states.get(j-1)).put(curStt,num);
            }
        }
        
        System.out.println(TMat);
        Double p = HiddenPathProbability(path, TMat);
        System.out.println(p);
    }

    public static double OutcomeProbability(HashMap<String,HashMap<String,Double>> EMat, String sequence, String hp) {
        double p = 1f;
        for (int i=0 ; i < sequence.length() ; i++) {
            String curStr = ""+ sequence.charAt(i);
            String curStt = ""+ hp.charAt(i);
            double pp = EMat.get(curStt).get(curStr);
            p *= pp;
        }
        System.out.println(p);
        return p;
    }
    
    public static void problem2() throws IOException{
        ArrayList<String> list =  Parser.ParseTextIntoList("src/bio2/dataset_11594_4.txt");
        String str = list.get(0);
        String strCompos = list.get(2);
        String path = list.get(4);
        String pathCompos = list.get(6);
        ArrayList<String> strs = new ArrayList<>(Arrays.asList(strCompos.split(" ")));
        ArrayList<String> states = new ArrayList<>(Arrays.asList(pathCompos.split(" ")));
        
        HashMap<String,HashMap<String,Double>> EMat = new HashMap<>();
        for (String state : states) {
            for (String s : strs) {
                HashMap<String,Double> newMap = new HashMap<>();
                newMap.put(s,-1d);
                EMat.put(state,newMap);
            }
        }
        
        String topRow = list.get(8);
        ArrayList<String> topRowElements = new ArrayList<>(Arrays.asList(topRow.split("\t")));
        topRowElements.remove(0);
        for (int i=9; i<list.size(); i++) {
            String curStr = list.get(i);
            ArrayList<String> splitlist = new ArrayList<String>(Arrays.asList(curStr.split("\t")));
            String curStt = splitlist.remove(0);
            for (int j=0; j<topRowElements.size(); j++) {
                String tpStr = topRowElements.get(j);
                double num = Double.parseDouble(splitlist.get(j));
                EMat.get(curStt).put(tpStr, num);
            }
        }

        double p = OutcomeProbability(EMat, str, path);
        
    }
    
    public static HashMap<String,ArrayList<Double>> s_max_recurse() {
    
        return null;
    }
    
    public static String Decoding (String ePath, ArrayList<String> states, HashMap<String,HashMap<String,Double>> TMat, HashMap<String,HashMap<String,Double>> EMat) {
        String hPath = "";
        HashMap<String,ArrayList<Double>> s = new HashMap<>();
        
        String x1 = ""+ePath.charAt(0);
        for (String k : states) {
            s.put(k, new ArrayList<Double>());
            s.get(k).add(((double)1d/states.size()) * EMat.get(k).get(""+ePath.charAt(0)));
        }
        
        for (int i=1; i<ePath.length(); i++) {
            for (String k : states){
                double maxnum = -1f;
                String maxStt = "fuck";
                for (String l : states) {
                    double num = s.get(l).get(i-1);
                    num *= (double)TMat.get(l).get(k);
                    num *= (double)EMat.get(k).get(""+ePath.charAt(i));
                    if (num > maxnum) {
                        maxnum = num;
                        maxStt = l;
                    }
                }
                s.get(k).add(maxnum);
            }
        }
        
        // backtrack
        double maxP=0;
        String maxStt = "ass";
        int n = ePath.length()-1;
        for (String k : states) {
            if (s.get(k).get(n)>maxP) {
                maxP = s.get(k).get(n);
                maxStt = k;
            }
        }
        hPath = maxStt + hPath;
        
        for (int i=n; i>=1; i--) {
            maxStt = "fuck";
            double maxnum = -1d;
            String curStt = ""+hPath.charAt(0);
            String curStr = ""+ePath.charAt(i);
            double num = s.get(curStt).get(i);
            for (String l : states){
                double pastNum = s.get(l).get(i-1);
                pastNum *= TMat.get(l).get(curStt);
                pastNum *= EMat.get(curStt).get(curStr);
                String currentNumStr = Double.toString(num);
                String pastNumStr = Double.toString(pastNum);
                if (currentNumStr.substring(0,5).equals(pastNumStr.substring(0,5))){
                    maxStt = l;
                }
            }
            hPath = maxStt + hPath;
        }
        return hPath;
    }
    
    public static void problem3() throws IOException{
        ArrayList<String> list =  Parser.ParseTextIntoList("src/bio2/dataset_11594_8.txt");
        String str = list.get(0);
        String strCompos = list.get(2);
        String pathCompos = list.get(4);
        ArrayList<String> strs = new ArrayList<>(Arrays.asList(strCompos.split(" ")));
        ArrayList<String> states = new ArrayList<>(Arrays.asList(pathCompos.split(" ")));
        
        ArrayList<String> rawEMat = new ArrayList<>(list.subList(6,11));
        ArrayList<String> rawTMat = new ArrayList<>(list.subList(12,17));
        
        HashMap<String,HashMap<String,Double>> EMat = Parser.ParseListIntoStringDoubleMatrix(rawEMat);
        HashMap<String,HashMap<String,Double>> TMat = Parser.ParseListIntoStringDoubleMatrix(rawTMat);
        
        String hPath = Decoding(str,states,EMat,TMat);
        System.out.println(hPath);
    }
    
    public static double OutcomeLikelihood(String x, ArrayList<String> states, HashMap<String,HashMap<String,Double>> EMat, HashMap<String,HashMap<String,Double>> TMat){
        HashMap<String,ArrayList<Double>> forward = new HashMap<>();
        for (String k : states) {
            ArrayList<Double> newList = new ArrayList<>();
            newList.add((double)1/states.size() * EMat.get(k).get(""+x.charAt(0)));
            forward.put(k,newList);
        }
        
        for (int i=1 ; i<x.length() ; i++) {
            String xStr = ""+x.charAt(i);
            for (String k : states) {
                double num=0;
                for (String l : states) {
                    num += forward.get(l).get(i-1) * TMat.get(l).get(k) * EMat.get(k).get(xStr);
                }
                forward.get(k).add(num);
            }
        }
        int n = x.length()-1;
        
        double num = 0;
        for (String k : states) {
            num += forward.get(k).get(n);
        }
        
        
        return num;
    }
    
    public static void problem4() throws IOException {
        ArrayList<String> list =  Parser.ParseTextIntoList("src/bio2/dataset_11594_8.txt");
        String str = list.get(0);
        String strCompos = list.get(2);
        String pathCompos = list.get(4);
        
        ArrayList<Integer> sepIndList = new ArrayList<>();
        for (int i = 0 ; i < list.size() ; i++) {
           if (list.get(i).equals("--------")) {
               sepIndList.add(i);
           }
        }
        
        ArrayList<String> strs = new ArrayList<>(Arrays.asList(strCompos.split(" ")));
        ArrayList<String> states = new ArrayList<>(Arrays.asList(pathCompos.split(" ")));
        
        
        int[] TMatIndices = new int[2];
        TMatIndices[0] = sepIndList.get(2)+1;
        TMatIndices[1] = sepIndList.get(3);
        int[] EMatIndices = new int[2];
        EMatIndices[0] = sepIndList.get(3)+1;
        EMatIndices[1] = list.size();
        
        ArrayList<String> rawTMat = new ArrayList<>(list.subList(TMatIndices[0],TMatIndices[1]));
        ArrayList<String> rawEMat = new ArrayList<>(list.subList(EMatIndices[0],EMatIndices[1]));
        
        HashMap<String,HashMap<String,Double>> EMat = Parser.ParseListIntoStringDoubleMatrix(rawEMat);
        HashMap<String,HashMap<String,Double>> TMat = Parser.ParseListIntoStringDoubleMatrix(rawTMat);
        
        double p = OutcomeLikelihood(str,states,EMat,TMat);
        System.out.println(p);
    }
    
    public static HMM ProfileHMM(ArrayList<String> alignment, double threshold, ArrayList<String> sigma) {
        HMM H = new HMM(alignment, threshold, sigma);
        
        return H;
    }
    
    public static void problem5 () throws IOException {
        ArrayList<String> list =  Parser.ParseTextIntoList("src/bio2/dataset_11632_4.txt");
        double threshold = Double.parseDouble(list.get(0));
        ArrayList<String> sigma = new ArrayList<>(Arrays.asList(list.get(2).split(" ")));
        ArrayList<String> alignment = new ArrayList<>(list.subList(4,list.size()));
        sigma.add("-");
        HMM H = ProfileHMM(alignment, threshold, sigma);
        
        Outputter.OutputHMM(H);
        
    }
    
    public static void problem6 () throws IOException {
        ArrayList<String> list =  Parser.ParseTextIntoList("src/bio2/dataset_11632_4.txt");
        String[] t_p = list.get(0).split(" ");
        double threshold = Double.parseDouble(t_p[0]);
        double pseudocount = Double.parseDouble(t_p[1]);
        ArrayList<String> sigma = new ArrayList<>(Arrays.asList(list.get(2).split(" ")));
        ArrayList<String> alignment = new ArrayList<>(list.subList(4,list.size()));
        sigma.add("-");
        HMM H = ProfileHMM(alignment, threshold, sigma);
        H.ApplyPseudocount(pseudocount);
        Outputter.OutputHMM(H);
        
    }   
    
}