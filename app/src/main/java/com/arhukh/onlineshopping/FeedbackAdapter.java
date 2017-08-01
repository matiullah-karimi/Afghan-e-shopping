package com.arhukh.onlineshopping;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolder> {
    private List<Product> mData;
    private LayoutInflater mInflator;
    Context context;

    public FeedbackAdapter(Context context, List<Product> data){
        this.mData = data;
        this.mInflator = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_feedbacks, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Product currentObject = mData.get(position);
        holder.setData(currentObject, position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView date, title, desc;
        RatingBar ratingBar;
        int position;
        Product current;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.uName);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            date = (TextView) itemView.findViewById(R.id.date);
            title = (TextView) itemView.findViewById(R.id.fTitle);
            desc = (TextView) itemView.findViewById(R.id.fDesc);
        }

        public void setData(Product current, int position) {
            this.name.setText( current.getImage());
            this.ratingBar.setRating(Float.parseFloat(current.getPrice()));
            this.date.setText(current.getId());
            this.title.setText(current.getName());
            this.desc.setText(current.getDescription());
            this.position = position;
            this.current = current;
        }
    }
}
