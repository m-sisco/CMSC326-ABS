public class Landscape
{
    private Cell[][] cells;

    public Landscape( int width, int height )
    {
        cells = new Cell[ width ][ height ];

        for ( int i = 0; i < width; ++i )
        {
            for ( int j = 0; j < height; ++j )
            {
                double resourceCapacity = calcResourceCapacity( i, j );
                int regrowthRate = 1;

                cells[i][j] = new Cell( resourceCapacity, regrowthRate, i, j );
            }
        }
    }


    public Cell getCellAt( int row, int column )
    {
        return cells[ row ][ column ];
    }



    private double calcResourceCapacity( int x, int y )
    {
        return f( x - (cells.length/ 4), y - (cells[0].length / 4) ) + f( x - (3 * cells.length/ 4), y - (3 * cells[0].length / 4) );
    }


    private double f( int x, int y )
    {
        double phi = 4.0;
        double theta_x = 0.3 * cells.length;
        double theta_y = 0.3 * cells[ 0 ].length;

        return phi * Math.exp( -1 * Math.pow( x / theta_x, 2 ) - Math.pow( y / theta_y, 2 ) );
    }

}
