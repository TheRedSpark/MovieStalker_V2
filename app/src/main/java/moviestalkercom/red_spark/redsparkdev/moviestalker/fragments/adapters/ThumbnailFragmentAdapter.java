package moviestalkercom.red_spark.redsparkdev.moviestalker.fragments.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Collections;
import java.util.List;

import moviestalkercom.red_spark.redsparkdev.moviestalker.LogHelp;
import moviestalkercom.red_spark.redsparkdev.moviestalker.R;
import moviestalkercom.red_spark.redsparkdev.moviestalker.data.Constants;
import moviestalkercom.red_spark.redsparkdev.moviestalker.network.GlideApp;

import com.bumptech.glide.Glide;


/**
 * Created by Red_Spark on 08-Aug-17.
 */

public class ThumbnailFragmentAdapter extends RecyclerView.Adapter<ThumbnailFragmentAdapter.MyViewHolder>{

    private static final String TAG = ThumbnailFragmentAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private List<String> mThumbnails = Collections.emptyList();
    private OnClickListener mOnClickListener;


    public ThumbnailFragmentAdapter(Context context, List<String> thumbnails, OnClickListener onClickListener){
        inflater = LayoutInflater.from(context);
        mThumbnails = thumbnails;

        if (onClickListener instanceof OnClickListener) {
            mOnClickListener = onClickListener;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClickListener");
        }

    }
    public interface OnClickListener{
        void onItemClick(View view, int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //LogHelp.print(TAG, "onBindViewHolder for pos:" + position);
        String imageUrl =
              Constants.POSTER_BASE_URL + Constants.POSTER_SIZE.W95 + mThumbnails.get(position);
        GlideApp.with(holder.itemView.getContext()).load(imageUrl).placeholder(R.drawable.test).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        LogHelp.print(TAG, "mThumbnails was:"+mThumbnails.size());
        return mThumbnails.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnail;


        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.image_thumbnail);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onItemClick(v, getLayoutPosition());
        }
    }
    public void setData(List<String> thumbnails){
        this.mThumbnails = thumbnails;
        notifyDataSetChanged();
        LogHelp.print(TAG, "setData");
    }

}
