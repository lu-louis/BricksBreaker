package ucsb2015.bricksbreaker;

/**
 * Created by LuLouis on 2/26/15.
 */
public class Brick {
    //
    public Coordinate   coor;       // location of the brick
    public int          width;
    public int          height;
    public int          length;
    public float        vertex;    // should be vertex matrix
    public float        texture;   // should be texture metric
    public int          color;
    public boolean      visibility; // whether the brick is hit or not

    /***********************************************
     *
     * function: Brick initialization function
     *
     ***********************************************/
    public Brick(int x, int y, int z, int w, int h, int l, int v, int c){
        Coordinate coor = new Coordinate();
        coor.x = x;
        coor.y = y;
        coor.z = z;

        width   = w;
        height  = h;
        length  = l;

        vertex  = v;
        color   = c;

        visibility = true;
    }

    /***********************************************
     *
     * function: brick_hit
     *
     ***********************************************/
    public void brick_hit(){
        visibility = false;
    }


}
