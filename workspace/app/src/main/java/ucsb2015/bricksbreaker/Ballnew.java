package ucsb2015.bricksbreaker;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.lang.Math;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumengyin on 3/2/15.
 */

public class Ballnew {

    private int latitudeBands = 6;
    private int longitudeBands = 15;

    private List<BallPart> mBallPartList = new ArrayList<BallPart>();

    //===================================
    public Ballnew(Context context) {


        for (int j = 0; j < longitudeBands; j++) {
            BallPart mBallPart = new BallPart(context, j);
            mBallPartList.add(mBallPart);
        }
    }

    public void draw(float[] mvpMatrix) {

        for (int i = 0; i < longitudeBands; i++) {
            mBallPartList.get(i).draw(mvpMatrix);
        }
    }


}
