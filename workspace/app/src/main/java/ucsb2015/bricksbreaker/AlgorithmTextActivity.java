package ucsb2015.bricksbreaker;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;


public class AlgorithmTextActivity extends ActionBarActivity {


    Ball        myBall;
    GameStatus  myGameStatus;
    Coordinate  container;
    int[]       screen_resolution;

    // [M]
    private final int NUM_BRICK_START = 10;
    private final int CONTAINER_DEPTH = 400;

    private final String LOG_GAMESTATUS = "Game Status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algorithm_text);
        // main flow
        // load game configuration

        // game initialization
        game_init();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_algorithm_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*********************************************************
     *
     * Game initialization function
     *
     *********************************************************/
    public void game_init(){
        /***********  Retrieve phone setting  ***********/
        // Retrieve screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        //screen_resolution = new int[]{screenWidth, screenHeight};
        container.x = screenWidth;
        container.y = screenHeight;
        container.z = CONTAINER_DEPTH;

        // get orientation
        int display_mode = getResources().getConfiguration().orientation;

        /***********  Load game configuration  ***********/

        
        /***********  Object initialization  ***********/
        // Initialize game status
        myGameStatus.level  = 1;
        myGameStatus.score  = 0;
        myGameStatus.bricks_left    = NUM_BRICK_START;
        myGameStatus.speed  = 1;

        // Initialize bricks array



        // Initialize ball
        myBall.ball_init(container, display_mode);

        // Initialize

    }

    /*********************************************************
     *
     * Collision Detection
     *
     *********************************************************/
    public int collision_detection(Ball ball, Brick brick){
        // [M]
        int col_axis = 0;

        // calculate distance
        double x_dist, y_dist, z_dist, distance;
        x_dist  =  Math.pow( (double)(ball.coor.x - brick.coor.x), 2 );
        y_dist  =  Math.pow( (double)(ball.coor.y - brick.coor.y), 2 );
        z_dist  =  Math.pow( (double)(ball.coor.z - brick.coor.z), 2 );
        distance = Math.sqrt( x_dist + y_dist + z_dist );

        // determine collision
        if(distance < ball.radius + brick.width/2){
            // collision at x
            col_axis = 1;
        }
        if(distance < ball.radius + brick.length/2){
            // collision at y
            col_axis = 2;
        }
        if(distance < ball.radius + brick.height/2){
            // collision at z
            col_axis = 3;
        }

        // update bricks
        brick.brick_hit();

        // update ball
        ball.update_direction(col_axis);

        // update GameStatus
        myGameStatus.bricks_left -= 1;
        myGameStatus.score       += 100;



        // check if end game
        if( myGameStatus.bricks_left ==0 ){
            Log.d(LOG_GAMESTATUS, "Level End");
        }

        return col_axis;
    }

    /*********************************************************
     *
     *
     *
     *********************************************************/

}
