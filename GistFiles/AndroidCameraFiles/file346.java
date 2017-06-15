@FragmentWithArgs public class TestDialog extends GenericDialog<Integer> {

  @Arg @StringRes public int strText1;
  @BindView(android.R.id.text1) TextView text1;
  
  int i = 0;

  {
    layouts = new int[] { android.R.layout.simple_list_item_1 };
  }

  @Override public void onPositiveClick() {
    onPositiveClickImp(i++);
    if(i == 2) dismiss(); // validation
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    text1.setText(strText1 != 0 ? strText1 : R.string.ok);
  }
}