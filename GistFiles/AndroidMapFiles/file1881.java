@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail, container, false);
    aQuery = new AQuery(getActivity(), view);
    aQuery.id(R.id.fragment_detail_text).text("Fragment内でも使える");
    return view;
}