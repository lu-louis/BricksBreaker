package ucsb2015.bricksbreaker;

/**
 * Created by LuLouis on 3/10/15.
 */
public class BallSetting {
    private final int WIDTH_INDEX   = 0;
    private final int HEIGHT_INDEX  = 1;

    // [M]
    private final float BALL_SZIE_START = (float) 50.0;
    private final int BALL_Z_START = (int) 0.0;

    // static property
    public Coordinate   coor;
    public Coordinate   coor_max;
    public float        radius;

    // motion parameter
    public Coordinate   direction;  // moving direction
    // unit vector, time unit: [ms] ?

    /* List of methods
     * ---- Implemented:
     *
     *      public void ball_init(Coordinate container, int display_mode);
     *      public void set_location(Coordinate new_coor);
     *      public void update_location();
     *      public void update_direction(int hit_dir);
     *
     * ---- Undefined:
     *      public void draw()
     *      private
     */
    public BallSetting(){

    }

    /***********************************************
     *
     * function: Ball initialization function
     *
     * @param container
     *
     ***********************************************/

    public void ball_init(Coordinate container, int display_mode){
        Coordinate start_dir;
        // get landscape

        //start_dir.x = 0;
        //start_dir.y = 0;
        //start_dir.z = 0;
        coor_max.x = container.x;
        coor_max.y = container.y;
        coor_max.z = container.z;
        // initialize radius
        radius = BALL_SZIE_START;

        // initialize coordinate : x,y
        switch(display_mode) {
            case 1:
                // vertical, portrait
                coor.x = (int) (container.x / 2 - radius);
                coor.y = (int) (container.y /10 - radius);
                break;
            case 2:
                // horizontal, landscape
                coor.x = (int) (container.x /10 - radius);
                coor.y = (int) (container.y / 2 - radius);
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
        //texture_sel = true;
        //color = Color.RED;
        //texture =;
    }
    /***********************************************
     *
     * function: Set location
     *
     ***********************************************/
    public void set_location(Coordinate new_coor){
        coor.x = new_coor.x;
        coor.y = new_coor.y;
        coor.z = new_coor.z;
    }

    /***********************************************
     *
     * function: Update location
     *
     ***********************************************/

    public void update_location(){

        coor.x = coor.x + direction.x;
        coor.y = coor.y + direction.y;
        coor.z = coor.z + direction.z;

        // out of boundary check
        if( coor.x < 0 || coor.x > coor_max.x ){
            coor.x = 0;
        }
        if( coor.y < 0 || coor.y > coor_max.y ){
            coor.y = 0;
        }
        if( coor.z < 0 || coor.z > coor_max.z){
            coor.z = 0;
        }

    }

    /***********************************************
     *
     * function: Update direction
     * Description:
     *      If the ball collide with an object in
     *      one direction, then it will not
     *
     * @param hit_dir
     *      hit direction indicate which direction
     *      the collision happens with other object.
     *      For multiple object collision, this func
     *      need to be called multiple times.
     *      0:no collision, 1:x, 2:y, 3:z
     *
     *
     ***********************************************/
    public void update_direction(int hit_dir){
        if(hit_dir > 0) {
            switch (hit_dir) {
                case 1:
                    // collide in x direction
                    direction.x *= -1;
                    break;
                case 2:
                    // collide in y direction
                    direction.y *= -1;
                    break;
                case 3:
                    // collide in x direction
                    direction.z *= -1;
                    break;
            }
        }
        else{
            // if hit the x boundary
            if( coor.x == 0  ||
                    coor.x == coor_max.x   ) {
                direction.x *= -1;
            }
            // if hit the y boundary
            if( coor.y == 0 ||
                    coor.y == coor_max.y ){
                direction.y *= -1;
            }
            // if hit the z boundary
            if( coor.z == 0 ||
                    coor.z == coor_max.z   ){
                direction.z *= -1;
            }
        }
    }

}
