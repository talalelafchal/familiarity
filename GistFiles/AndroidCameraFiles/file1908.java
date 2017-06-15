    private Point getScreenSize() {
        Point p = new Point(-1, -1);

        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);

        // since SDK_INT = 1;
        p.x = metrics.widthPixels;
        p.y = metrics.heightPixels;

        // includes window decorations (status bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                p.x = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                p.y = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        }

        // includes window decorations (status bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                p.x = realSize.x;
                p.y = realSize.y;
            } catch (Exception ignored) {
            }
        }

        return p;
    }