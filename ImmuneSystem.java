import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;

public class ImmuneSystem {

    private String bitString;
    private ArrayList<Disease> diseaseList;
    private HashMap<Disease, Integer> indexMap;

    public static int SYSTEM_LENGTH = 50;

    public ImmuneSystem(){
        diseaseList = new ArrayList<Disease>();
        indexMap = new HashMap<Disease, Integer>();

        bitString = "";
        Random r = new Random();
        for (int i = 0; i < SYSTEM_LENGTH; i++){
            bitString += r.nextInt(2);
        }
    }

    public void add(Disease d){
        int minMatch = Integer.MAX_VALUE;
        int matchIndex = -1;

        for (int i = 0; i < SYSTEM_LENGTH - d.getDisease().lengh(); i++){
            int match =  match(d.getDisease, i);

            //if match is 0, dont add disease, person is immune
            if (match == 0) return;
            else if (match < minMatch){
                matchIndex = i;
                minMatch = match;
            }
        }

        diseaseList.add(d);
        indexMap.put(d, matchIndex);
    }

    private int match(String s, int index){
        int dif = 0;
        for (int i = 0; i < s.length(); i++){
            if (s.charAt(i) != bitString.charAt(i + index))
                dif++;
        }
        return dif;
    }

    public void update(){
        
    }

    public double getMetabolismChange(){
        return diseaseList.size();
    }
}
