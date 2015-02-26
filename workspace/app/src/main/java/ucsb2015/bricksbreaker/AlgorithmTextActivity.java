package ucsb2015.bricksbreaker;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;


public class AlgorithmTextActivity extends ActionBarActivity {

    int level;
    Ball        myBall;
    GameStatus  myGameStatus;

    // [M]
    private final int NUM_BRICK_START = 10;


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
        int[] screen_resolution = new int[]{screenWidth, screenHeight};

        // get orientation
        int display_mode = getResources().getConfiguration().orientation;

        /***********  Load configuration  ***********/

        
        /***********  Object initialization  ***********/
        // Initialize game status
        myGameStatus.level  = 1;
        myGameStatus.score  = 0;
        myGameStatus.bricks_left    = NUM_BRICK_START;

        // Initialize bricks array



        // Initialize ball
        myBall.ball_init(screen_resolution, display_mode);

        // Initialize

    }

}
