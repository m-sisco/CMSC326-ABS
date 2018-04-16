import java.util.Random;

public class Agent
{
    private static Random r = new Random( 168522 );

    private String id;   // identifier for the agent

    private int    row;
    private int    col;

    private double wealth;

    private double metabolicRate;
    private int vision;

    private double nextMoveTime;
    private double deathTime;

    private double maxAge;


    public Agent( String id, double currentTime )
    {
        this.id = id;

        metabolicRate = r.nextDouble() * 3 + 1;
        vision = r.nextInt( 6 ) + 1;
        wealth = r.nextDouble() * 20 + 5;

        nextMoveTime = ( Math.log( 1 - r.nextDouble() ) / ( -1 ) ) + currentTime;

        maxAge = r.nextDouble() * 40 + 60;
        deathTime = currentTime + maxAge;
    }

    // simple accessor methods below
    public int    getRow() { return this.row; }
    public int    getCol() { return this.col; }
    public String getID()  { return this.id;  }

    // simple mutator methods below
    public void   setRowCol(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public int getVision()
    {
        return vision;
    }


    public double getWealth()
    {
        return wealth;
    }


    public void addWealth( double newWealth )
    {
        wealth = wealth + newWealth;
    }


    public double getMetabolicRate()
    {
        return metabolicRate;
    }


    public double getNextMoveTime()
    {
        return nextMoveTime;
    }


    public double getDeathTime()
    {
        return deathTime;
    }


    public void setNextMoveTime( double newTime )
    {
        nextMoveTime = newTime;
    }


    public void setDeathTime( double starvation )
    {
        deathTime = starvation;
    }
}

