public static void sendViewToBack(final View child) {
  final ViewGroup parent = (ViewGroup)child.getParent();
  if (null != parent) {
    parent.removeView(child);
    parent.addView(child, 0);
  }
}