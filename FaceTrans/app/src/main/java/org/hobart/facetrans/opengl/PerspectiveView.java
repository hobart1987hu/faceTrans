package org.hobart.facetrans.opengl;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

public class PerspectiveView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private float[] projectionMatrix = new float[16];

    private float[] viewMatrix = new float[16];

    private float[] modelMatrix = new float[16];

    private float[] mvpMatrix = new float[16];

    private TextureMesh coverMesh, contentMesh;

    private float distance = 10.5f;

    private float width, height, ratio, factor;

    private long duration = 800;

    private Bitmap coverTexture, contentTexture;

    float left = -ratio;
    float right = ratio;
    float top = 1;
    float bottom = -1;


    float origin_left = -ratio;
    float origin_right = ratio;
    float origin_top = 1;
    float origin_bottom = -1;


    float newleft = 0;
    float newright = 0;
    float newtop = 0;
    float newbottom = 0;


    private AnimationCallback animationCallback;

    public void setAnimationCallback(AnimationCallback animationCallback) {
        this.animationCallback = animationCallback;
    }

    public interface AnimationCallback {
        public void onAnimationEnd(boolean isReverse);
    }

    public PerspectiveView(Context context) {
        super(context);
        this.init();
    }

    public void setReverse(boolean isReverse, Bitmap coverTexture, Bitmap contentTexture) {
        this.isReverse = isReverse;
        this.hasTexture = false;
        this.contentTexture = contentTexture;
        this.coverTexture = coverTexture;
    }

    /**
     * 是否反向
     */
    private boolean isReverse = false;

    public void setTextures(Bitmap coverTexture, Bitmap contentTexture, float left, float right, float top, float bottom) {

        this.isReverse = false;
        this.coverTexture = coverTexture;
        this.contentTexture = contentTexture;

        this.hasTexture = false;

        this.origin_left = left;
        this.origin_right = right;
        this.origin_top = top;
        this.origin_bottom = bottom;

        this.left = OpenGlUtils.toGLX(origin_left, ratio, this.width);
        this.right = OpenGlUtils.toGLX(origin_right, ratio, this.width);
        this.bottom = OpenGlUtils.toGLY(origin_bottom, this.height);
        this.top = OpenGlUtils.toGLY(origin_top, this.height);

    }

    private void init() {
        this.setEGLContextClientVersion(2);

        this.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);

        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = distance;

        float lookX = 0.0f;
        float lookY = 0.0f;
        float lookZ = -1.0f;

        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        coverMesh = new TextureMesh(getContext());
        contentMesh = new TextureMesh(getContext());

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        glViewport(0, 0, width, height);

        ratio = (float) width / height;
    }

    float nd;

    /**
     * @param gl10
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (coverTexture == null) {
            return;
        }
        float near = distance;

        float far = 250;

        nd = (near * ratio * 2) / (right - left) - near;

        newleft = left * (nd + near) / near;
        newright = right * (nd + near) / near;
        newtop = top * (nd + near) / near;
        newbottom = bottom * (nd + near) / near;

        if (isReverse) {
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);
        } else {
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, near, far);
        }

        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        setMeshTexture();

        Vertex[] vertexes = getTextureVertexes();

        coverMesh.setVertexes(vertexes);

        contentMesh.setVertexes(getTextureVertexes());

        Matrix.setIdentityM(modelMatrix, 0);
        if (isReverse) {

            Matrix.translateM(modelMatrix, 0, ((newleft + Math.abs(newright - newleft) / 2) * (factor)), ((newtop - Math.abs(newbottom - newtop) / 2) * (factor)), -nd * factor);

        } else {

            Matrix.translateM(modelMatrix, 0, (newleft + Math.abs(newright - newleft) / 2) * (1 - factor), (newtop - Math.abs(newbottom - newtop) / 2) * (1 - factor), -nd * (1 - factor));
        }

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        contentMesh.draw(mvpMatrix);

        Matrix.setIdentityM(modelMatrix, 0);

        if (isReverse) {
            Matrix.translateM(modelMatrix, 0, ((newleft + Math.abs(newright - newleft) / 2) * (factor)), ((newtop - Math.abs(newbottom - newtop) / 2) * (factor)), -nd * factor);
        } else {

            Matrix.translateM(modelMatrix, 0, (newleft + Math.abs(newright - newleft) / 2) * (1 - factor), (newtop - Math.abs(newbottom - newtop) / 2) * (1 - factor), -nd * (1 - factor));
        }

        if (isReverse) {
            Matrix.translateM(modelMatrix, 0, -ratio, 0, 0f);
            Matrix.rotateM(modelMatrix, 0, 90 - 90 * factor, 0, 1, 0);
            Matrix.translateM(modelMatrix, 0, ratio, 0, 0);
        } else {
            Matrix.translateM(modelMatrix, 0, -ratio, 0, 0f);
            Matrix.rotateM(modelMatrix, 0, -90 * factor, 0, 1, 0);
            Matrix.translateM(modelMatrix, 0, ratio, 0, 0);
        }

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        coverMesh.draw(mvpMatrix);
    }

    boolean hasTexture;

    private void setMeshTexture() {
        if (!hasTexture && coverTexture != null) {

            Bitmap bitmap = Bitmap.createBitmap(coverTexture);

            coverMesh.setTexture(bitmap);

            bitmap = Bitmap.createBitmap(contentTexture);
            contentMesh.setTexture(bitmap);

            hasTexture = true;
        }
    }

    private Vertex[] getTextureVertexes() {

        Vertex[] vertexes = new Vertex[]{
                new Vertex(-Math.abs(newright - newleft) / 2, Math.abs(newbottom - newtop) / 2, 0f, 1f),
                new Vertex(-Math.abs(newright - newleft) / 2, -Math.abs(newbottom - newtop) / 2, 0f, 1f),
                new Vertex(Math.abs(newright - newleft) / 2, Math.abs(newbottom - newtop) / 2, 0f, 1f),
                new Vertex(Math.abs(newright - newleft) / 2, -Math.abs(newbottom - newtop) / 2, 0f, 1f)
        };
        return vertexes;
    }

    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final float f = (Float) valueAnimator.getAnimatedValue();
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        factor = f;
                        requestRender();
                    }
                });
            }
        });

        animator.start();
        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                if (animationCallback != null) {
                    animationCallback.onAnimationEnd(isReverse);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });
    }
}
