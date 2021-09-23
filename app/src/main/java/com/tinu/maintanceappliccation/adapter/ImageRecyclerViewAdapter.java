package com.tinu.maintanceappliccation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tinu.maintanceappliccation.ApplicationClass;
import com.tinu.maintanceappliccation.R;
import com.tinu.maintanceappliccation.activity.ImageUploadActivity;
import com.tinu.maintanceappliccation.models.ImageModel;
import com.tinu.maintanceappliccation.utility.DialogManager;


import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ListViewHolder> {

    private Context context;
    private ImageRecyclerViewAdapter.CustomOnClickListener customOnClickListener;
    private LayoutInflater inflater = null;
    List<ImageModel>imagesList;

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_photos,iv_close;

        public ListViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            iv_photos =(ImageView) itemView.findViewById(R.id.iv_photos);
            iv_close =(ImageView) itemView.findViewById(R.id.iv_close);

        }

        @Override
        public void onClick(View v) {

        }

    }

    public ImageRecyclerViewAdapter(Context context, List<ImageModel>imagesList, ImageRecyclerViewAdapter.CustomOnClickListener customOnClickListener) {

        this.context = context;
        this.imagesList = imagesList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.customOnClickListener=customOnClickListener;


    }

    @Override
    public ImageRecyclerViewAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos, parent, false);
        ImageRecyclerViewAdapter.ListViewHolder lrcv = new ImageRecyclerViewAdapter.ListViewHolder(layoutView);
        return lrcv;
    }

    @Override
    public void onBindViewHolder(final ImageRecyclerViewAdapter.ListViewHolder holder, final int position) {

        if(imagesList.get(position)!=null ){

            Glide.with(ApplicationClass.getCurrentContext())

                    .load(imagesList.get(position).getPath())
                    .thumbnail(0.1f)
                    .into( holder.iv_photos);

        }



        holder.iv_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //customOnClickListener .OrderDetails(imagesList.get(position),position);

            }
        });

        holder.iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.showConfirmDialogue(ApplicationClass.getCurrentActivity(), ApplicationClass.getCurrentContext().getResources().getString(R.string.delete), new DialogManager.IMultiActionDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        if (imagesList.get(position).isServer())
                        {
                            customOnClickListener.OrderDetails(imagesList.get(position).getId(),imagesList.get(position));

                            imagesList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, imagesList.size());
                        }
                        else {
                            customOnClickListener.OrderDetails(imagesList.get(position).getId(),imagesList.get(position));

                            imagesList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, imagesList.size());
                        }
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });


            }
        });


    }


    @Override
    public int getItemCount() {
        if (imagesList!=null)
        {
            if (imagesList.size()>0)
            {
                return imagesList.size();
            }
            else
            {
                return 0;
            }
        }
        return 0;
    }



    public interface CustomOnClickListener {

        void OrderDetails(int   imageid,ImageModel imageModel);


    }


}
