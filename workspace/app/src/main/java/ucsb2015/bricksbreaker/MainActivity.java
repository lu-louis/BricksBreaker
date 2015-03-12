package ucsb2015.bricksbreaker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private final int[]   DIMENSION = new int[]{3,9,6};
    private final int   DIM_SCALE = 1000;
    private final int   X_INDEX = 0;
    private final int   Y_INDEX = 1;
    private final int   Z_INDEX = 2;
    private final String LOG_GAMESTATUS = "LOG_GS";

    private MyGLSurfaceView mGLView;
    private Camera camera;
    private SurfaceView surfaceview;
    private boolean preview;
    private static final String TAG = "OpenGLES20Complete";
    private FrameLayout mFrameLayout;


    GameStatus mGameStatus;
    int game_level;
    ArrayList<BrickSetting> mBrickSettingList = new ArrayList<BrickSetting>();
    BallSetting mBall;
    int numBrick_x;
    int numBrick_y;
    int numBrick_z;
    int i,j,k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get game setting
        // retrieve message from previous intent
        // mGameStatus.level =


        // Game View initialization
        // add game view (GL serfaceview)
        mGLView = new MyGLSurfaceView(this, game_level);
        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        surfaceview=new SurfaceView(this);
        surfaceview.getHolder().addCallback(new SurfaceCallback());
        mFrameLayout=new FrameLayout(this);
        mFrameLayout.addView(mGLView);
        mFrameLayout.addView(surfaceview);
        setContentView(mFrameLayout);

        // Game logic
        // create bricks list
        switch(game_level){
            case 1:
            case 2:
            case 3:
                // temporal setting
                numBrick_x = 3;
                numBrick_y = 2;
                numBrick_z = 6;
                Coordinate location = new Coordinate();
                BrickSetting tmpBrickSetting;
                for(i = 0 ; i<numBrick_x ; i++){
                    location.x = i;
                    for(k = 0 ; k<numBrick_z ; k++){
                        location.z = k;
                        for (j=0 ; j<numBrick_y ; j++){
                            if(j!=0) {
                                location.y = DIMENSION[Y_INDEX] - 1;
                            }
                            else{
                                location.y = j;
                            }
                            tmpBrickSetting = new BrickSetting(i+j+k, location);
                            mBrickSettingList.add(tmpBrickSetting);
                        }
                    }
                }
                break;
        }
        // create ball lists

        // Game loop
        while(mGameStatus.bricks_left!=0){
            // collision detection
            for(i = 0 ; i<numBrick_x ; k++)
                for(j = 0 ; j<numBrick_y ; k++)
                    for(k = 0 ; k<numBrick_z ; k++)
                        collision_detection(mBall, mBrickSettingList.get(i+j+k));
            //
            mGLView.update_ball_loc(mBall.coor);

        }

    }

    /***********************************************
     *
     * Menu Setting
     *
     ***********************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /***********************************************
     *
     * Camera Preview function
     *
     ***********************************************/
    private final class SurfaceCallback implements SurfaceHolder.Callback{

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height){


        }
        public void surfaceCreated(SurfaceHolder holder){
            try {
                camera = Camera.open();
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                Camera.Size   size = sizes.get(0);
                parameters.setPreviewSize(size.width, size.height);

                parameters.setPictureFormat(ImageFormat.JPEG);

                parameters.setPictureSize(size.width, size.height);
                camera.setDisplayOrientation(90);
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceview.getHolder());
                camera.startPreview();
                preview = true;

            } catch(IOException e){
                Log.e(TAG, e.toString());
            }
        }
        public void surfaceDestroyed(SurfaceHolder holder){
            if(camera!=null)
                if(preview)
                    camera.stopPreview();
            camera.release();


        }
    }


    /*********************************************************
     *
     * Collision Detection
     *
     *********************************************************/
    public int collision_detection(BallSetting ball, BrickSetting brick){
        // [M]
        int col_axis = 0;

        if(!brick.visibility){
            return col_axis;
        }
        // calculate distance
        double x_dist, y_dist, z_dist, distance;
        x_dist  =  Math.pow( (double)(ball.coor.x - brick.absCoor.x), 2 );
        y_dist  =  Math.pow( (double)(ball.coor.y - brick.absCoor.y), 2 );
        z_dist  =  Math.pow( (double)(ball.coor.z - brick.absCoor.z), 2 );
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
        mGLView.brick_hit(brick.id);

        // update ball
        ball.update_direction(col_axis);


        // update GameStatus
        mGameStatus.bricks_left -= 1;
        mGameStatus.score       += 100;



        // check if end game
        if( mGameStatus.bricks_left ==0 ){
            Log.d(LOG_GAMESTATUS, "Level End");
        }

        return col_axis;
    }

    /*********************************************************
     *
     *  Game Setting configuration
     *  Need to change to xml in the future
     *
     *********************************************************/
    public void game_setting(){

    }
}
