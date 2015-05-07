/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

/**
 *
 * @author USER1
 */
public class CandidatePattern { 
    public int startIndex;
    public int endIndex;
    public String str;
    public int matchStartIndex;
    public int matchEndIndex;
    
    public CandidatePattern (String str, int s, int e) {
        this.startIndex = s;
        this.endIndex = e;
        this.str = str;
    }
}
