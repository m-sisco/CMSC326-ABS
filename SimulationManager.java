import squint.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;


class SimulationManager extends GUIManager
{
    // whether to write data about avg diseases and healthy agents to a file
    private boolean WRITE_OUTPUT_DATA = true;
    private String output_file_name = "len-" + Parameters.MAX_DISEASE_LENGTH + "-disease.csv";

    // default window width and height defined as constants
    private final int WINDOW_WIDTH  = 500;
    private final int WINDOW_HEIGHT = 500;

    protected ArrayList<Agent> agentList; 
        // A list of all agents in the simulation; this is declared as
        // protected because we access it directly from within AgentCanvas.  
        // Why?  Because we only access it to draw the agents, and given 
        // that the list may be large, it doesn't make sense to
        // make a copy and return that copy to AgentCanvas.

    protected Landscape landscape;
    protected int gridSize;

    private AgentCanvas canvas;  // the canvas on which agents are drawn
    private Random rng;

    private double time;  // the simulation time

    private PriorityQueue<Event> eventCalendar = new PriorityQueue<>();

    private ArrayList<Disease> diseases = new ArrayList<>();

    private int agentCount = 0; // keeps track of total number of agents created

    // comparator for ordering cells when an agent moves
    // so that cells with the most resources are first in priority queue
    Comparator<Cell> comparator = new Comparator<Cell>()
    {
        @Override
        public int compare( Cell c1, Cell c2 )
        {
            if ( c1.getResourceLevel() > c2.getResourceLevel() )
            {
                return -1;
            }

            if ( c2.getResourceLevel() > c1.getResourceLevel() )
            {
                return 1;
            }

            return 0;
        }
    };

    //======================================================================
    //* public SimulationManager(int gridSize, int numAgents, int initialSeed)
    //======================================================================
    public SimulationManager(int gridSize, int numAgents, int initialSeed)
    {
        // generate unique diseases
        while( diseases.size() < Parameters.NUMBER_DISEASES )
        {
            Disease d = new Disease();
            if ( ! diseases.contains( d ) )
            {
                diseases.add( d );
            }
        }


        this.gridSize  = gridSize;
        this.agentList = new ArrayList<Agent>();

        rng = new Random(initialSeed);

        this.time = 0;   // initialize the simulation clock

        landscape = new Landscape(gridSize, gridSize);

        // generate agents and place in the landscape
        for (int i = 0; i < numAgents; i++)
        {
            Agent a = new Agent("agent " + agentCount, time);
            agentList.add(a);

            ++agentCount;

            int row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
            int col = rng.nextInt(gridSize); // an int in [0, gridSize-1]

            while( landscape.getCellAt( row, col ).isOccupied() )
            {
                row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
                col = rng.nextInt(gridSize); // an int in [0, gridSize-1]
            }


            a.setRowCol(row, col);
            a.addWealth( landscape.getCellAt( row, col ).getResourceLevel() );
            landscape.getCellAt( row, col ).setAgent( a );

            // start each agent with 10 random, distinct diseases
            ImmuneSystem agentImmuneSystem = a.getImmuneSys();

            // mix up the disease list to get 10 random, distinct diseases
            Collections.shuffle( diseases, rng );


            for ( int j = 0; j < Parameters.BIRTH_DISEASES; ++j )
            {
                agentImmuneSystem.add( diseases.get( j ), 0 );
            }

            // determine this agent's next event
            setFutureEvent( a );
        }


        this.createWindow();
        this.run();
    }

    //======================================================================
    //* public void createWindow()
    //======================================================================
    public void createWindow()
    {
        this.createWindow(WINDOW_WIDTH, WINDOW_HEIGHT);
        contentPane.setLayout(new BorderLayout()); // java.awt.*

        canvas = new AgentCanvas(this);
        contentPane.add( new JScrollPane(canvas), BorderLayout.CENTER);
    }

    // simple accessor methods
    public int    getGridSize() { return this.gridSize; }
    public double getTime()     { return this.time;     }

