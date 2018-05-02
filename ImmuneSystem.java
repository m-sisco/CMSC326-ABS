import javafx.util.Pair;

import java.util.*;

public class ImmuneSystem {

    private String bitString;
    private ArrayList<Disease> diseaseList;
    private HashMap<Disease, Integer> indexMap;
    private PriorityQueue<Pair<Disease,Event>> updates;  // diseases and when they should update immune sys

    public ImmuneSystem(){
        diseaseList = new ArrayList<Disease>();
        indexMap = new HashMap<Disease, Integer>();
        updates = new PriorityQueue<>( 10, comparator );

        bitString = "";
        Random r = new Random();
        for (int i = 0; i < Parameters.IMMUNE_SYSTEM_LENGTH; i++){
            bitString += r.nextInt(2);
        }
    }

    public void add(Disease d, double time){
        // first check whether the agent already has this disease
        if ( indexMap.get( d ) != null ) {
            return;
        }

        int minMatch = Integer.MAX_VALUE;
        int matchIndex = -1;

        for (int i = 0; i < Parameters.IMMUNE_SYSTEM_LENGTH
                - d.getDisease().length(); i++){
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

        // pair disease with time to determine which one should be updated next
        Event diseaseUpdate = new Event( "update", time + 1, null ); // don't really need to connect an agent to these
        updates.add( new Pair<>( d, diseaseUpdate ) );
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
            Pair<Disease, Event> nextUpdate = updates.poll();
            Disease d = nextUpdate.getKey();

            int start = indexMap.get( d );
            int j = diseaseList.indexOf( d );

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
                    indexMap.remove(d);
                } else {
                    // add the disease back to the update list with a new time
                    double nextUpdateTime = nextUpdate.getValue().getTime() + 1;
                    Event updateEvent = new Event( "update", nextUpdateTime, null );

                    updates.add( new Pair<>( d, updateEvent ) );
                }
            } else {
                diseaseList.remove(j);
                indexMap.remove(d);
            }
    }

    public double getMetabolismChange(){
        return diseaseList.size() * Parameters.DISEASE_IMPACT;
    }


    public double getNextUpdateTime() {
        if ( updates.isEmpty() ) {
            return Double.MAX_VALUE;
        }

        return updates.peek().getValue().getTime();
    }


    // sorts <disease, event> pairs by event time
    Comparator<Pair<Disease, Event>> comparator = new Comparator<Pair<Disease, Event>>() {
        @Override
        public int compare( Pair<Disease, Event> pair1, Pair<Disease, Event> pair2 ) {
            return pair1.getValue().compareTo( pair2.getValue() );
        }
    };
}
