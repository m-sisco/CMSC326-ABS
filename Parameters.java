public class Parameters {
    //minimum length for a disease
    public static final int MIN_DISEASE_LENGTH = 3;
    //maximum length for a disease
    public static final int MAX_DISEASE_LENGTH = 14;
    //impact a disease has on metabolism
    public static final double DISEASE_IMPACT = 1;
    //length of the immune system
    public static final int IMMUNE_SYSTEM_LENGTH = 50;
    //number of different diseases present
    public static final int NUMBER_DISEASES = 25;
    //number of diseases given to an agent at birth
    public static final int BIRTH_DISEASES = 10;
    //probablity of mutation when transfering a disease
    public static final double MUTATION_PROBABILITY = .001;
    //time simulation will end
    public static final int END_TIME = 100;
    // seeds for random number generation
    public static final int SIM_SEED = 8675309;
    public static final int AGENT_SEED = 168522;
    public static final int DISEASE_SEED = 4340968;
    public static final int IMMUNE_SEED = 3104290;
}
