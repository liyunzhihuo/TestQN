package com.hssb.testqn.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SquareGLSurfaceView extends GLSurfaceView {
    private static final String TAG = "SquareGLSurfaceView";
    DemoRenderer mRenderer;
    public SquareGLSurfaceView(Context context){
        this(context,null);
    }
    public SquareGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //为了可以激活log和错误检查，帮助调试3D应用，需要调用setDebugFlags()。
        this.setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
//        mRenderer = new DemoRenderer();
//        this.setRenderer(mRenderer);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        Log.i(TAG, "specify width mode:" + MeasureSpec.toString(widthMeasureSpec) + " size:" + width);
        Log.i(TAG, "specify height mode:" + MeasureSpec.toString(heightMeasureSpec) + " size:" + height);

        setMeasuredDimension(width, width);
    }
    class DemoRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onDrawFrame(GL10 gl) {
            //每帧都需要调用该方法进行绘制。绘制时通常先调用glClear来清空framebuffer。
            //然后调用OpenGL ES其他接口进行绘制
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            //当surface的尺寸发生改变时，该方法被调用，。往往在这里设置ViewPort。或者Camara等。
            gl.glViewport(0, 0, w, h);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 该方法在渲染开始前调用，OpenGL ES的绘制上下文被重建时也会调用。
            //当Activity暂停时，绘制上下文会丢失，当Activity恢复时，绘制上下文会重建。
        }
    }
}
