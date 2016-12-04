package com.kewldevs.sathish.faceless;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sathish on 12/4/16.
 */

public class ViewedCardsViewHolder extends RecyclerView.ViewHolder {

    ImageView USR_IMG;
    TextView USR_NAME;

    public ViewedCardsViewHolder(View itemView) {
        super(itemView);
        USR_IMG = (ImageView) itemView.findViewById(R.id.viewed_usr_img);
        USR_NAME = (TextView) itemView.findViewById(R.id.viewed_usr_name);
    }

}
