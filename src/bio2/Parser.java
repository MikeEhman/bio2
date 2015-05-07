package bio2;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    public static ArrayList<String> ParseTextIntoList(String filename) throws FileNotFoundException, IOException{
        ArrayList<String> list = new ArrayList<String>();
        File file = new File(filename);
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            if(line.equals("Output")) break;
            if(!line.equals("Input")) list.add(line);
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
    
}
