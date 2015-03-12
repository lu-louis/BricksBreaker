package ucsb2015.bricksbreaker;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Random;
/**
 * Created by yumengyin on 3/1/15.
 */
class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private final double   DIM_SCALE = 1000.0;
    private final double   DIM_X     = 1500.0;

    public volatile int randomInt1,randomInt2,randomInt3;

    public MyGLSurfaceView(Context context, int level) {
        super(context);


        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setZOrderOnTop(true);
        setEGLConfigChooser(8,8,8,8,16,0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);


        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mRenderer.set_game_level(level);

    }




    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();



        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int touch_x = (int) x;
                int touch_y = (int) y;

                mRenderer.touch_event(touch_x, touch_y);
                /*
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }
                System.out.print("width = " + getWidth());
                System.out.print("height = " + getHeight());
                mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
                */
                requestRender();
        }

        return true;
    }

    /*
    public void brick_hit(int index){
        mRenderer.brick_hit(index);
    }

    public void update_ball_loc(Coordinate coor){
        Coordinate coor_tran = new Coordinate();
        //coor_tran = CoorTranslate(coor);
        mRenderer.update_ball_loc(coor);
    }

    public Coordinate CoorTranslate(Coordinate coor){
        return coor;
    }
    */
}