package ucsb2015.bricksbreaker;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;
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
 */
public class MyGLRenderer extends Activity implements GLSurfaceView.Renderer {

    private Context _context;

    private static final String TAG = "MyGLRenderer";
    private List<Brick> mBrickList = new ArrayList<Brick>();
    //private List<Ballnew> mBallnewList = new ArrayList<Ballnew>();
    private Ballnew mBallnew ;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];


    public MyGLRenderer(Context context) {
        _context = context;

    }

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;

    public volatile int randomInt1,randomInt2,randomInt3;

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        /*
        for(int i = 0 ;i<20; i++) {
            Ballnew mBallnew = new Ballnew(_context, i);
            mBallnewList.add(mBallnew);
        }
        */
        mBallnew = new Ballnew(_context);

        for(int i = 0 ;i<90; i++) {
            Brick mBrick = new Brick(_context, i);
            mBrickList.add(mBrick);
        }
    }


    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(mVMatrix, 0, 0,4,3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


        // Draw bricks
        for(int i = 0 ;i<90; i++) {
            // Calculate the projection and view transformation
           // Matrix.setLookAtM(mVMatrix,0,-2.0f, 2.0f, -4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

            Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 1.0f, 0f);

            // Combine the rotation matrix with the projection and camera view
            Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);


            mBrickList.get(i).draw(mMVPMatrix);
        }


            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

            Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 1.0f, 0f);

            // Combine the rotation matrix with the projection and camera view
            Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);


          //  mBallnewList.get(i).draw(mMVPMatrix);
        mBallnew.draw(mMVPMatrix);

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



}
