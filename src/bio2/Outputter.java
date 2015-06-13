/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bio2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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
    
    public static String ProcessStrStrDblMat (HashMap<String,HashMap<String,Double>> mat) {
        // 1st keys -> columns
        // 2nd keys -> rows
        // values -> entries
        
        String buffer = "";
        ArrayList<String> rows = new ArrayList<>();
        
        
        ArrayList<String> keyList1 = new ArrayList<>(mat.keySet());
        ArrayList<String> keyList2 = new ArrayList<>(mat.get(keyList1.get(0)).keySet());
        
        Collections.sort(keyList1, new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            if (o1.equals("S")) {
                return -1;
            } else if (o1.equals("E")) {
                return 1;
            } else if (o2.equals("S")) {
                return 1;
            } else if (o2.equals("E")) {
                return -1;
            } else {
                if (o1.length()>=2 && o2.length()>=2){
                    if (o1.charAt(1)==o2.charAt(1)) {
                        if (o1.charAt(0)=='M') {
                            return -1;
                        }
                        if (o1.charAt(0)=='D' && o2.charAt(0)=='M') {
                            return 1;
                        }
                        if (o1.charAt(0)=='D' && o2.charAt(0)=='I') {
                            return -1;
                        }
                        if (o1.charAt(0)=='I') {
                            return 1;
                        }
                    }
                    return Character.compare(o1.charAt(1), o2.charAt(1));
                }
                return 0;
            }}
        });
        Collections.sort(keyList2, new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            if (o1.equals("S")) {
                return -1;
            } else if (o1.equals("E")) {
                return 1;
            } else if (o2.equals("S")) {
                return 1;
            } else if (o2.equals("E")) {
                return -1;
            } else {
                if (o1.length()>=2 && o2.length()>=2){
                    if (o1.charAt(1)==o2.charAt(1)) {
                        if (o1.charAt(0)=='M') {
                            return -1;
                        }
                        if (o1.charAt(0)=='D' && o2.charAt(0)=='M') {
                            return 1;
                        }
                        if (o1.charAt(0)=='D' && o2.charAt(0)=='I') {
                            return -1;
                        }
                        if (o1.charAt(0)=='I') {
                            return 1;
                        }
                    }
                    return Character.compare(o1.charAt(1), o2.charAt(1));
                }
                return 0;
            }}
        });
        
        String toprow = "";
        for (String key2 : keyList2){
            toprow += "\t" + key2;
        }
        rows.add(toprow);
        
        for (String key1 : keyList1) {
            rows.add(key1);
        }
        
        // now for entries
        for (int i=0; i<keyList1.size(); i++) {
            String key1 = keyList1.get(i);
            String curRow = rows.get(i+1);
            for (int j=0 ; j<keyList2.size(); j++) {
                String key2 = keyList2.get(j);
                double num = mat.get(key1).get(key2);
                String numStr = "";
                if (num==0d) {
                    numStr = "0";
                } else {
                    numStr = Double.toString((double)Math.round(num*1000)/1000);
                }
                
                curRow += "\t" + numStr;
            }
            rows.set(i+1, curRow);
        }
        
        for (String r : rows) {
            buffer+=r+"\n";
        }
        return buffer;
    }
    
    public static void OutputHMM (HMM H) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("src/bio2/output.txt");
        
        HashMap<String,HashMap<String,Double>> T = H.transition;
        HashMap<String,HashMap<String,Double>> E = H.emission;
        
        String tbuff = ProcessStrStrDblMat(T);
        String ebuff = ProcessStrStrDblMat(E);
        
        writer.write(tbuff);
        writer.println("--------");
        writer.write(ebuff);
        writer.close();
    }
    
}
