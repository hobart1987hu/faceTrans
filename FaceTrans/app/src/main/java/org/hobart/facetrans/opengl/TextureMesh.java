package org.hobart.facetrans.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import org.hobart.facetrans.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;


public class TextureMesh {

    private int[] textureId;

    private Bitmap texture;

    private Vertex[] vertexes;

    private Shader shader;

    private FloatBuffer vertexBuffer, textureCoordBuffer;

    public TextureMesh(Context context) {
        shader = new Shader();
        shader.setProgram(context, R.raw.vertex_shader, R.raw.fragment_shader);

        vertexBuffer = ByteBuffer.allocateDirect(4 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        textureCoordBuffer = ByteBuffer.allocateDirect(2 * 4 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordBuffer.put(new float[]{
                0, 0,
                0, 1,
                1, 0,
                1, 1
        });
    }

    public void draw(float[] projectionMatrix) {
        if (vertexes == null) {
            return;
        }

        initBuffer();

        this.shader.useProgram();

        if (textureId == null) {
            textureId = new int[1];

            glGenTextures(1, textureId, 0);

            glBindTexture(GL_TEXTURE_2D, textureId[0]);

            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        } else {
            glBindTexture(GL_TEXTURE_2D, textureId[0]);
        }

        if (texture != null) {
            glEnable(GL_TEXTURE_2D);
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, texture, 0);
            glDisable(GL_TEXTURE_2D);

            this.texture.recycle();
            this.texture = null;
        }

        int aTextureCoord = this.shader.getHandle("aTextureCoord");
        glVertexAttribPointer(aTextureCoord, 2, GL_FLOAT, false,
                0, textureCoordBuffer);
        glEnableVertexAttribArray(aTextureCoord);

        glUniformMatrix4fv(shader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);

        int aPosition = this.shader.getHandle("aPosition");
        glVertexAttribPointer(aPosition, 4, GL_FLOAT, false,
                4 * 4, vertexBuffer);
        glEnableVertexAttribArray(aPosition);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        //unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setTexture(Bitmap bitmap) {
        this.texture = bitmap;
    }

    public boolean hasTexture() {
        return this.texture != null;
    }

    public void setVertexes(Vertex[] vertexes) {
        this.vertexes = vertexes;
    }

    public void clear() {
        vertexes = null;
    }

    public boolean isClear() {
        return vertexes == null;
    }

    private void initBuffer() {
        this.vertexBuffer.clear();

        for (Vertex v : vertexes) {
            this.vertexBuffer.put(v.getPosition());
        }

        this.vertexBuffer.position(0);

        textureCoordBuffer.position(0);
    }
}
