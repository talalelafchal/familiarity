public class Triangle extends Model3D {

    private FloatBuffer vertexBuffer;
    
    static final int COORDS_PER_VERTEX = 3;
    
    static float triangleCoords[] = {   // in counterclockwise order:
            0.0f,  1.0f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };
    static final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

    private ShortBuffer indexBuffer;

    public Triangle() {
        super();
        init();
    }

    private void init() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        indexBuffer = ShortBuffer.allocate(vertexCount);
        indexBuffer.put((short)0);
        indexBuffer.put((short)1);
        indexBuffer.put((short)2);

        indexBuffer.rewind();
    }

    @Override
    protected FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }
    
    @Override
    protected ShortBuffer getIndexBuffer() {
        return indexBuffer;
    }
}