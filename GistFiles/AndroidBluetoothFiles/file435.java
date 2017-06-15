public class WeatherAdapter extends ArrayAdapter<Weather>{

    Context context; 
    int layoutResourceId;    
    Weather data[] = null;
    
    //Custom constructor same as the ArrayAdapter constructor
    //Except as data we will take Weather objects that will include all the resources we need.
    public WeatherAdapter(Context context, int layoutResourceId, Weather[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    
    //The actual implementation elements of the row
    static class WeatherHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }

    //This method will be called for every item in the ListView to create views with their properties set as we want.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder = null;
        
        if(row == null)
        {
            //Get the resource from the resource file
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            //Get the actual elements and assign them to our holder class
            holder = new WeatherHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            
            //Set the tag associated with this view/row the holder object.
            row.setTag(holder);
        }
        else
        {
            holder = (WeatherHolder)row.getTag();
        }
        
        Weather weather = data[position];
        
        holder.txtTitle.setText(weather.title);
        holder.imgIcon.setImageResource(weather.icon);
        
        return row;
    }
}