public class ProductAdapter extends ArrayAdapter<Product> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context ctx) {
        super(ctx, R.layout.row_layout, productList);

        this.productList = productList;
        this.context = ctx;
    }

    public int getCount() {
        if (productList != null)
            return productList.size();
        return 0;
    }

    public Product getItem(int position) {
        if (productList != null)
            return productList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (productList != null)
            return productList.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // row_layout.xml file for list items
            convertView = inflater.inflate(R.layout.row_layout, parent, false);
        }

        Product product = productList.get(position);

        TextView textId = (TextView) convertView.findViewById(R.id.id);
        textId.setText(product.getId());

        TextView textTitle = (TextView) convertView.findViewById(R.id.title);
        textTitle.setText(product.getTitle());

        return convertView;
    }
}
