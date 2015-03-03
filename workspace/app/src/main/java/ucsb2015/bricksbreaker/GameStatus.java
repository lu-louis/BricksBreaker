package ucsb2015.bricksbreaker;

/**
 * Created by LuLouis on 2/26/15.
 */
public class GameStatus {

    int bricks_left;
    int score;
    int level;

    //
    int resolution;     // unit : time, update time interval
    int speed;

    public GameStatus(){
        bricks_left = 0;
        score       = 0;
        level       = 0;
        speed       = 0;
    }
}
