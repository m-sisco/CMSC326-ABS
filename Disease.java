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
            char c=(char)(s+'0');
            disease = disease + c;
        }
    }

    public Disease(Disease d){
        Random r = new Random();
        int mut = r.nextInt(10);
        String dis = d.getDisease();
        int len = dis.length();
        String newDis;
        if(mut == 0){
            int bit = r.nextInt(len);
            for(int i = 0; i < len; i++){
                if(i == bit){
                    char let = dis.charAt(i);
                    if(let == '0'){
                        newDis += '1';

                    }else{
                        newDis += '0';
                    }
                }
                char let = dis.charAt(i);
                newDis += let;
            }
        }
    }

    public String getDisease(){
        return disease;
    }

    public static void main(String [] args){
        Disease d;
        System.out.print(d.getDisease());
    }



}
