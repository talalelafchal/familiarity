@FragmentWithArgs public abstract class GenericDialog<DataType> extends DialogFragment {

  @Arg @StringRes public int strTitle;
  @Arg(required = false) public @StringRes int strBtnPositive;
  @Arg(required = false) public @StringRes int strBtnNegative;

  @Arg @DimenRes public int dimenWidth;
  @Arg @DimenRes public int dimenHeight;

  @BindView(R.id.txt_title_dialog_generic) TextView tvTitle;
  @BindView(R.id.btn_dialog_generic_positive) Button btnPositive;
  @BindView(R.id.btn_dialog_generic_negative) Button btnNegative;
  @BindView(R.id.layout_linear_dialog_generic) LinearLayout linearLayout;

  protected int[] layouts;

  protected OnPositiveClickListener onClickListener;

  public interface OnPositiveClickListener {
    void onPositiveClick(Object data);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FragmentArgs.inject(this);
  }

  @Override public void onStart() {
    super.onStart();
    getDialog().getWindow()
        .setLayout(getResources().getDimensionPixelSize(dimenWidth),
            getResources().getDimensionPixelSize(dimenHeight));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_generic, container);
    if (layouts.length > 0) {
      addViews(getActivity(), rootView, layouts);
    }
    return rootView;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    tvTitle.setText(strTitle);
    btnPositive.setText(strBtnPositive != 0 ? strBtnPositive : R.string.ok);
    btnNegative.setText(strBtnNegative != 0 ? strBtnNegative : R.string.cancel);
  }

  @OnClick(R.id.btn_dialog_generic_positive) public abstract void onPositiveClick();

  @OnClick(R.id.btn_dialog_generic_negative) void onNegativeClick() {
    dismiss();
  }

  private void setOnClickListener(@NonNull OnPositiveClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  protected void onPositiveClickImp(DataType data) {
    if (data != null && onClickListener != null) {
      onClickListener.onPositiveClick(data);
    }
  }

  @Override public void onDetach() {
    onClickListener = null;
    super.onDetach();
  }

  public static void addViews(Activity activity, View rootView, int[] layouts) {
    LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.layout_linear_dialog_generic);
    for (int i = 0; i < layouts.length; i++) {
      LayoutInflater.from(activity).inflate(layouts[0], ll, true);
    }
  }

  public static void show(Activity activity, GenericDialog genericDialog,
      OnPositiveClickListener onPositiveClickListener) {
    genericDialog.setOnClickListener(onPositiveClickListener);
    genericDialog.show(activity.getFragmentManager(), genericDialog.getClass().getSimpleName());
  }

  public static void reAttachListener(@NonNull Activity activity, Class fragment,
      @NonNull OnPositiveClickListener onPositiveClickListener) {
    GenericDialog gd =
        (GenericDialog) activity.getFragmentManager().findFragmentByTag(fragment.getSimpleName());
    if (gd != null) gd.setOnClickListener(onPositiveClickListener);
  }
}