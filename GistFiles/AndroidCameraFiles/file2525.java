@Override protected void onViewBound(@NonNull View view) {
    super.onViewBound(view);

    TestDialog.reAttachListener(getActivity(), TestDialog.class, this);

    btnShowDialog.setOnClickListener(v -> {
      TestDialog gd = new TestDialogBuilder(R.dimen.dialog_add_inspection_item_height,
          R.dimen.dialog_add_inspection_item_width, R.string.camera, R.string.title).build();

      TestDialog.show(getActivity(), gd, this);
    }
}