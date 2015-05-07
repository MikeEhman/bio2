/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author USER1
 */
public class SuffixArray extends ArrayList<Integer>{
       
    public SuffixArray (String text) {
        ArrayList<String> S = new ArrayList<>();
        HashMap<String, Integer> SALexMap = new HashMap<>();
        ArrayList<String> suffices = new ArrayList<>();
        int originalLength = text.length();
        for (int i=0; i<originalLength; i++){
            SALexMap.put(text, i);
            suffices.add(text);
            text = text.substring(1);
        }
        Collections.sort(suffices);
        for (int i=0; i<suffices.size(); i++) {
                this.add(SALexMap.get(suffices.get(i)));
            
        }
    }
       
}
