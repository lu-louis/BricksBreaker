package ucsb2015.bricksbreaker;

/**
 * Created by LuLouis on 2/26/15.
 */


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

import javax.microedition.khronos.opengles.GL10;

public class Brick {
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +

                    "attribute vec4 vPosition;" +
                    "attribute vec4 a_color;" +
                    "attribute vec2 tCoordinate;" +
                    "varying vec2 v_tCoordinate;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    "  gl_Position =  uMVPMatrix * vPosition;" +
                    "	v_tCoordinate = tCoordinate;" +
                    "	v_Color = a_color;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 v_Color;" +
                    "varying vec2 v_tCoordinate;" +
                    "uniform sampler2D s_texture;" +
                    "void main() {" +
                    // texture2D() is a build-in function to fetch from the texture map
                    "	vec4 texColor = texture2D(s_texture, v_tCoordinate); " +
                    "  gl_FragColor = texColor*0.5;" +
                    "}";


    //
    public int      blickNum;
    public volatile Coordinate coor = new Coordinate();       // location of the brick
    public int      width   = 1;    //z
    public int      height  = 2;    //y
    public int      length  = 4;    //x
    public boolean  visibility = true; // whether the brick is hit or not

    // object drawing parameter
    private final   FloatBuffer vertexBuffer,texCoordBuffer,colorBuffer;
    private final   ByteBuffer  orderBuffer;
    private final   int         mProgram;
    private int     mPositionHandle,mTexCoordHandle;//
    private int     mTextureUniformHandle,mColorHandle;//
    private int     mMVPMatrixHandle;
    private int     mTextureDataHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    private float BrickCoords[] = {
            //Vertices according to faces

            -0.05f, -0.05f, 0.05f, //H   front
            0.05f, -0.05f, 0.05f,  //E
            -0.05f, 0.05f, 0.05f,  //G
            0.05f, 0.05f, 0.05f,   //F

            0.05f, -0.05f, 0.05f,  //E
            0.05f, -0.05f, -0.05f, //B
            0.05f, 0.05f, 0.05f,    //F
            0.05f, 0.05f, -0.05f,//C

            0.05f, -0.05f, -0.05f,//B
            -0.05f, -0.05f, -0.05f,//A
            0.05f, 0.05f, -0.05f,//C
            -0.05f, 0.05f, -0.05f,//D

            -0.05f, -0.05f, -0.05f,//A
            -0.05f, -0.05f, 0.05f,//H
            -0.05f, 0.05f, -0.05f,//D
            -0.05f, 0.05f, 0.05f,//G

            -0.05f, -0.05f, -0.05f,//A
            0.05f, -0.05f, -0.05f,//B
            -0.05f, -0.05f, 0.05f,//H
            0.05f, -0.05f, 0.05f,//E

            -0.05f, 0.05f, 0.05f,//G
            0.05f, 0.05f, 0.05f,//F
            -0.05f, 0.05f, -0.05f,//D
            0.05f, 0.05f, -0.05f,//C
    };

    private final int vertexCount = BrickCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex (should be 4 bytes per float!?)

    //===================================
    static final int COORDS_PER_TEX = 2;
    static float texCoord[] = {
            0.0f, 1/6f,
            1.0f, 1/6f,
            0.0f, 0.0f,
            1.0f, 0.0f,

            0.0f, 2/6f,
            1.0f, 2/6f,
            0.0f, 1/6f,
            1.0f, 1/6f,

            0.0f, 3/6f,
            1.0f, 3/6f,
            0.0f, 2/6f,
            1.0f, 2/6f,

            0.0f, 4/6f,
            1.0f, 4/6f,
            0.0f, 3/6f,
            1.0f, 3/6f,

            0.0f, 5/6f,
            1.0f, 5/6f,
            0.0f, 4/6f,
            1.0f, 4/6f,

            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 5/6f,
            1.0f, 5/6f,

    };

    private byte order[] = {
            //Faces definition

            0,1,3, 0,3,2,           //Face front
            4,5,7, 4,7,6,           //Face right
            8,9,11, 8,11,10,        //...
            12,13,15, 12,15,14,
            16,17,19, 16,19,18,
            20,21,23, 20,23,22,

    };



