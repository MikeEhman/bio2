package bio2;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    public static ArrayList<String> ParseTextIntoList(String filename) throws FileNotFoundException, IOException{
        ArrayList<String> list = new ArrayList<String>();
        File file = new File(filename);
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            if(line.equals("Output:")) break;
            if(!line.equals("Input:")) list.add(line);
        }

        return list;
    }
    
    
    public static HashMap<Integer,HashMap<Integer,Integer>> IntoDMat(ArrayList<String> list) {
        HashMap<Integer,HashMap<Integer,Integer>> mat = new HashMap<>();
        int size = list.size();
        for(int i=0; i<size; i++){
            mat.put(i,new HashMap<Integer,Integer>());
        }
        for(int i=0; i<size; i++){
            String line = list.get(i);
            String[] arr = line.split("\t");
            for(int j=0; j<arr.length; j++){
                mat.get(i).put(j,Integer.parseInt(arr[j]));
            }
        }
        
        return mat;
    } 

    public static HashMap<Integer,HashMap<Integer,Float>> IntoFDMat(ArrayList<String> list) {
        HashMap<Integer,HashMap<Integer,Float>> mat = new HashMap<>();
        int size = list.size();
        for(int i=0; i<size; i++){
            mat.put(i,new HashMap<Integer,Float>());
        }
        for(int i=0; i<size; i++){
            String line = list.get(i);
            String[] arr = line.split(" ");
            for(int j=0; j<arr.length; j++){
                mat.get(i).put(j,Float.parseFloat(arr[j]));
            }
        }
        
        return mat;
    } 
    
    public static ArrayList<float[]> ParseFloatDataPoints (ArrayList<String> list, int n) throws IOException {
        String[] lineArr;
        ArrayList<float[]> Data = new ArrayList<>();
        for (String line : list) {
            lineArr = line.split(" ");
            float[] p = new float[n];
            for (int i=0; i<n; i++) {
                float num = Float.parseFloat(lineArr[i]);
                p[i] = num;
            }
            Data.add(p);
        }
        return Data;
    }
    
    public static HashMap<String,HashMap<String,Double>> ParseListIntoStringDoubleMatrix(ArrayList<String> list) {
        HashMap<String,HashMap<String,Double>> Mat = new HashMap<>();

        // top row consists of x states
        String topRow = list.get(0);
        // separated by: tabs
        ArrayList<String> topRowList = new ArrayList<String>(Arrays.asList(topRow.split("\t")));
        topRowList.remove(0); // usually there's a blank at first column
        
        // first column consists of y alphabets
        ArrayList<String> firColList = new ArrayList<String>();
        for (int i=1; i<list.size(); i++) {
            String curRow = list.get(i);
            firColList.add(""+curRow.charAt(0));
        }
       
        for (String xs : firColList) {
            Mat.put(xs, new HashMap<String,Double>());
        }
        
        // look through all entries and put numbers at appropriate positions
        for (int i=1; i<list.size(); i++) {
            String row = list.get(i);
            int xIndex = i-1;
            ArrayList<String> splitRowList = new ArrayList<String> (Arrays.asList(row.split("\t")));
            splitRowList.remove(0);
            for (int yIndex=0; yIndex<splitRowList.size(); yIndex++) {
                HashMap<String,Double> subMap = new HashMap<>();
                
                String xStr = firColList.get(xIndex);
                String yStr = topRowList.get(yIndex);
                
                // read number
                double num = Double.parseDouble(splitRowList.get(yIndex));

                // put number in submap
                Mat.get(xStr).put(yStr, num);
            }
        }
        
        return Mat;
    
    }
    
}
