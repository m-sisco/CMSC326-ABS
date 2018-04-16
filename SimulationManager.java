import squint.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

class SimulationManager extends GUIManager
{
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

        this.gridSize  = gridSize;
        this.agentList = new ArrayList<Agent>();

        rng = new Random(initialSeed);

        this.time = 0;   // initialize the simulation clock

        landscape = new Landscape(gridSize, gridSize);

        for (int i = 0; i < numAgents; i++)
        {
            Agent a = new Agent("agent ", time);
            agentList.add(a);

            int row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
            int col = rng.nextInt(gridSize); // an int in [0, gridSize-1]

            while( landscape.getCellAt( row, col ).isOccupied() )
            {
                row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
                col = rng.nextInt(gridSize); // an int in [0, gridSize-1]
            }

            // we should check to make sure the cell isn't already occupied!

            a.setRowCol(row, col);
            a.addWealth( landscape.getCellAt( row, col ).getResourceLevel() );

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
        double maxTime = 100;
        int count = 1;

        Event nextEvent = eventCalendar.poll();

        // bogus simulation code below...
        while ( nextEvent.getTime() < maxTime )
        {
            time = nextEvent.getTime();


            if ( nextEvent.getType().equals( "move" ) )
            {
                move( nextEvent.getAgent() );
            }
            else
            {
                landscape.getCellAt( nextEvent.getAgent().getRow(), nextEvent.getAgent().getCol() ).setOccupied( false );
                agentList.remove( nextEvent.getAgent() );

                Agent newAgent = new Agent( "agent", time );

                int row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
                int col = rng.nextInt(gridSize); // an int in [0, gridSize-1]

                while( landscape.getCellAt( row, col ).isOccupied() )
                {
                    row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
                    col = rng.nextInt(gridSize); // an int in [0, gridSize-1]
                }

                newAgent.setRowCol(row, col);
                newAgent.addWealth( landscape.getCellAt( row, col ).getResourceLevel() );

                agentList.add( newAgent );

                setFutureEvent( newAgent );
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
        }
    }


    public void move( Agent a )
    {
        PriorityQueue<Cell> cellsWithinVision = new PriorityQueue<>( 11, comparator );

        Cell currentCell = landscape.getCellAt( a.getRow(), a.getCol() );
        currentCell.setResourceLevel( ( time - currentCell.getTimeLastDepleted() ) * currentCell.getRegrowthRate() );

        cellsWithinVision.add( currentCell );

        // add cells within agent's vision
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
        ArrayList<Cell> candidateCells = new ArrayList<>();

        candidateCells.add( cellsWithinVision.poll() );

        while( !cellsWithinVision.isEmpty() &&
               cellsWithinVision.peek().getResourceLevel() == candidateCells.get( 0 ).getResourceLevel() )
        {
            candidateCells.add( cellsWithinVision.poll() );
        }

        Cell newCell = candidateCells.get( rng.nextInt( candidateCells.size() ) );

        landscape.getCellAt( a.getRow(), a.getCol() ).setOccupied( false );
        landscape.getCellAt( a.getRow(), a.getCol() ).setTimeLastDepleted( time );

        a.setRowCol( newCell.getRow(), newCell.getCol() );
        a.addWealth( newCell.getResourceLevel() );
        newCell.setOccupied( true );

        a.setNextMoveTime( getMoveTime() );
        setFutureEvent( a );
    }


    private void setFutureEvent( Agent a )
    {
        Cell newCell = landscape.getCellAt( a.getRow(), a.getCol() );

        double nextMove = a.getNextMoveTime();

        if ( a.getDeathTime() < nextMove )
        {
            eventCalendar.add( new Event( "death", a.getDeathTime(), a ) );
            return;
        }


        double addedWealth = landscape.getCellAt( newCell.getRow(), newCell.getCol() ).getRegrowthRate() * ( nextMove
                                                                                                             - time );
        double metabolized = a.getMetabolicRate() * ( nextMove - time );
        double newWealth = a.getWealth() + addedWealth - metabolized;

        if ( newWealth <= 0 )
        {
            double slope = ( newWealth - a.getWealth() ) / ( nextMove - time );

            double deathTime = ( ( -1 * a.getWealth() ) / slope ) + time;

            a.setDeathTime( deathTime );

            Event death = new Event( "death", deathTime, a );
            eventCalendar.add( death );
        }
        else
        {
            Event move = new Event( "move", nextMove, a );
            eventCalendar.add( move );
        }
    }


    public double getMoveTime()
    {
        return ( Math.log( 1 - rng.nextDouble() ) / ( -1 ) ) + time;
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
        new SimulationManager(40, 400, 8675309);
    }
}
