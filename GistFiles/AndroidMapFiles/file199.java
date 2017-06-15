import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Move and rename asset files generated from Sketch into Android supported
 * structure.
 * 
 * @author shalom
 */
public class MoveAndRename {

    private Map<String, String> targets;

    public MoveAndRename() {
        targets = new HashMap<String, String>();
        targets.put("", "drawable-mdpi");
        targets.put("@1.5x", "drawable-hdpi");
        targets.put("@2x", "drawable-xhdpi");
        targets.put("@3x", "drawable-xxhdpi");
    }

    public void execute(String path) {
        File directory = new File(path);
        File[] assets = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getPath().endsWith("png");
            }
        });
        // Create asset dirs
        for (String target : targets.values()) {
            new File(directory, target).mkdir();
        }

        for (File asset : assets) {
            boolean moved = false;
            try {
                String assetName = asset.getName();
                Path sourcePath = Paths.get(asset.toURI());
                for (String targetIdentifier : targets.keySet()) {
                    if (!targetIdentifier.isEmpty() && assetName.indexOf(targetIdentifier) > -1) {
                        String name = cleanupName(asset.getName(), targetIdentifier);
                        Path targetPath = sourcePath.resolveSibling(targets.get(targetIdentifier))
                                .resolve(name);
                        Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
                        moved = true;
                    }
                }
                if (!moved) {
                    // Move to the mdpi dir
                    String name = cleanupName(asset.getName(), "");
                    Path targetPath = sourcePath.resolveSibling(targets.get("")).resolve(name);
                    Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Cleanup the name by replacing all non-letters & numbers with underscores,
     * and lower-casing the result.
     * 
     * @param name
     * @param targetIdentifier The target identifier (e.g. @1.5x) that will be
     *            deleted from the name.
     * @return A clean name
     */
    private String cleanupName(String name, String targetIdentifier) {
        name = name.replaceFirst(targetIdentifier, "");
        int suffixDotIndex = name.lastIndexOf(".");
        name = name.substring(0, suffixDotIndex).replaceAll("\\W+", "_")
                + name.substring(suffixDotIndex);
        name = name.replaceFirst("_\\.", ".");
        name = name.toLowerCase();
        return name;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            new MoveAndRename().execute(args[0]);
        } else {
            System.err.println("Usage: MoveAndRename <assets-path>");
        }
    }
}