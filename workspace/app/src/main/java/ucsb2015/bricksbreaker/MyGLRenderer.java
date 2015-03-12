package ucsb2015.bricksbreaker;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yumengyin on 3/1/15.
 * Container info
 *      Game Env container
 *
 */
public class MyGLRenderer extends Activity implements GLSurfaceView.Renderer {
    volatile float x=0;
    private Context _context;

    private final boolean   TEST_INFO = true;
    private final String    BRICK_DEBUG = "BrickDebugInfo";
    /*****************************************
     *
     * Game control object
     *
     *****************************************/
    private final String LOG_GAMESTATUS = "LOG_GS";
    public static final String GMO = "Game Mechanism Object";

    private final int[]   DIMENSION = new int[]{3,9,6};
    private final int   DIM_SCALE = 1000;
    private final int   X_INDEX = 0;
    private final int   Y_INDEX = 1;
    private final int   Z_INDEX = 2;
    public static final int CONTAINER_SIZE_X = 1500;    // 400, 400*3+100*(3-1) = 1400
    public static final int CONTAINER_SIZE_Y = 3000;    // 200, 200*9+100*(9-1) = 2600
    public static final int CONTAINER_SIZE_Z = 1200;    // 100, 100*6+100*(6-1) = 1100
    private int display_width;
    private int display_height;


    public List<BrickSetting>  mBrickSettingList = new ArrayList<>();
    public BallSetting mBall = new BallSetting();
    public GameStatus mGameStatus = new GameStatus();


    int i,j,k;




    /*****************************************
     *
     * GL Renderer Object
     *
     *****************************************/
    private static final String TAG = "MyGLRenderer";
    public  volatile    List<Brick>     mBrickList = new ArrayList<Brick>();
    public  volatile    Ballnew1        mBallnew1;

    private final float[]   mMVPMatrix        = new float[16];
    private final float[]   mMVPMatrix_Brick  = new float[16];
    private final float[]   mProjMatrix       = new float[16];
    private final float[]   mVMatrix          = new float[16];
    private final float[]   mRotationMatrix   = new float[16];


    public MyGLRenderer(Context context) {
        _context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        display_width   = size.x;
        display_height  = size.y;
    }

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;

    public volatile int randomInt1,randomInt2,randomInt3;

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        /*****************************************
         *
         * Game initialization
         *
         *****************************************/

        // Game status
        //set_level(mGameStatus.level);     // set by GLSurfaceView call
        set_container();

