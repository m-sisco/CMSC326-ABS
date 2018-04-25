import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;

public class ImmuneSystem {

    private String bitString;
    private ArrayList<Disease> diseaseList;
    private HashMap<Disease, Integer> indexMap;

    private static int SYSTEM_LENGTH = 50;

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

        for (int i = 0; i < SYSTEM_LENGTH - d.getDisease().length(); i++){
            int match =  match(d.getDisease(), i);

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

    public Disease get(int index){return diseaseList.get(index);}
    public int size(){return diseaseList.size();}

    private int match(String s, int index){
        int dif = 0;
        for (int i = 0; i < s.length(); i++){
            if (s.charAt(i) != bitString.charAt(i + index))
                dif++;
        }
        return dif;
    }

    public void update(){
        for (int j = diseaseList.size()-1; j >= 0; j--){
            Disease d = diseaseList.get(j);
            int start = indexMap.get(d);

            boolean change = false;
            for (int i = 0; i < d.getDisease().length() && ! change; i++){
                if (d.getDisease().charAt(i) != bitString.charAt(i+start)){
                    StringBuilder sb = new StringBuilder(bitString);
                    sb.setCharAt(i+start, d.getDisease().charAt(i));
                    bitString = sb.toString();
                    change = true;
                }
            }

            if (change){
                if (match(d.getDisease(), start) == 0){
                    diseaseList.remove(j);
                }
            } else {
                diseaseList.remove(j);
            }
        }
    }

    public double getMetabolismChange(){
        return diseaseList.size();
    }
}
