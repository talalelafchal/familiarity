package com.example.summer.newapp;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Summer on 30.11.2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private int[] image;
    private Listener listener;

     private String[] recycler;




    public static interface Listener {
        public void onClick(int position);


        
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;


        public ViewHolder (CardView v) {
            super(v);
            cardView = v;

        }

    }

    public RecyclerAdapter(String[] recycler, String[] latitube, String[] longitube, int[] image){
        this.image = image;
        this.recycler = recycler;



    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.oblast_card,parent,false);
        return  new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        CardView cardView = holder.cardView;
        //добавить ресурсы фото
        ImageView imageView = (ImageView)cardView.findViewById(R.id.oblast_card);
       Drawable drawable = cardView.getResources().getDrawable(image[position]);
        imageView.setImageDrawable(drawable);
        imageView.setContentDescription(recycler[position]);

        //посмотреть в книге стр 635-650!
        TextView textView =(TextView)cardView.findViewById(R.id.name);
        textView.setText(recycler[position]);
        cardView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void  onClick (View v) {
                if (listener != null) {
                    listener.onClick(position);
                }

            }

              //  Intent intent = new Intent(container.getContex(), MoscowOblActivity.class);
                //intent.putExtra(MoscowOblActivity.EXTRA_MOSCOWNO, position);
                //container.getContext().startActivity(intent);


           
       });

}
    @Override
    public int getItemCount(){

        return recycler.length;
    }
}
