/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.nasa.worldwind.geom;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.draw.DrawContext;
import gov.nasa.worldwind.render.AbstractRenderable;
import gov.nasa.worldwind.draw.Drawable;
import gov.nasa.worldwind.render.Color;
import gov.nasa.worldwind.render.RenderContext;
import gov.nasa.worldwind.render.ShaderProgram;
import gov.nasa.worldwind.util.Tile;

/**
 * Created by zach on 10/18/16.
 */

public class BoundingBoxDiagnostic extends AbstractRenderable implements Drawable {

    private ShaderProgram program;

    private static BoundingBoxDiagnostic INSTANCE = null;

    private static final Random RAND = new Random();

    private final Map<Tile, TileMeta> tileCache = new ConcurrentHashMap<>();

    private final Queue<List<Tile>> readyDrawingList = new ConcurrentLinkedQueue<>();

    private List<Tile> queueList = new ArrayList<>();

    private List<Tile> activeDrawTiles = null;

    private final float[] mvp = new float[16];

    public static final String VERT =
        "precision mediump float;\n" +
        "\n" +
        "attribute vec3 pos;\n" +
        "uniform vec3 col;\n" +
        "uniform mat4 mvp;\n" +
        "varying vec3 colr;\n" +
        "\n" +
        "void main() {\n" +
        "   gl_Position = mvp * vec4(pos, 1.0);\n" +
        "   colr = col;\n" +
        "}";

    public static final String FRAG =
        "precision mediump float;\n" +
        "\n" +
        "varying vec3 colr;\n" +
        "\n" +
        "void main() {\n" +
        "   gl_FragColor = vec4(colr, 1.0);\n" +
        "}";

    public static BoundingBoxDiagnostic get() {
        if (INSTANCE == null) {
            INSTANCE = new BoundingBoxDiagnostic();
            INSTANCE.program = new ShaderProgram();
            INSTANCE.program.setProgramSources(VERT, FRAG);
        }
        return INSTANCE;
    }

    @Override
    public void recycle() {
        // don't care
    }

    @Override
    public void draw(DrawContext dc) {

        if (dc.pickMode) {
            return;
        }

        // Awkward way of getting the last (latest) on the queue..
        while(this.readyDrawingList.peek() != null) {
            this.activeDrawTiles = this.readyDrawingList.poll();
        }

        // There was nothing to draw
        if (this.activeDrawTiles == null) {
            return;
        }

        // Use the program
        this.program.useProgram(dc);

        int posAttrLoc = GLES20.glGetAttribLocation(this.program.getProgramId(), "pos");
        int colUniLoc = GLES20.glGetUniformLocation(this.program.getProgramId(), "col");
        int mvpUniLoc = GLES20.glGetUniformLocation(this.program.getProgramId(), "mvp");

        GLES20.glEnableVertexAttribArray(posAttrLoc);
        GLES20.glVertexAttribPointer(posAttrLoc, 3, GLES20.GL_FLOAT, false, 3 * 4, 0);

        // Sanity to check to make sure it should be in clip coordinates
        Vec3 what = new Vec3(0.0, 0.0, rad*1.2);
        what.multiplyByMatrix(dc.modelviewProjection);
        Log.d("gov.nasa.worldwind", "the transformed z axis: " + what.toString());

        dc.modelviewProjection.transposeToArray(this.mvp, 0);
        GLES20.glUniformMatrix4fv(mvpUniLoc, 1, false, this.mvp, 0);

        GLES20.glLineWidth(4.75f);

        for (Tile tile : this.activeDrawTiles) {
            TileMeta meta = this.tileCache.get(tile);
            if (meta == null) {
                // it shouldn't be null
                continue;
            }

            GLES20.glUniform3f(colUniLoc, meta.color.red, meta.color.green, meta.color.blue);

            if (meta.buffer == -1) {
                int[] a = new int[1];
                GLES20.glGenBuffers(a.length, a, 0);
                meta.buffer = a[0];
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, meta.buffer);
                GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, TileMeta.BYTE_SIZE, meta.vertices, GLES20.GL_STATIC_DRAW);
                meta.vertices = null; // copied data to video memory don't need it anymore
            } else {
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, meta.buffer);
            }

            GLES20.glDrawArrays(GLES20.GL_LINES, 0, TileMeta.ELEMENT_COUNT);

        }

        GLES20.glLineWidth(1f);

    }

    @Override
    protected void doRender(RenderContext rc) {

        this.readyDrawingList.add(this.queueList);

        this.queueList = new ArrayList<>();

        rc.offerShapeDrawable(this, 10000.0); // fake camera distance means we want it as the last object drawn, but does this get culled by the frustrum?

    }

    public void submitForDisplay(Tile tile, RenderContext rc) {

        TileMeta meta = this.tileCache.get(tile);
        if (meta == null) {
            meta = new TileMeta(tile, rc);
            this.tileCache.put(tile, meta);
        }

        this.queueList.add(tile);

    }
