class AndroidLanguageSourceSet extends BaseLanguageSourceSet {
}

class Rules {

    @Defaults
    public static void addDefaultAndroidSourceSet(@Path("android.sources") ModelMap<FunctionalSourceSet> sources) {

        sources.all(new Action<FunctionalSourceSet>() {
            @Override
            public void execute(FunctionalSourceSet functionalSourceSet) {
                functionalSourceSet.create(
                        "resources", AndroidLanguageSourceSet.class, new Action<AndroidLanguageSourceSet>() {
                    @Override
                    public void execute(AndroidLanguageSourceSet sourceSet) {
                        ComponentSpecIdentifier componentId = sourceSet.getIdentifier();
                        String parentName = componentId.getParent().getName();
                        String name = componentId.getName();
                        String srcDirPath = String.format("src/%s/%s", parentName, name);
                        sourceSet.getSource().srcDir(srcDirPath);
                    }
                });
            }
        });
    }
}