    //======================================================================
    //* public void run()
    //* This is where your main simulation event engine code should go...
    //======================================================================
    public void run()
    {
        // for writing data on each simulation
        BufferedWriter writer = null;

        if ( WRITE_OUTPUT_DATA )
        {
            try
            {
                writer = new BufferedWriter( new FileWriter( new File( output_file_name ) ) );

            }
            catch ( Exception e )
            {
                System.err.println( "Creation of output file failed" );
                System.exit( 1 );
            }

            try
            {
                writer.write( "time, avgDiseases, propHealthy\n" );
            }
            catch ( Exception e )
            {
                System.err.println( "Error writing file" );
            }


            // write data for initial state
            writeData( writer );
        }


        // agents are redrawn approximately every unit time
        int count = 1;

        Event nextEvent = eventCalendar.poll();

        // get each event in the calendar until maxTime
        while ( nextEvent.getTime() < Parameters.END_TIME)
        {
            time = nextEvent.getTime();

            if ( nextEvent.getType().equals( "move" ) )
            {
                move( nextEvent.getAgent() );
            }
            else if ( nextEvent.getType().equals( "update" ) )
            {
                // update the next disease in agent's immune system
                Agent a = nextEvent.getAgent();
                a.getImmuneSys().update();

                // determine the agent's next event
                setFutureEvent( a );
            }
            else if ( nextEvent.getType().equals( "death" ) )
            {
                // remove the dead agent from the landscape
                landscape.getCellAt( nextEvent.getAgent().getRow(), nextEvent.getAgent().getCol() ).setOccupied( false );
                agentList.remove( nextEvent.getAgent() );

                // generate a new agent to replace it and place it randomly in the landscape

                Agent newAgent = new Agent( "agent " + agentCount, time );
                ++agentCount;

                int row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
                int col = rng.nextInt(gridSize); // an int in [0, gridSize-1]

                while( landscape.getCellAt( row, col ).isOccupied() )
                {
                    row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
                    col = rng.nextInt(gridSize); // an int in [0, gridSize-1]
                }

                newAgent.setRowCol(row, col);
                newAgent.addWealth( landscape.getCellAt( row, col ).getResourceLevel() );
                landscape.getCellAt( row, col ).setAgent( newAgent );

                agentList.add( newAgent );


                // set the new agent's diseases randomly from the list
                Collections.shuffle( diseases, rng );
                for ( int i = 0; i < Parameters.BIRTH_DISEASES; ++i  )
                {
                    newAgent.getImmuneSys().add( diseases.get(i), time );
                }

                setFutureEvent( newAgent );
            }


            if ( WRITE_OUTPUT_DATA )
            {
                writeData( writer );
            }


            nextEvent = eventCalendar.poll();

            if ( time > count )
            {
                ++count;
                canvas.repaint();
                try
                {
                    Thread.sleep( 500 );
                }
                catch ( Exception e )
                {
                }
            }

            /*canvas.repaint();
            try
            {
                Thread.sleep( 1 );
            }
            catch ( Exception e )
            {
            }*/
        }

        canvas.repaint();
        try
        {
            Thread.sleep( 500 );
        }
        catch ( Exception e )
        {
        }

        System.out.println( agentCount );
        System.out.println( time );


        if ( WRITE_OUTPUT_DATA )
        {
            try
            {
                writer.close();
            }
            catch( Exception e )
            {
                System.err.println( "Problem closing output file" );
            }
        }

    }


