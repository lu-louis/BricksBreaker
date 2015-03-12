package ucsb2015.bricksbreaker;

/**
 * Created by LuLouis on 2/26/15.
 */
public class GameStatus {

    int bricks_left;
    int score;
    int level;
    Coordinate container_size;

    //
    int resolution;     // unit : time, update time interval
    int speed;

    public GameStatus(){
        bricks_left = 0;
        score       = 0;
        level       = 0;
        speed       = 0;
        container_size = new Coordinate();
        container_size.x = 0;
        container_size.y = 0;
        container_size.z = 0;
    }
}