static double rad = -10.0;
    private static class TileMeta {

        private static final Vec3 SCRATCH = new Vec3();

        private final Tile tile;

        private final Color color;

        private int buffer = -1;

        private static final int ELEMENT_COUNT = 2;

        private static final int VERTEX_COUNT = ELEMENT_COUNT * 3;

        private static final int BYTE_SIZE = VERTEX_COUNT * 4;

        // 9 vertices * 3 coordinates per vertex * 4 bytes per float
        private FloatBuffer vertices = ByteBuffer.allocateDirect(BYTE_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();

        public TileMeta(Tile tile, RenderContext renderContext) {
            this.tile = tile;
            this.color = new Color(RAND.nextFloat(), RAND.nextFloat(), RAND.nextFloat(), 1f);
            this.buildBuffer(renderContext);
        }

        private void buildBuffer(RenderContext rc) {

//            // Bounding Box
//            BoundingBox bb = this.tile.getExtent(rc);
//            vertices.put((float) bb.center.x);
//            vertices.put((float) bb.center.y);
//            vertices.put((float) bb.center.z);
//            vertices.put((float) bb.topCenter.x);
//            vertices.put((float) bb.topCenter.y);
//            vertices.put((float) bb.topCenter.z);
//            vertices.put((float) bb.bottomCenter.x);
//            vertices.put((float) bb.bottomCenter.y);
//            vertices.put((float) bb.bottomCenter.z);
//            vertices.put((float) (bb.center.x - (0.5 * bb.r.x)));
//            vertices.put((float) (bb.center.y - (0.5 * bb.r.y)));
//            vertices.put((float) (bb.center.z - (0.5 * bb.r.z)));
//            vertices.put((float) (bb.center.x + (0.5 * bb.r.x)));
//            vertices.put((float) (bb.center.y + (0.5 * bb.r.y)));
//            vertices.put((float) (bb.center.z + (0.5 * bb.r.z)));
//
//            // Sector Outline
//            Sector s = this.tile.sector;
//            rc.geographicToCartesian(s.minLatitude(), s.maxLongitude(), 1e4, WorldWind.ABSOLUTE, SCRATCH);
//            vertices.put((float) SCRATCH.x);
//            vertices.put((float) SCRATCH.y);
//            vertices.put((float) SCRATCH.z);
//            rc.geographicToCartesian(s.maxLatitude(), s.maxLongitude(), 1e4, WorldWind.ABSOLUTE, SCRATCH);
//            vertices.put((float) SCRATCH.x);
//            vertices.put((float) SCRATCH.y);
//            vertices.put((float) SCRATCH.z);
//            rc.geographicToCartesian(s.maxLatitude(), s.minLongitude(), 1e4, WorldWind.ABSOLUTE, SCRATCH);
//            vertices.put((float) SCRATCH.x);
//            vertices.put((float) SCRATCH.y);
//            vertices.put((float) SCRATCH.z);
//            rc.geographicToCartesian(s.minLatitude(), s.minLongitude(), 1e4, WorldWind.ABSOLUTE, SCRATCH);
//            vertices.put((float) SCRATCH.x);
//            vertices.put((float) SCRATCH.y);
//            vertices.put((float) SCRATCH.z);

            // BBD Debug Values
            //vertices.put(new float[]{(float) (rc.globe.getPolarRadius()*1.5), 0f, 0f});
            //vertices.put(new float[]{0f, (float) (rc.globe.getPolarRadius()*1.5), 0f});
            if (rad<0.0) {
                rad = rc.globe.getEquatorialRadius();
            }
            vertices.put(new float[]{0f, 0f, 0f});
            vertices.put(new float[]{0f, 0f, (float) (rc.globe.getEquatorialRadius()*1.2)});

            // Complete
            vertices.rewind();
        }
    }

}
