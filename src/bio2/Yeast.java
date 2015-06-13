package bio2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public static float[] minPWRTCenters (float[] dataPoint, ArrayList<float[]> centers) {
        float mind = 99999f;
        float[] minp = null;
        for (float[] c : centers) {
            float d = distanceBetween(dataPoint, c);
            if (d<mind) {
                mind = d;
                minp = c;
            }
        }
        return minp;
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
    
    public static float distortion(ArrayList<float[]> Data, ArrayList<float[]> Centers) {
        float d = 0;
        for (float[] dataPoint : Data) {
            d += (float)Math.pow(dWRTCenters(dataPoint, Centers),2);
        }
        d = d * (1/(float)Data.size());
        return d;
    }
    
    public static float[] cog(ArrayList<float[]> Data) {
        float[] g = new float[Data.get(0).length];
        
        for (int i=0; i<g.length; i++) {
            g[i] = 0;
        }
        
        for (float[] dataPoint : Data) {
            for (int i=0; i<dataPoint.length; i++) {
                g[i] += dataPoint[i]/Data.size();
            }
        }
        
        return g;
    }
    
    public static void problem2() throws IOException{
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10927_3.txt");
        ArrayList<String> sub1 = null;
        ArrayList<String> sub2 = null;
        String[] lineArr = list.remove(0).split(" ");
        int k = Integer.parseInt(lineArr[0]);
        int m = Integer.parseInt(lineArr[1]);
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).equals("--------")) {
                sub1 = new ArrayList<String>(list.subList(0,i));
                sub2 = new ArrayList<String>(list.subList(i+1,list.size()));
                break;
            }
        }
        ArrayList<float[]> Centers = Parser.ParseFloatDataPoints(sub1, m);
        ArrayList<float[]> Data = Parser.ParseFloatDataPoints(sub2, m);
        
        float Disto = distortion(Data,Centers);
        System.out.println(Disto);
    }
    
    public static ArrayList<float[]> Lloyd(ArrayList<float[]> Data, int k) {
        ArrayList<float[]> Centers = new ArrayList<>(Data.subList(0, k));
        ArrayList<DataCluster> DCs = new ArrayList<>();
        HashMap<float[],DataCluster> center_to_DC = new HashMap<>();
        HashMap<float[],DataCluster> dp_to_DC = new HashMap<>();
        
        for (float[] center : Centers) {
                DataCluster newDC = new DataCluster();
                newDC.center=center;
                newDC.dataPoints.add(center);
                center_to_DC.put(center, newDC);
                DCs.add(newDC);
        }
        
        boolean flag = true;
        while (flag) {
            for (float[] dataPoint : Data) {
                float[] closestCenter = minPWRTCenters(dataPoint, Centers);
                DataCluster closestDC = center_to_DC.get(closestCenter);

                if (dp_to_DC.containsKey(dataPoint)){
                    DataCluster originDC = dp_to_DC.get(dataPoint);
                    originDC.dataPoints.remove(dataPoint);
                }

                closestDC.dataPoints.add(dataPoint);
                dp_to_DC.put(dataPoint,closestDC);
            }
            
            flag = false;
            for (DataCluster DC : DCs) {
               float[] newCOG = cog(DC.dataPoints);
               if (newCOG[0]!=DC.center[0] && newCOG[1]!=DC.center[1]) {
                   flag = true;
               } else {
                   System.out.println(newCOG[0] + " " +  newCOG[1]);
               }
               
               DC.dataPoints.remove(DC.center);
               DC.dataPoints.add(newCOG);
               Centers.remove(DC.center);
               Centers.add(newCOG);
               
               center_to_DC.remove(DC.center);
               center_to_DC.put(newCOG,DC);
               DC.center = newCOG;
            }
        }
        ArrayList<float[]> finalCenters = new ArrayList<>();
        for (DataCluster DC : DCs) {
            finalCenters.add(DC.center);
        }
        return finalCenters;
    }
    
    public static void problem3 () throws IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10928_3.txt");
        String[] lineArr = list.remove(0).split(" ");
        int k = Integer.parseInt(lineArr[0]);
        int n = Integer.parseInt(lineArr[1]);
        
        
        ArrayList<float[]> Data = Parser.ParseFloatDataPoints(list, n);
        ArrayList<float[]> cs = Lloyd(Data, k);
        
        Outputter.printOut(cs);
    
    }
    
    public static float dotProduct(float[] a, float[] b) {
        float product = 0;
        for (int i=0 ; i<a.length; i++) {
            product += a[i] * b[i];
        }
        return product;
    }
    
    public static ArrayList<float[]> SoftKMeansClustering (int k, int m, float beta, ArrayList<float[]> Data) {
        // first time
        ArrayList<float[]> Centers = new ArrayList<>(Data.subList(0, k));
        //Data.removeAll(Centers);
        
        // initialize
        ArrayList<float[]> HiddenMatrix = new ArrayList<>();
        for (int i=0; i<Data.size(); i++) {
            HiddenMatrix.add(new float[Data.size()]);
        }
        
        for (int asdf=0; asdf<100; asdf++){
            //E-Step
            for(int i=0; i<k; i++){
                float[] curList = HiddenMatrix.get(i);
                for (int j=0; j<Data.size(); j++) {
                    float newNum = 0;
                    newNum += Math.pow(Math.E,-beta*distanceBetween(Data.get(j),Centers.get(i)));
                    float den = 0;
                    for (float[] c : Centers) {
                        den += Math.pow(Math.E,-beta*distanceBetween(Data.get(j),c));
                    }
                    newNum /= den;
                    curList[j]=newNum;
                }
                HiddenMatrix.set(i,curList);
            }

            //M-Step
            for (int i=0; i<Centers.size(); i++) {
                float[] curCenter = Centers.get(i);
                float[] newCenter = new float[curCenter.length];
                for (int j=0; j<newCenter.length; j++) {
                    newCenter[j] = 0;
                }

                float den = 0;
                for (int j=0; j<HiddenMatrix.get(i).length; j++) {
                    den += HiddenMatrix.get(i)[j];
                }

                for (int j=0; j<Data.size(); j++) {
                    float[] curData = Data.get(j);
                    for (int l=0; l<newCenter.length; l++) {
                        newCenter[l] += HiddenMatrix.get(i)[j]*curData[l]/den;
                    }
                }

                Centers.set(i,newCenter);
            }
        }
        
        
        return Centers;
    }
    
    public static void problem4() throws IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10933_7.txt");
        String[] lineArr = list.remove(0).split(" ");
        int k = Integer.parseInt(lineArr[0]);
        int m = Integer.parseInt(lineArr[1]);
        float beta = Float.parseFloat(list.remove(0));
        
        ArrayList<float[]> Data;
        try {
            Data = Parser.ParseFloatDataPoints(list, m);
            ArrayList<float[]> cs = SoftKMeansClustering(k, m, beta, Data);
            Outputter.printOut(cs);
        } catch (IOException ex) {
            Logger.getLogger(Yeast.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static PhyloTree HierarchicalClustering(HashMap<Integer,HashMap<Integer,Float>> DMat, int n){
        PhyloTree T = new PhyloTree(false);
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
        for(int i=0; i<n; i++){
            Node no = T.MakeNewNode();
            no.age = 0;
            no.numlabel = i;
            ArrayList<Node> leaves = new ArrayList<Node>();
            leaves.add(no);
            Cluster c = new Cluster(no,leaves);
            clusters.add(c);
        }
        while(clusters.size()>1) {
            CombinationGenerator CG = new CombinationGenerator(clusters.size(),2);
            float min_dist = Float.MAX_VALUE;
            Cluster[] closest = new Cluster[2];
            while (CG.hasMore()) {
                int[] comb = CG.getNext();
                Cluster c1 = clusters.get(comb[0]);
                Cluster c2 = clusters.get(comb[1]);
                if (!c1.equals(c2)){
                    float dist = c1.distanceTo(c2, DMat);
                    if (dist < min_dist) {
                        min_dist = dist;
                        closest[0] = c1;
                        closest[1] = c2;
                    }
                }
            }
            
            // merging of two clusters
            Cluster close1 = closest[0];
            Cluster close2 = closest[1];
            
            Node newHead = T.MakeNewNode();
            newHead.age = min_dist/2;
            newHead.numlabel = 2*n-clusters.size();
            ArrayList<Node> newLeaves = new ArrayList<>(close1.leaves);
            newLeaves.addAll(close2.leaves);
            Cluster newC = new Cluster(newHead,newLeaves);
            clusters.add(newC);
            
            for (Node leaf : newLeaves){
                System.out.print(leaf.numlabel+1 + " ");
            }
            System.out.println("");
            Edge nEdge1 = T.MakeNewEdge();
            T.u_connect(nEdge1, close1.head, newHead);
            
            Edge nEdge2 = T.MakeNewEdge();
            T.u_connect(nEdge2, close2.head, newHead);
            
            clusters.remove(close1);
            clusters.remove(close2);
            
            // add new row and column
            DMat.put(newC.head.numlabel,new HashMap<Integer,Float>());
            DMat.get(newC.head.numlabel).put(newC.head.numlabel, 0f);
            for(int i=0; i<clusters.size(); i++){
                Cluster curCluster = clusters.get(i);
                if (!curCluster.equals(newC)) {
                    float cDist = T.newClusterDistance(close1, close2, curCluster, DMat);
                    DMat.get(newC.head.numlabel).put(curCluster.head.numlabel,cDist);
                    DMat.get(curCluster.head.numlabel).put(newC.head.numlabel,cDist);
                }
            }
            
            // modifying DMat
            Iterator<Integer> hashIt = DMat.keySet().iterator();
            while (hashIt.hasNext()) {
                int num = hashIt.next();
                DMat.get(num).remove(close1.head.numlabel);
                DMat.get(num).remove(close2.head.numlabel);
            }
            DMat.remove(close1.head.numlabel);
            DMat.remove(close2.head.numlabel);
            
            
            
        }
        
        T.root = clusters.get(0).head;
        T.updateEdgeLength();
        
        return T;
    }
    
    public static void problem5 () throws IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/dataset_10934_7.txt");
        int n = Integer.parseInt(list.remove(0));
        HashMap<Integer,HashMap<Integer,Float>> fDMat = Parser.IntoFDMat(list);
        
        PhyloTree T = HierarchicalClustering(fDMat, n);
        System.out.println(T);
    }
 
    public static void quiz1() {
        float maxd = 0;
        float[][] datArr2 = {{2,6},{4,9},{5,7},{6,5},{8,3}};
        float[][] centArr2 = {{4,5},{7,4}};
        float[][] datArr = {{2,8},{2,5},{6,9},{7,5},{5,2}};
        float[][] centArr = {{3,5},{5,4}};
        
        ArrayList<float[]> datList = new ArrayList<>(Arrays.asList(datArr));
        ArrayList<float[]> centList = new ArrayList<>(Arrays.asList(centArr));
        
        
        for (float[] dat : datList){
            float d = dWRTCenters(dat, centList);
            if (d>maxd) {
                maxd = d;
            }
        }
        System.out.println(maxd);
        datList = new ArrayList<>(Arrays.asList(datArr2));
        centList = new ArrayList<>(Arrays.asList(centArr2));
        System.out.println(distortion(datList, centList));
        float[][] datArr3 = {{17,0,-4},{3,14,23},{9,7,16},{7,3,5}};
        float[] c = cog(new ArrayList<float[]>(Arrays.asList(datArr3)));
        System.out.println(c[0] + " " + c[1] + " " + c[2]);
        int i = 1;
    }
    
    public static void quiz2() throws IOException {
        ArrayList<String> list = Parser.ParseTextIntoList("src/bio2/test.txt");
        HashMap<Integer,HashMap<Integer,Float>> FDMat = Parser.IntoFDMat(list);
        
        float[][] datArr = {{2,8},{2,5},{6,9},{7,5},{5,2}};
        float[][] centArr = {{3,5},{5,4}};  
        ArrayList<float[]> Centers = new ArrayList<float[]>(Arrays.asList(centArr));
        ArrayList<float[]> Data = new ArrayList<float[]>(Arrays.asList(datArr));
        
        int i = 0;
        int j = 1;
        float beta = 1f;
        
        float newNum = 0;
        newNum += Math.pow(Math.E,-beta*distanceBetween(Data.get(j),Centers.get(i)));
        float den = 0;
        for (float[] c : Centers) {
            den += Math.pow(Math.E,-beta*distanceBetween(Data.get(j),c));
        }
        newNum /= den;
        
        System.out.println(newNum);
        
        ArrayList<ArrayList<Float>> HiddenMatrix = new ArrayList<>();
        ArrayList<Float> one = new ArrayList<>();
        for (String num : Arrays.asList("0.5 0.7 0.2 0.6 0.1".split(" "))) {
            
            one.add(Float.parseFloat(num));
            
        }
        HiddenMatrix.add(one);
        
        one = new ArrayList<>();
        for (String num : Arrays.asList("0.5 0.7 0.2 0.6 0.1".split(" "))) {
            
            one.add(Float.parseFloat(num));
            
        }
        HiddenMatrix.add(one);
        
        float[][] datArr2 = {{2,8},{2,5},{6,9},{7,5},{5,2}};
        ArrayList<float[]> datList2 = new ArrayList<>(Arrays.asList(datArr2));
        
        ArrayList<Float> ans = new ArrayList<>();
        for (int ii = 0; ii<2; ii++) {
            float coord = 0;
            float den2 = 0;
            for (int jj = 0; jj<datList2.size(); jj++) {
                float[] curP = datList2.get(jj);
                coord += curP[ii] * HiddenMatrix.get(ii).get(jj);
                den2 += HiddenMatrix.get(ii).get(jj);
            }
            coord /= den2;
            ans.add(coord);
        }
        
        System.out.println(ans);
        
    }
}
