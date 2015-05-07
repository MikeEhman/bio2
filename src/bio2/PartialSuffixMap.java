/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author USER1
 */
public class PartialSuffixMap extends HashMap<String,Integer> {
    
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
    
    public static ArrayDeque<BWLetter> BWLReconstruct (String text) {
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
        return reconst;
    }
    
    
    public PartialSuffixMap(String text, int c) {
        ArrayDeque<BWLetter> sortedBWLs = BWLReconstruct(text);
        int i = 0;
        while (sortedBWLs.size()>0) {
            BWLetter curBWL = sortedBWLs.removeFirst();
            if (i%c==0) {
                this.put(curBWL.str + curBWL.rep,i);
            }
            i++;
        }
        
        i = 0;
    }
}