    /*
     * Moves Agent a to the closest cell within its vision that has the most resources.
     */
    public void move( Agent a )
    {
        PriorityQueue<Cell> cellsWithinVision = new PriorityQueue<>( a.getVision() * 4, comparator );

        Cell currentCell = landscape.getCellAt( a.getRow(), a.getCol() );

        // update agent's wealth
        a.addWealth( ( time - currentCell.getTimeLastDepleted() ) * currentCell.getRegrowthRate() );

        currentCell.setResourceLevel( 0 );
        currentCell.setTimeLastDepleted( time );

        // add current cell as an option in case all cells are at 0/occupied
        cellsWithinVision.add( currentCell );


        // add cells within agent's vision to priority queue
        for ( int j = 1; j <= a.getVision(); ++j )
        {
            Cell right = landscape.getCellAt( ( a.getRow() + j ) % gridSize, a.getCol() );
            Cell left = landscape.getCellAt( ( a.getRow() - j + gridSize ) % gridSize, a.getCol() );
            Cell up = landscape.getCellAt( a.getRow(), ( a.getCol() + j ) % gridSize );
            Cell down = landscape.getCellAt( a.getRow(), ( a.getCol() - j + gridSize ) % gridSize );

            if ( ! right.isOccupied() )
            {
                right.setResourceLevel( ( time - right.getTimeLastDepleted() ) * right.getRegrowthRate() );
                cellsWithinVision.add( right );
            }

            if ( ! left.isOccupied() )
            {
                left.setResourceLevel( ( time - left.getTimeLastDepleted() ) * left.getRegrowthRate() );
                cellsWithinVision.add( left );
            }

            if ( ! up.isOccupied() )
            {
                up.setResourceLevel( ( time - up.getTimeLastDepleted() ) * up.getRegrowthRate() );
                cellsWithinVision.add( up );
            }

            if ( ! down.isOccupied() )
            {
                down.setResourceLevel( ( time - down.getTimeLastDepleted() ) * down.getRegrowthRate() );
                cellsWithinVision.add( down );
            }
        }

        // need to randomly break ties

        // add all equal cells to a list
        ArrayList<Cell> candidateCells = new ArrayList<>();
        candidateCells.add( cellsWithinVision.poll() );

        while( !cellsWithinVision.isEmpty() &&
               cellsWithinVision.peek().getResourceLevel() == candidateCells.get( 0 ).getResourceLevel() )
        {
            candidateCells.add( cellsWithinVision.poll() );
        }

        // get a random cell to move to from the cells in the list
        Cell newCell = candidateCells.get( rng.nextInt( candidateCells.size() ) );

        landscape.getCellAt( a.getRow(), a.getCol() ).setOccupied( false );
        landscape.getCellAt( a.getRow(), a.getCol() ).setTimeLastDepleted( time );

        a.setRowCol( newCell.getRow(), newCell.getCol() );
        a.addWealth( newCell.getResourceLevel() );

        newCell.setOccupied( true );
        newCell.setAgent( a );
        newCell.setTimeLastDepleted( time );


        // get a random disease from a neighbor

        // get all of the agent's neighbors
        ArrayList<Agent> neighbors = new ArrayList<>();

        // check if the cells adjacent to the agent are occupied and add the agent there if so
        if ( landscape.getCellAt( ( a.getRow() - 1 + gridSize ) % gridSize, a.getCol() ).isOccupied() )
        {
            neighbors.add( landscape.getCellAt( (a.getRow() - 1 + gridSize) % gridSize, a.getCol() ).getAgent() );
        }

        if ( landscape.getCellAt( ( a.getRow() + 1 + gridSize ) % gridSize, a.getCol() ).isOccupied() )
        {
            neighbors.add( landscape.getCellAt( (a.getRow() + 1 + gridSize) % gridSize, a.getCol() ).getAgent() );
        }

        if ( landscape.getCellAt( a.getRow(), ( a.getCol() - 1 + gridSize ) % gridSize ).isOccupied() )
        {
            neighbors.add( landscape.getCellAt( a.getRow(), ( a.getCol() - 1 + gridSize ) % gridSize ).getAgent() );
        }

        if ( landscape.getCellAt( a.getRow(), ( a.getCol() + 1 + gridSize ) % gridSize ).isOccupied() )
        {
            neighbors.add( landscape.getCellAt( a.getRow(), ( a.getCol() + 1 + gridSize ) % gridSize ).getAgent() );
        }

        for ( int i = 0; i < neighbors.size(); ++i )
        {
            // get a random disease (if any) from each neighbor
            int neighborDiseases = neighbors.get(i).getImmuneSys().size();

            if ( neighborDiseases > 0 )
            {
                // use copy constructor to give the disease a chance to mutate
                Disease d = new Disease( neighbors.get(i).getImmuneSys().get( rng.nextInt( neighborDiseases ) ) );

                // if it mutates and produces a new disease, add it to the list of diseases
                if ( ! diseases.contains( d ) )
                {
                    diseases.add( d );
                }

                infect( a, d );
            }
        }

        a.setNextMoveTime( getMoveTime() );
        setFutureEvent( a );
    }


