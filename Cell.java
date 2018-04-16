public class Cell
{
    private double resourceCapacity;
    private boolean occupied;
    private double resourceLevel;
    private int regrowthRate;

    private double timeLastDepleted;

    private int row;
    private int col;


    public Cell( double _resourceCapacity, int _regrowthRate, int _row, int _col )
    {
        resourceCapacity = _resourceCapacity;
        regrowthRate = _regrowthRate;

        row = _row;
        col = _col;

        resourceLevel = resourceCapacity;
        occupied = false;
    }


    public boolean isOccupied()
    {
        return occupied;
    }


    public void setOccupied( boolean newStatus )
    {
        occupied = newStatus;
    }


    public double getResourceLevel()
    {
        return resourceLevel;
    }


    public void setResourceLevel( double _resourceLevel )
    {
        if ( _resourceLevel <= resourceCapacity )
        {
            resourceLevel = _resourceLevel;
        }
        else
        {
            resourceLevel = resourceCapacity;
        }
    }


    public int getRegrowthRate()
    {
        return regrowthRate;
    }


    public void setRegrowthRate( int regrowthRate )
    {
        this.regrowthRate = regrowthRate;
    }


    public void setCapacity( double _resourceCapacity )
    {
        resourceCapacity = _resourceCapacity;
    }


    public double getCapacity()
    {
        return resourceCapacity;
    }


    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }


    public double getTimeLastDepleted()
    {
        return timeLastDepleted;
    }


    public void setTimeLastDepleted( double time )
    {
        timeLastDepleted = time;
    }
}
