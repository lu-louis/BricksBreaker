package ucsb2015.bricksbreaker;

import android.graphics.Color;

/**
 * Created by LuLouis on 2/26/15.
 */
public class Ball {

    private final int WIDTH_INDEX   = 0;
    private final int HEIGHT_INDEX  = 1;

    // [M]
    private final float BALL_SZIE_START = (float) 50.0;
    private final int BALL_Z_START = (int) 0.0;

    
    // static property
    public Coordinate   coor;
    public float        radius;
    public float        vertex;     // vertex matrix
    public float        texture;    // texture matrix
    public int          color;
    public boolean      texture_sel;    // [0] texture, [1] color

    // motion parameter
    public Coordinate   direction;  // moving direction
                                    // unit vector, time unit: [ms] ?

    /***********************************************
     *
     * function: Ball initialization function
     *
     * @param screen_size
     *
     ***********************************************/

    public void ball_init(int screen_size[], int display_mode){
        Coordinate start_dir;
        // get landscape

        //start_dir.x = 0;
        //start_dir.y = 0;
        //start_dir.z = 0;

        // initialize radius
        radius = BALL_SZIE_START;

        // initialize coordinate : x,y
        switch(display_mode) {
            case 1:
                // vertical, portrait
                coor.x = (int) (screen_size[WIDTH_INDEX] / 2 - radius);
                coor.y = (int) (screen_size[HEIGHT_INDEX]/10 - radius);
                break;
            case 2:
                // horizontal, landscape
                coor.x = (int) (screen_size[WIDTH_INDEX] /10 - radius);
                coor.y = (int) (screen_size[HEIGHT_INDEX]/ 2 - radius);
                break;

        }
        // initialize coordinate : z
        coor.z = (int) radius;
        // intialize direction
        // [M]
        direction.x = 0;
        direction.y = 0;
        direction.z = 1;

        // initialize vertex
        //vertext = ;

        // initialize texture
        texture_sel = true;
        color = Color.RED;
        //texture =;
    }
}
