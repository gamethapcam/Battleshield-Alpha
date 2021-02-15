package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLOnlyTextureData;

import java.util.Stack;

/**
 * Can be nested, unlike {@link FrameBuffer} which cannot.
 */
public class MyFrameBuffer extends FrameBuffer {

    private static final String TAG = MyFrameBuffer.class.getSimpleName();

    private static Stack<MyFrameBuffer> boundFrameBuffers;

    private boolean began;

    protected MyFrameBuffer(GLFrameBufferBuilder<? extends GLFrameBuffer<Texture>> bufferBuilder) {
        super(bufferBuilder);
    }

    public MyFrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth) {
        super(format, width, height, hasDepth);
    }

    public MyFrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
        super(format, width, height, hasDepth, hasStencil);
    }


    @Override
    public void begin() {
        begin(true);
    }

    protected void begin(boolean push) {
        super.begin();

        if (push) {

            if (began)
                throw new IllegalStateException("Cannot call begin() twice without a call to end() in between.");

            began = true;

            if (boundFrameBuffers == null)
                boundFrameBuffers = new Stack<>();

            boundFrameBuffers.push(this);
        }
    }

    @Override
    public void end(int x, int y, int width, int height) {
        if (!began)
            throw new IllegalStateException("begin() must be called before end().");

        began = false;

        boundFrameBuffers.pop();
        if (boundFrameBuffers.isEmpty()) {
            super.end(x, y, width, height); // Binds the default frame buffer.
        }
        else {
            MyFrameBuffer myFrameBuffer = boundFrameBuffers.peek();
            /*Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, myFrameBuffer.getFramebufferHandle());
            myFrameBuffer.setFrameBufferViewport();*/
            myFrameBuffer.begin(false);
        }

    }

    /**
     * Must be called when the game is destroyed.
     * Because static variables sometimes don't behave expectedly when the app gets destroyed.
     */
    public static void clearBoundFrameBuffers() {
        if (boundFrameBuffers != null)
            boundFrameBuffers.clear();
        Gdx.app.log(TAG, "boundFrameBuffers got cleared!!");
    }
}
