/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author nam
 */
public class Outputter {

    public static void printOut(ArrayList<float[]> fs) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        
        for (int i=0; i<fs.size(); i++) {
                float[] p = (float[])fs.get(i);
                for (int j=0; j<p.length; j++){
                    writer.print(p[j] + " ");
                }
                writer.println("");
        }
        writer.close();
        
    }
    
}
