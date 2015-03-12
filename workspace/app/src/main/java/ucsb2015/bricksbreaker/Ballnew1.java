package ucsb2015.bricksbreaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by yumengyin on 3/8/15.
 */
public class Ballnew1 {
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
                    "  gl_Position = uMVPMatrix * vPosition;" +
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
                    "  gl_FragColor =  texColor*0.5;" +
                    "}";


    //
    public volatile Coordinate   coor = new Coordinate();       // location of the brick
    public int          width = 1;//z
    public int          height = 2;//y
    public int          length = 4;//x
    public boolean      visibility = true; // whether the brick is hit or not

    private final FloatBuffer vertexBuffer,texCoordBuffer,colorBuffer;
    private final ShortBuffer orderBuffer;
    private final int mProgram;
    private int mPositionHandle,mTexCoordHandle;//
    private int  mTextureUniformHandle,mColorHandle;//
    private int mMVPMatrixHandle;
    private int mTextureDataHandle;
    private int latitudeBands = 8;
    private int longitudeBands = 18;

    private double M_PI = 3.14;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    private float BallCoords[];

    private final int vertexCount = latitudeBands*longitudeBands*4;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex (should be 4 bytes per float!?)

    //===================================
    static final int COORDS_PER_TEX = 2;
    static float texCoord[];
    private short order[];



    private float radius = 0.2f;
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
    public Ballnew1(Context context) {
        BallCoords = new float[latitudeBands*longitudeBands*12];//10800
        order = new short[latitudeBands*longitudeBands*6];
        texCoord = new float[latitudeBands*longitudeBands*8];

        coor.x = 0;
        coor.y = - 1;
        coor.z = 0;

        //private int latitudeBands = 30;
        //private int longitudeBands = 30;
        //int count = 0;
        Log.d("T", latitudeBands+" ");
        Log.d("T", longitudeBands+" ");
        int index;
        for (int i = 0; i < latitudeBands; i++) {
            for (int j=0; j < longitudeBands; j++) {

                BallCoords[i*longitudeBands*4*3 + j*4*3] = radius * (float)Math.sin((i)*M_PI/latitudeBands)*(float)Math.cos(2*(j)*M_PI/longitudeBands);
                BallCoords[i*longitudeBands*4*3 + j*4*3 + 1] = radius * (float)Math.cos((i)*M_PI/latitudeBands)*(float)Math.cos(2*(j)*M_PI/longitudeBands);
                BallCoords[i*longitudeBands*4*3 + j*4*3 + 2] = radius * (float)Math.sin(2*(j)*M_PI/longitudeBands);



                BallCoords[i*longitudeBands*4*3 + j*4*3 + 3] = radius * (float)Math.sin((i+1)*M_PI/latitudeBands)*(float)Math.cos(2*(j)*M_PI/longitudeBands);
                BallCoords[i*longitudeBands*4*3 + j*4*3 + 4] = radius * (float)Math.cos((i+1)*M_PI/latitudeBands)*(float)Math.cos(2*(j)*M_PI/longitudeBands);
                BallCoords[i*longitudeBands*4*3 + j*4*3 + 5] = radius * (float)Math.sin(2*(j)*M_PI/longitudeBands);

                BallCoords[i*longitudeBands*4*3 + j*4*3 + 6] = radius * (float)Math.sin((i)*M_PI/latitudeBands)*(float)Math.cos(2*(j+1)*M_PI/longitudeBands);
                BallCoords[i*longitudeBands*4*3 + j*4*3 + 7] = radius * (float)Math.cos((i)*M_PI/latitudeBands)*(float)Math.cos(2*(j+1)*M_PI/longitudeBands);
                BallCoords[i*longitudeBands*4*3 + j*4*3 + 8] = radius * (float)Math.sin(2*(j+1)*M_PI/longitudeBands);

                BallCoords[i*longitudeBands*4*3 + j*4*3 + 9] = radius * (float)Math.sin((i+1)*M_PI/latitudeBands)*(float)Math.cos(2*(j+1)*M_PI/longitudeBands);
                BallCoords[i*longitudeBands*4*3 + j*4*3 + 10] = radius * (float)Math.cos((i+1)*M_PI/latitudeBands)*(float)Math.cos(2*(j+1)*M_PI/longitudeBands);
                BallCoords[i*longitudeBands*4*3 + j*4*3 + 11] = radius * (float)Math.sin(2*(j+1)*M_PI/longitudeBands);
                index = i*longitudeBands*4*3+j*4*3+11;
                if(index > 10799)
                    Log.d("T", index+" ");


                order[i*longitudeBands*6 + j*6]   =(short)(0+i*longitudeBands*4 + j*4);
                order[i*longitudeBands*6 + j*6+1] =(short)(1+i*longitudeBands*4 + j*4);
                order[i*longitudeBands*6 + j*6+2] =(short)(3+i*longitudeBands*4 + j*4);
                order[i*longitudeBands*6 + j*6+3] =(short)(3+i*longitudeBands*4 + j*4);
                order[i*longitudeBands*6 + j*6+4] =(short)(2+i*longitudeBands*4 + j*4);
                order[i*longitudeBands*6 + j*6+5] =(short)(0+i*longitudeBands*4 + j*4);

                /*
                0.0f, 2/6f,
                        1.0f, 2/6f,
                        0.0f, 1/6f,
                        1.0f, 1/6f,
                  */
                texCoord[i*longitudeBands*8 + j*8] = 0.0f;
                texCoord[i*longitudeBands*8 + j*8 + 1] = ((i*longitudeBands+j)%6)/6f;

                texCoord[i*longitudeBands*8 + j*8 + 2] = 1.0f;
                texCoord[i*longitudeBands*8 + j*8 + 3] = ((i*longitudeBands+j)%6)/6f;

                texCoord[i*longitudeBands*8 + j*8 + 4] = 0.0f;
                texCoord[i*longitudeBands*8 + j*8 + 5] = ((i*longitudeBands+j+1)%6)/6f;

                texCoord[i*longitudeBands*8 + j*8 + 6] = 1.0f;
                texCoord[i*longitudeBands*8 + j*8 + 7] = ((i*longitudeBands+j+1)%6)/6f;


            }

        }

        coor.x = 0;
        coor.y = 1;
        coor.z = 3;

        for (int i = 0; i < vertexCount; i++) {
            BallCoords[i * 3] = BallCoords[i * 3]  + coor.x * (float) 0.25;
            BallCoords[i * 3 + 1] = BallCoords[i * 3 + 1]  + coor.y * (float) 1.2;
            BallCoords[i * 3 + 2] = BallCoords[i * 3 + 2]  + coor.z * (float) 0.5;

        }


        //===================================
        // shape coordinate
        //===================================
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                BallCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(BallCoords);
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




        ByteBuffer obb = ByteBuffer.allocateDirect(
                order.length * 4);
        obb.order(ByteOrder.nativeOrder());

        orderBuffer = obb.asShortBuffer();
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
            //Log.i("chuu", "Error: mColorHandle = " + mColorHandle);

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
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
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

            for (int i = 0; i < latitudeBands*longitudeBands; i++) {
                orderBuffer.position(6*i );//* ((i+blickNum)%6));
                // Draw the triangle
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, orderBuffer);
            }
            // Disable vertex array
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
