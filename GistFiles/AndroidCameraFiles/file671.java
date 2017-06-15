/**
 * Created by luisburgos on 8/18/16.
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {

    public View cirlceView;
    public TextView nameTextView;
    public TextView descriptionTextView;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        cirlceView = itemView.findViewById(R.id.cirlceView);
        nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
        descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
    }

}
