import java.util.Random;

/**
 * Created by Marissa on 4/23/2018.
 */
public class Disease {
    private String disease;
    private static Random r = new Random( Parameters.DISEASE_SEED );

    public Disease(){

        disease = new String();
        int l = r.nextInt(Parameters.MAX_DISEASE_LENGTH - Parameters.MIN_DISEASE_LENGTH)
                +Parameters.MIN_DISEASE_LENGTH; // random length from MIN to MAX
        for (int i = 0; i < l; i++){
            int s = r.nextInt(2);
            char c=(char)(s+'0');
            disease = disease + c;
        }
    }

    public Disease(Disease d){
        int mut = r.nextInt((int)(1/Parameters.MUTATION_PROBABILITY));
        String dis = d.getDisease();
        int len = dis.length();
        disease = new String();
        if(mut == 0){
            int bit = r.nextInt(len);
            for(int i = 0; i < len; i++){
                if(i == bit){
                    char let = dis.charAt(i);
                    if(let == '0'){
                        disease += '1';

                    }else{
                        disease += '0';
                    }
                }
                char let = dis.charAt(i);
                disease += let;
            }
        }else{
            disease = new String( dis );
        }
    }

    public String getDisease(){
        return disease;
    }


    public boolean equals( Object other ) {
        if ( other instanceof Disease ) {
            Disease d = (Disease) other;
            return disease.equals( d.getDisease() );
        }

        return false;
    }
}
