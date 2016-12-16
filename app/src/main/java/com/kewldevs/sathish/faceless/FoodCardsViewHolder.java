package com.kewldevs.sathish.faceless;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.kewldevs.sathish.faceless.R.id.cardAvailable;
import static com.kewldevs.sathish.faceless.R.id.cardDeleteButton;
import static com.kewldevs.sathish.faceless.R.id.cardDesc;
import static com.kewldevs.sathish.faceless.R.id.cardExpiry;
import static com.kewldevs.sathish.faceless.R.id.cardHideLayout;
import static com.kewldevs.sathish.faceless.R.id.cardImg;
import static com.kewldevs.sathish.faceless.R.id.cardName;
import static com.kewldevs.sathish.faceless.R.id.cardPostedTime;
import static com.kewldevs.sathish.faceless.R.id.cardViewButton;

/**
 *
 * Created by sathish on 9/30/16.
 *
 */


    /**
     * VIEW HOLDER
     */

    public class FoodCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView NAME,DESC,AVAIL,EXP,POSTON;
        ImageView IMG;
        LinearLayout HIDDENLAYOUT;
        Button VIEWBUTTON,DELETEBUTTON;


        public FoodCardsViewHolder(View itemView) {
            super(itemView);
            IMG = (ImageView) itemView.findViewById(cardImg);
            NAME = (TextView) itemView.findViewById(cardName);
            DESC = (TextView) itemView.findViewById(cardDesc);
            AVAIL = (TextView) itemView.findViewById(cardAvailable);
            EXP = (TextView) itemView.findViewById(cardExpiry);
            POSTON = (TextView) itemView.findViewById(cardPostedTime);
            VIEWBUTTON = (Button) itemView.findViewById(cardViewButton);
            DELETEBUTTON = (Button) itemView.findViewById(cardDeleteButton);
            HIDDENLAYOUT = (LinearLayout) itemView.findViewById(cardHideLayout);
            HIDDENLAYOUT.setVisibility(View.GONE);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            if(HIDDENLAYOUT.getVisibility() == View.VISIBLE) {
                HIDDENLAYOUT.setVisibility(View.GONE);
            }else HIDDENLAYOUT.setVisibility(View.VISIBLE);

        }

    }
