package bio2;

import java.io.IOException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author nam
 */
public class Yeast {
    
    public static float distanceBetween(float[] a, float[] b) {
    
        float dist = 0;
        if (a.length==b.length){
            for (int i=0; i<a.length; i++) {
                dist += Math.pow(a[i]-b[i],2);
            }
            dist = (float)Math.sqrt(dist);
        } else {
            return -1;
        }
        return dist;
    }
    
    public static float dWRTCenters (float[] dataPoint, ArrayList<float[]> centers) {
        float mind = 99999f;
        float[] minp = null;
        for (float[] c : centers) {
            float d = distanceBetween(dataPoint, c);
            if (d<mind) {
                mind = d;
                minp = c;
            }
        }
        return mind;
    }
    
    public static float[] pMaxDWRTCenters (ArrayList<float[]> Data, ArrayList<float[]> centers) {
        float maxd = 0f;
        float[] maxp = null;
        for (float[] dataPoint : Data) {
            if (!centers.contains(dataPoint)){
                float d = dWRTCenters(dataPoint, centers);
                if (d>maxd) {
                    maxd = d;
                    maxp = dataPoint;
                }
            }
        }
        return maxp;
    }
    
    public static ArrayList<float[]> FarthestFirstTraversal(ArrayList<float[]> Data, int k){
        float[] dataPoint = Data.get(0);
        ArrayList<float[]> centers = new ArrayList<>();
        centers.add(dataPoint);
        while (centers.size()<k) {
            dataPoint = pMaxDWRTCenters(Data, centers);
            centers.add(dataPoint);
        }
        return centers;
    }
    
    public static void problem1() throws IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/sample.txt");
        String[] lineArr = list.remove(0).split(" ");
        int k = Integer.parseInt(lineArr[0]);
        int n = Integer.parseInt(lineArr[1]);
        
        
        ArrayList<float[]> Data = Parser.ParseFloatDataPoints(list, n);
        ArrayList<float[]> cs = FarthestFirstTraversal(Data, k);
        
        Outputter.printOut(cs);
        
    }
    
}
