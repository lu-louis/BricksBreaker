package ucsb2015.bricksbreaker;

/**
 * Created by LuLouis on 3/10/15.
 */
public class BrickSetting {

    private final int   BRICK_WIDTH    = 200;   // y
    private final int   BRICK_HEIGHT   = 100;   // z
    private final int   BRICK_LENGTH   = 400;   // x
    private final int   BRICK_SPACE_X  = 100;
    private final int   BRICK_SPACE_Y  = 100;
    private final int   BRICK_SPACE_Z  = 100;

    public Coordinate relCoor;
    public Coordinate absCoor;
    public boolean visibility;
    public int width;
    public int height;
    public int length;
    public int id;

    public BrickSetting(int id_num, Coordinate location){
        id      = id_num;
        // set brick dimension
        width   = BRICK_WIDTH;
        height  = BRICK_HEIGHT;
        length  = BRICK_LENGTH;

        relCoor = new Coordinate();
        absCoor = new Coordinate();

        relCoor = location;
        absCoor.x = BRICK_WIDTH/2 + BRICK_WIDTH*location.x  + BRICK_SPACE_X;
        absCoor.y = BRICK_HEIGHT/2 + BRICK_WIDTH*location.y + BRICK_SPACE_Y;
        absCoor.z = BRICK_LENGTH/2 + BRICK_WIDTH*location.z + BRICK_SPACE_Z;

        visibility = true;

    }

    public void brick_hit(){
        visibility = false;
    }
}