    private final int texCoordStride = COORDS_PER_TEX * 4; // 4 bytes per float

    //===================================
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };

    // Set another color
    static final int COLORB_PER_VER = 4;
    static float colorBlend[] = {
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };
    //private final int colorBlendCount = colorBlend.length / COLORB_PER_VER;
    private final int colorBlendStride = COLORB_PER_VER * 4;

    //===================================

    public Brick(Context context,int number) {
        coor.x = (number % 3 - 1);
        coor.y = (number / 3)%6;

        blickNum = number;
        if(number/18==0){
            coor.z = 5;
        }
        else{
            coor.z = 1;
        }

        for (int i = 0; i < vertexCount; i++) {
            BrickCoords[i * 3] = BrickCoords[i * 3]*length + coor.x * (float) 0.5;
            BrickCoords[i * 3 + 1] = BrickCoords[i * 3 + 1]*width + coor.y * (float) 0.2;
            BrickCoords[i * 3 + 2] = BrickCoords[i * 3 + 2]*height + coor.z * (float) 0.5;

        }


        //===================================
        // shape coordinate
        //===================================
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                BrickCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(BrickCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //===================================
        // texture coordinate
        //===================================
        // initialize texture coord byte buffer for texture coordinates
        ByteBuffer texbb = ByteBuffer.allocateDirect(
                texCoord.length * 4);
        // use the device hardware's native byte order
        texbb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        texCoordBuffer = texbb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        texCoordBuffer.put(texCoord);
        // set the buffer to read the first coordinate
        texCoordBuffer.position(0);


        //===================================
        // color
        //===================================
        ByteBuffer cbb = ByteBuffer.allocateDirect(
                colorBlend.length * 4);
        cbb.order(ByteOrder.nativeOrder());

        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colorBlend);
        colorBuffer.position(0);


        orderBuffer = ByteBuffer.allocateDirect(order.length);
        orderBuffer.put(order);
        orderBuffer.position(0);

        //===================================
        // loading an image into texture
        //===================================
        mTextureDataHandle = loadTexture(context, R.drawable.colorpic);

        //===================================
        // shader program
        //===================================
        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables


    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);


        if (visibility == true) {
            // get handle to vertex shader's vPosition member
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);


            // setting vertex color
            mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
            Log.i("chuu", "Error: mColorHandle = " + mColorHandle);

            GLES20.glEnableVertexAttribArray(mColorHandle);
            GLES20.glVertexAttribPointer(mColorHandle, COLORB_PER_VER,
                    GLES20.GL_FLOAT, false,
                    colorBlendStride, colorBuffer);
            MyGLRenderer.checkGlError("glVertexAttribPointer...color");


            // setting texture coordinate to vertex shader
            mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "tCoordinate");
            GLES20.glEnableVertexAttribArray(mTexCoordHandle);
            GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEX,
                    GLES20.GL_FLOAT, false,
                    texCoordStride, texCoordBuffer);
            MyGLRenderer.checkGlError("glVertexAttribPointer...texCoord");


            // get handle to shape's transformation matrix
            //  mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            MyGLRenderer.checkGlError("glGetUniformLocation");

            // Apply the projection and view transformation
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
            MyGLRenderer.checkGlError("glUniformMatrix4fv");


            // texture
            mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");

            // Set the active texture unit to texture unit 0.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES20.glUniform1i(mTextureUniformHandle, 0);


//            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 12*3);

            for (int i = 0; i < 6; i++) {
                orderBuffer.position(6*i );
                // Draw the triangle
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_BYTE, orderBuffer);
            }
            // Disable vertex array
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }


    /***********************************************
     *
     * function: Brick initialization function
     *
     ***********************************************/
    /*
    public Brick(int x, int y, int z, int w, int h, int l, int v, int c){
        Coordinate coor = new Coordinate();
        coor.x = x;
        coor.y = y;
        coor.z = z;

        width   = w;
        height  = h;
        length  = l;

        visibility = true;
    }
    */

    /***********************************************
     *
     * function: brick_hit
     *
     ***********************************************/
    public void brick_hit(){
        visibility = false;
    }
}
