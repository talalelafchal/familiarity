public class SnackManager {
    private Snackbar current;
    private Queue<Snackbar> snacks = new LinkedList<>();

    public void add(Snackbar bar) {
        bar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (snacks.size() > 0) {
                    show();
                }
            }
        });
        snacks.offer(bar);
    }

    public void show() {
        if (snacks.isEmpty()) return;
        current = snacks.poll();
        current.show();
    }

    public void dismiss() {
        if (current != null && current.isShown())
            current.dismiss();
    }
}
