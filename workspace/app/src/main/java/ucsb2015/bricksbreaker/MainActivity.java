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


    private MyGLSurfaceView mGLView;
    private Camera camera;
    private SurfaceView surfaceview;
    private boolean preview;
    private static final String TAG = "OpenGLES20Complete";
    private FrameLayout mFrameLayout;

    public final static String LEVEL = "GameLevel";
    String game_level_string;
    int game_level;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get game setting
        // retrieve message from previous intent
        // mGameStatus.level =
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game_level_string = extras.getString(LEVEL);
            game_level = 0;
            // switch statement can be replace by string processing
            // extract last character
            // change char into integer
            switch(game_level_string){
                case "level_1":
                    game_level = 1;
                    break;
                case "level_2":
                    game_level = 2;
                    break;
                case "level_3":
                    game_level = 3;
                    break;
            }
        }


        // Game View initialization
        // add game view (GL serfaceview)
        if(game_level==0){
            Log.e("GM", "Game level error");
            return;
        }
        mGLView = new MyGLSurfaceView(this, game_level);
        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        surfaceview = new SurfaceView(this);
        surfaceview.getHolder().addCallback(new SurfaceCallback());
        mFrameLayout = new FrameLayout(this);
        mFrameLayout.addView(mGLView);
        mFrameLayout.addView(surfaceview);
        setContentView(mFrameLayout);


    }

    /**
     * ********************************************
     * <p/>
     * Menu Setting
     * <p/>
     * *********************************************
     */
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

    /**
     * ********************************************
     * <p/>
     * Camera Preview function
     * <p/>
     * *********************************************
     */
    private final class SurfaceCallback implements SurfaceHolder.Callback {

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {


        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open();
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                Camera.Size size = sizes.get(0);
                parameters.setPreviewSize(size.width, size.height);

                parameters.setPictureFormat(ImageFormat.JPEG);

                parameters.setPictureSize(size.width, size.height);
                camera.setDisplayOrientation(90);
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceview.getHolder());
                camera.startPreview();
                preview = true;

            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null)
                if (preview)
                    camera.stopPreview();
            camera.release();


        }
    }
}


