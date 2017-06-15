
/**
 * 修改安卓项目中的gradle文件和gradle-wrapper.properties文件
 * <p>
 * 调用{@link #setupOneLevelProjects(String)}修改只有单层的项目文件夹，或者
 * {@link #setupTwoLevelProjects(String, String, String[])}修改两层目录的项目文件夹
 * <p>
 * 默认匹配的 module 为 app 或者 Application，可以在{@link #MATCH_MODULES}中继续添加
 * <p>
 * <p>
 * Tips：使用前请先复制一个旧项目试验一下
 */
@SuppressWarnings({"ConstantConditions", "WeakerAccess"})
public class ChangeGradleFiles {

    /**
     * gradle wrapper版本
     */
    private static final String NEW_GRADLE_WRAPPER_VERSION = "distributionUrl = https\\://services.gradle.org/distributions/gradle-3.3-all.zip";

    /**
     * -----💥重要内容💥----
     * module名，在{@link #modifyModuleGradle(File)}方法中遍历修改匹配上的 module
     * -----------------
     */
    private static final String[] MATCH_MODULES = {
            "app",
            "Application",
    };

    public static String[] sNewContent;
    public static String[] sMatchContent;

    // 添加要匹配的内容和用于替代的新内容，针对module下的 build.gradle
    {
        sMatchContent = new String[]{
                MATCH_GRADLE_PLUGIN_VERSION,
                MATCH_COMPILE_SDK_VERSION,
                MATCH_BUILD_TOOLS_VERSION,
                MATCH_TARGET_SDK_VERSION,
        };

        sNewContent = new String[]{
                NEW_GRADLE_PLUGIN_VERSION,
                NEW_COMPILE_SDK_VERSION,
                NEW_BUILD_TOOL_VERSION,
                NEW_TARGET_SDK_VERSION,
        };
    }

    /**
     * 需要用正则表达式匹配的内容
     */
    private static final String MATCH_GRADLE_PLUGIN_VERSION = "\\s+classpath 'com.android.tools.build:gradle.+";
    private static final String MATCH_COMPILE_SDK_VERSION = "\\s+compileSdkVersion.+";
    private static final String MATCH_BUILD_TOOLS_VERSION = "\\s+buildToolsVersion.+";
    private static final String MATCH_TARGET_SDK_VERSION = "\\s+targetSdkVersion.+";

    /**
     * 正则表达式匹配上后用于替换的内容，为简单起见，直接复制一整行的内容
     */
    private static final String NEW_GRADLE_PLUGIN_VERSION = "        classpath 'com.android.tools.build:gradle:2.3.2'";
    private static final String NEW_COMPILE_SDK_VERSION = "    compileSdkVersion 25";
    private static final String NEW_BUILD_TOOL_VERSION = "    buildToolsVersion \"25.0.3\"";
    private static final String NEW_TARGET_SDK_VERSION = "        targetSdkVersion 25";


    public static void main(String[] args) {

        ChangeGradleFiles change = new ChangeGradleFiles();

        String path = "D:\\WorkSpace\\AndroidProject\\Demo";
        change.setupOneLevelProjects(path);
        //change.setupTwoLevelProjects(path, null, null);

        /*
        path = "D:\\WorkSpace\\AndroidProject\\FirstLineOfCodes";
        String filterRule = "\\d+_.+";
        String[] skipDirs = {"14_.+"};
        change.setupTwoLevelProjects(path, filterRule, skipDirs);
        */
    }

