package ucsb2015.bricksbreaker;

import android.util.Log;

/**
 * Created by LuLouis on 2/26/15.
 */
public class GameStatus {


    int bricks_left;
    int score;
    int level;
    Coordinate container_size;
    Coordinate numBrick;

    //
    int refresh_rate = 1;     // unit : time, update time interval
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

        numBrick = new Coordinate();
        numBrick.x = 0;
        numBrick.y = 0;
        numBrick.z = 0;
    }

    public boolean init_cpl_check(){
        boolean pass = true;
        // check container size
        if( container_size.x==0 ||
            container_size.y==0 ||
            container_size.z==0){
            Log.e("GameStautsObj", "container size error"+
                        container_size.x +"/"+
                        container_size.y +"/"+
                        container_size.z);
            pass = false;
        }
        // check num of bricks in axix
        if( numBrick.x==0 ||
            numBrick.y==0 ||
            numBrick.z==0 ){
            Log.e("GameStautsObj", "numBrick error"+
                    numBrick.x +"/"+
                    numBrick.y +"/"+
                    numBrick.z);
            pass = false;
        }
        // check bricks_left
        if(bricks_left==0){
            Log.e("GameStautsObj", "bircks_left == 0");
            pass = false;
        }
        // check level
        if(level == 0 ){
            Log.e("GameStautsObj", "level == 0");
            pass = false;
        }


        return pass;
    }
}
