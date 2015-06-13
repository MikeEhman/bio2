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
 * @author USER1
 */
public class Cluster {
    
    public Node head;
    public ArrayList<Node> leaves;
    
    public Cluster(Node h, ArrayList<Node> n) {
        this.head = h;
        this.leaves = n;
    }
    
    public float distanceTo(Cluster c, HashMap<Integer,HashMap<Integer,Float>> DMat) {
        float dist = DMat.get(this.head.numlabel).get(c.head.numlabel);
        
        return dist;
    }
    
}