    /**
     * 修改只有一层的文件夹，即路径所在的目录下就是所有的Android项目
     */
    public void setupOneLevelProjects(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) setup(dir);
    }

    /**
     * 修改有两层的项目，即路径下第一层还是目录，第二层才是Android项目
     * 对于路径下的目录可以添加过滤规则，前提是你的目录的命名是有规则的，比如都以数字开头
     * 另外还可以利用第三个参数跳过某些文件夹，注意也是针对第一层的目录的
     *
     * @param path       the directory of projects
     * @param filterRule add filter rule to your first level directory, support regex
     * @param skipDirs   add directories you need to skip, support regex
     */
    public void setupTwoLevelProjects(String path, String filterRule, String[] skipDirs) {
        for (File projectParentDir : new File(path).listFiles()) {
            String rule = filterRule == null ? ".+" : filterRule;
            if (projectParentDir.isDirectory() && projectParentDir.getName().matches(rule)) {
                if (skipDirs == null || skipDirs.length == 0) {
                    setup(projectParentDir);
                } else {
                    for (String projectName : skipDirs) {
                        if (projectName != null && projectParentDir.getName().matches(projectName))
                            break;
                        else
                            setup(projectParentDir);
                    }
                }
            }
        }
    }

    /**
     * 遍历当前目录下所有安卓项目（默认所有文件夹都是安卓项目）并修改其中的文件
     *
     * @param projectParentDir The parent directory of your android projects
     */
    protected void setup(File projectParentDir) {
        for (File projectDir : projectParentDir.listFiles()) {
            if (projectDir.isDirectory()) {
                for (File projectFile : projectDir.listFiles()) {
                    modifyWrapper(projectFile);
                    modifyProjectGradle(projectFile);
                    modifyModuleGradle(projectFile);
                }
            }
        }
    }

    /**
     * 修改gradle文件夹下的gradle-wrapper.properties
     */
    private void modifyWrapper(File gradleDir) {
        if (gradleDir.getName().matches("gradle")) {
            File gradleWrapper = new File(
                    gradleDir.getAbsolutePath() + "\\wrapper\\gradle-wrapper.properties");
            Map<String, String> map = new TreeMap<>();
            map.put("distributionUrl.+", NEW_GRADLE_WRAPPER_VERSION);

            changeMatchedContent(gradleWrapper, map);
        }
    }

    /**
     * 修改项目根目录下的 gradle.build 文件
     */
    private void modifyProjectGradle(File projectGradle) {
        if (projectGradle.getName().matches("build\\.gradle")) {
            Map<String, String> map = new TreeMap<>();
            map.put(MATCH_GRADLE_PLUGIN_VERSION, NEW_GRADLE_PLUGIN_VERSION);

            changeMatchedContent(projectGradle, map);
        }
    }

    /**
     * 修改module下的gradle.build文件
     */
    private void modifyModuleGradle(File moduleDir) {
        String childDir = moduleDir.getName();

        Map<String, String> matchReplaceMap = new TreeMap<>();
        for (int i = 0; i < sMatchContent.length; i++) {
            matchReplaceMap.put(sMatchContent[i], sNewContent[i]);
        }

        // 从github上面下载下来的一些开源库的 module名大多数格式为：项目名 + "-" + "master"
        int hyphenIndex = childDir.indexOf("-");
        if (hyphenIndex != -1) {
            String prefix = childDir.substring(0, hyphenIndex);
            if (childDir.matches(prefix + ".*")) {
                File moduleGradle = new File(moduleDir.getAbsolutePath() + "\\build.gradle");
                if (moduleDir.isDirectory()) {
                    changeMatchedContent(moduleGradle, matchReplaceMap);
                }
            }
        }

        // 遍历文件夹名，如果 module 存在则修改此 build.gradle 中的内容
        for (String matchModule : MATCH_MODULES) {
            if (childDir.matches(matchModule)) {
                File moduleGradle = new File(moduleDir.getAbsolutePath() + "\\build.gradle");
                if (moduleDir.isDirectory()) {
                    changeMatchedContent(moduleGradle, matchReplaceMap);
                }
            }
        }
    }


    /**
     * @param file            The file you need to change. Key is for the content you need to match,
     *                        the value is for the new content to replace the matched content.
     * @param matchReplaceMap the map contains regular expression as key and new content as values
     */
    private void changeMatchedContent(File file, Map<String, String> matchReplaceMap) {
        BufferedReader gradleFile = null;
        StringBuilder result = new StringBuilder();

        int replacedNum = 0;

        try {
            gradleFile = new BufferedReader(new FileReader(file));
            String[] regexs = convertToArray(
                    matchReplaceMap.keySet().iterator(),
                    matchReplaceMap.keySet().size()
            );
            String[] replaces = convertToArray(
                    matchReplaceMap.values().iterator(),
                    matchReplaceMap.values().size()
            );

            boolean changed = false;
            String line;
            while ((line = gradleFile.readLine()) != null) {
                for (int i = 0; i < regexs.length; i++) {
                    if (line.matches(regexs[i])) {
                        changed = true;
                        result.append(replaces[i]).append(System.getProperty("line.separator"));
                        replacedNum++;
                        break;
                    }
                }
                if (!changed) result.append(line).append(System.getProperty("line.separator"));
                changed = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(gradleFile);
        }

        System.out.println("Replaced " + replacedNum + " line(s) in the " + file.getAbsolutePath());
        writeIntoFile(file, result.toString());
    }

    private String[] convertToArray(Iterator<String> itr, int size) {
        String[] result = new String[size];
        for (int i = 0; itr.hasNext(); i++) {
            result[i] = itr.next();
        }
        return result;
    }

    private void writeIntoFile(File file, String content) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.close(bufferedWriter);
        }
    }

}