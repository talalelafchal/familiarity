public class DebtorsListAdapter extends ArrayAdapter<Debtor> {

    private Context context;

    public DebtorsListAdapter(Context context, int resource, List<Debtor> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    private class ViewHolder{
        TextView name;
        TextView amount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Debtor d = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.debtor_listitem_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.amount = (TextView) convertView.findViewById(R.id.amountTextView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.name.setText(d.getName());
        holder.amount.setText(d.getAmount().toString());
        
        return convertView;
    }
}