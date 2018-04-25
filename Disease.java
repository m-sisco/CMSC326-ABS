import java.util.Random;

/**
 * Created by Marissa on 4/23/2018.
 */
public class Disease {
    private String disease;

    public Disease(){
        Random r = new Random();
        int l = r.nextInt(10)+1;
        for (int i = 0; i < l; i++){
            int s = r.nextInt(2);
            disease = disease + s;
        }
    }

    public String getDisease(){
        return disease;
    }


}