        // create bricks list
        switch(mGameStatus.level){
            case 1:
            case 2:
            case 3:
                // temporal setting
                mGameStatus.numBrick.x = 3;
                mGameStatus.numBrick.y = 2;
                mGameStatus.numBrick.z = 6;
                Coordinate location = new Coordinate();
                BrickSetting tmpBrickSetting;
                for(i = 0 ; i<mGameStatus.numBrick.x ; i++){
                    location.x = i;
                    for(k = 0 ; k<mGameStatus.numBrick.z ; k++){
                        location.z = k;
                        for (j=0 ; j<mGameStatus.numBrick.y ; j++){
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

                if(TEST_INFO){
                    for(i=0 ; i<mBrickSettingList.size() ; i++){
                        // print id
                        Log.d(BRICK_DEBUG, "--- index : "+i+" ---");
                        // print relative coordinate
                        Log.d(BRICK_DEBUG, "--- rel ---");
                        Log.d(BRICK_DEBUG, "X : "+mBrickSettingList.get(i).relCoor.x);
                        Log.d(BRICK_DEBUG, "Y : "+mBrickSettingList.get(i).relCoor.y);
                        Log.d(BRICK_DEBUG, "Z : "+mBrickSettingList.get(i).relCoor.z);
                        // print absolute coordinate
                        Log.d(BRICK_DEBUG, "--- abs ---");
                        Log.d(BRICK_DEBUG, "X : "+mBrickSettingList.get(i).absCoor.x);
                        Log.d(BRICK_DEBUG, "Y : "+mBrickSettingList.get(i).absCoor.y);
                        Log.d(BRICK_DEBUG, "Z : "+mBrickSettingList.get(i).absCoor.z);
                        Log.d(BRICK_DEBUG, "-----------------------------------");
                    }
                }
                Log.d(BRICK_DEBUG, "Total Brick Created = "+mBrickSettingList.size());
                break;
        }

        // create ball lists

        /*****************************************
         *
         * Draw Basic setting
         *
         *****************************************/

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        mBallnew1 = new Ballnew1(_context);

        for(int i = 0 ;i<90; i++) {
            Brick mBrick = new Brick(_context, i);
            mBrickList.add(mBrick);
        }


    }


    public void onDrawFrame(GL10 unused) {

        /*****************************************
         *
         * Draw objects section
         *
         *****************************************/
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //Matrix.setLookAtM(mVMatrix, 0, 0,(float)3,(float)3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mVMatrix, 0, 0,(float)4.5f,(float)1.5f, 0f, 0f, 1.5f, 0f, 0.0f, 1f);


        float [] mMVMatrix = new float[16];


        // Draw bricks
        for(int i = 0 ;i<36; i++) {
            // Combine the rotation matrix with the projection and camera view

            Matrix.multiplyMM(mMVPMatrix_Brick, 0, mProjMatrix, 0, mVMatrix, 0);

            mBrickList.get(i).draw(mMVPMatrix_Brick);
        }

        Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 1.0f, 0f);
        Matrix.multiplyMM(mMVMatrix, 0, mVMatrix, 0, mRotationMatrix, 0);
        x+=0.2;

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVMatrix, 0);
        Matrix.translateM(mMVPMatrix,0, mMVPMatrix,0,0,-x,0);


          //  mBallnewList.get(i).draw(mMVPMatrix);
        mBallnew1.draw(mMVPMatrix);

        /*****************************************
         *
         * Game mechanism section
         *  Check collision
         *      update brick_visibility
         *      update ball_direction
         *  Update ball_location
         *
         *****************************************/
        /*
        // collision detection
        for(i = 0 ; i<mGameStatus.numBrick_x ; k++)
            for(j = 0 ; j<mGameStatus.numBrick_y ; k++)
                for(k = 0 ; k<mGameStatus.numBrick_z ; k++)
                    collision_detection(mBall, mBrickSettingList.get(i+j+k), i+j+k);

        // Game end change
        if(mGameStatus.bricks_left==0)
            Log.d(LOG_GAMESTATUS, "Game End");
        */
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }








    /*********************************************************
     * -------------------------------------------------------
     *                Game Control Function
     * -------------------------------------------------------
    *********************************************************/
    public void set_game_level(int level){
        mGameStatus.level = level;
    }

    public void update_ball_loc(Coordinate coor){
        mBallnew1.coor.x = coor.x;
        mBallnew1.coor.y = coor.y;
        mBallnew1.coor.z = coor.z;
    }

    public void touch_event(int touch_x, int touch_y){

    }
    /*********************************************************
     *
     * Collision Detection
     *
     *********************************************************/
    public int collision_detection(BallSetting ball, BrickSetting brick, int index){
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

        // update brickSetting
        brick.brick_hit();
        // update GLbrick
        brick_hit(index);

        // update ball
        ball.update_direction(col_axis, mGameStatus.container_size);


        // update GameStatus
        mGameStatus.bricks_left -= 1;
        mGameStatus.score       += 100;



        // check if end game
        if( mGameStatus.bricks_left ==0 ){
            Log.d(LOG_GAMESTATUS, "Level End");
        }

        return col_axis;
    }

    public void brick_hit(int index){
        mBrickList.get(index).visibility = false;
    }


    /*********************************************************
     *
     *  Game Setting configuration
     *  Need to change to xml in the future
     *
     *********************************************************/
    public void set_container(){

        mGameStatus.container_size.x = CONTAINER_SIZE_X;
        mGameStatus.container_size.y = CONTAINER_SIZE_Y;
        mGameStatus.container_size.z = CONTAINER_SIZE_Z;
    }

}
