import android.media.MediaPlayer;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;

public class VRSphere extends Sphere {

    private StreamingTexture streamingTexture;

    public VRSphere() {
        super(100, 128, 64);

        setRotation(Vector3.Axis.Y, -90);
        setScale(1, 1, -1);
        setColor(0);
        setMaterial(new Material());
    }

    public void bindMediaPlayer(MediaPlayer mp) throws ATexture.TextureException {

        if (streamingTexture != null) {
            streamingTexture.updateMediaPlayer(mp);
        } else {
            streamingTexture = new StreamingTexture("video", mp);
            getMaterial().addTexture(streamingTexture);
        }
    }

    @Override
    public void render(Camera camera, Matrix4 vpMatrix, Matrix4 projMatrix, Matrix4 vMatrix, Matrix4 parentMatrix, Material sceneMaterial) {
        if (streamingTexture != null) {
            streamingTexture.update();
        }
        super.render(camera, vpMatrix, projMatrix, vMatrix, parentMatrix, sceneMaterial);
    }
}
