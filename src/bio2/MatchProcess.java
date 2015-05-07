/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author USER1
 */

public class MatchProcess {
    
    public int mismatch;
    public String pattern;
    public String str;
    public int lexpos; //lexicographical position (current)
    public ArrayDeque<BWLetter> q;
    
    public int step=0;
    public int k;
    
    int patInd;
    
    public MatchProcess(BWLetter BWL, String p) {
        this.q = new ArrayDeque<>();
        this.q.addFirst(BWL);
        this.pattern = p;
        this.str = BWL.str;
        this.step = 0;
    
        patInd = pattern.length()-1;
        String patStr = ""+pattern.charAt(patInd);
        if (!BWL.str.equals(patStr)) {
            this.mismatch=1;
        } else {
            this.mismatch = 0;
        }
        
    }
    
    public boolean proceed() { //returns true if steps completed
        
        this.step++;
        
        BWLetter curBWL = this.q.peekFirst();
        BWLetter nextBWL = curBWL.head;
        if (nextBWL==null) {
            this.mismatch=Integer.MAX_VALUE;
        } else {
            String nextStr = nextBWL.str;
            patInd = (pattern.length()-1) - this.step;
            String patStr = ""+pattern.charAt(patInd);
            
            if (!nextStr.equals(patStr)) {
                this.mismatch++;
            }
            
            this.q.addFirst(nextBWL);
            this.update();
            
        }
        
        if (this.step>=this.pattern.length()-1) {
            return true;
        } else {
            return false;
        }
    }
    
    public ArrayDeque<BWLetter> getQ () {
        return this.q;
    }
    
    public void update() {
        this.str = "";
        Iterator<BWLetter> BWIt = this.q.iterator();
        while (BWIt.hasNext()){
            this.str+=BWIt.next().str;
        }
    }
    
}
