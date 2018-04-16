
public class Event implements Comparable<Event>
{
    private String type;
    private double time;
    private Agent agent;

    public Event( String eventType, double eventTime, Agent eventAgent )
    {
        type = eventType;
        time = eventTime;
        agent = eventAgent;
    }



    public String getType()
    {
        return type;
    }


    public double getTime()
    {
        return time;
    }


    public Agent getAgent()
    {
        return agent;
    }


    public int compareTo( Event e )
    {
        if ( time < e.getTime() )
        {
            return -1;
        }
        else if ( time > e.getTime() )
        {
            return 1;
        }

        return 0;
    }
}