    /*
     * Adds the given disease to the agent's immune system and updates the metabolism.
     */
    public void infect( Agent a, Disease d )
    {
        Cell currentCell = landscape.getCellAt( a.getRow(), a.getCol() );

        // update a's resources based on its cell and metabolism
        double addedWealth = currentCell.getRegrowthRate() * ( time - currentCell.getTimeLastDepleted() );
        double metabolized = a.getMetabolicRate() * ( time - currentCell.getTimeLastDepleted() );

        a.addWealth( addedWealth - metabolized );
        currentCell.setTimeLastDepleted( time );

        // add the disease to a's immune system
        a.getImmuneSys().add( d, time );
    }


    /*
     * Determines the next event placed in the event queue for Agent a.
     */
    private void setFutureEvent( Agent a )
    {
        Cell newCell = landscape.getCellAt( a.getRow(), a.getCol() );

        double nextMove = a.getNextMoveTime();
        double nextUpdate = a.getImmuneSys().getNextUpdateTime();

        Event nextEvent;

        if ( nextMove < nextUpdate )
        {
            nextEvent = new Event( "move", nextMove, a );
        }
        else
        {
            nextEvent = new Event( "update", nextUpdate, a );
        }


        double nextEventTime = nextEvent.getTime();

        if ( a.getDeathTime() < nextEventTime )
        {
            Event death = new Event( "death", a.getDeathTime(), a );
            eventCalendar.add( death );
            a.setNextEvent( death );
        }
        else
        {
            // determine whether the agent will starve before its next event
            double addedWealth = landscape.getCellAt( newCell.getRow(),
                                                      newCell.getCol() ).getRegrowthRate() * ( nextEventTime - time );
            double metabolized = a.getMetabolicRate() * ( nextEventTime - time );
            double newWealth = a.getWealth() + addedWealth - metabolized;

            // if its wealth drops to 0, it will starve before its next event
            if ( newWealth <= 0 )
            {
                double slope = ( newWealth - a.getWealth() ) / ( nextMove - time );

                double deathTime = ( ( -1 * a.getWealth() ) / slope ) + time;

                a.setDeathTime( deathTime );

                Event death = new Event( "death", deathTime, a );
                eventCalendar.add( death );
                a.setNextEvent( death );
            }
            else
            {
                eventCalendar.add( nextEvent );
                a.setNextEvent( nextEvent );
            }
        }
    }


    // generates the next time an agent moves
    public double getMoveTime()
    {
        return ( Math.log( 1 - rng.nextDouble() ) / ( -1 ) ) + time;
    }


    /* Calculates the average number of diseases per agent and the proportion
     * of healthy agents and writes these to the writer along with the current
     * time, in csv format.
     */
    public void writeData( BufferedWriter writer )
    {
        // collecting data on avg number of diseases and percentage of healthy agents
        double avgDiseases = 0;
        double healthyAgents = 0;

        for ( Agent agent : agentList )
        {
            avgDiseases += agent.getImmuneSys().size();

            if ( agent.getImmuneSys().size() == 0 )
            {
                ++healthyAgents;
            }
        }

        avgDiseases = avgDiseases / agentList.size();
        healthyAgents = healthyAgents / agentList.size();


        try
        {
            writer.write( time + ", " );
            writer.write( avgDiseases + ", " );
            writer.write( healthyAgents + "\n" );
        }
        catch ( Exception e )
        {
            System.err.println( "Writing to file failed" );
        }
    }



    //======================================================================
    //* public static void main(String[] args)
    //* Just including main so that the simulation can be executed from the
    //* command prompt.  Note that main just creates a new instance of this
    //* class, which will start the GUI window and then we're off and
    //* running...
    //======================================================================
    public static void main(String[] args)
    {
        new SimulationManager(40, 400, Parameters.SIM_SEED);
    }
}
