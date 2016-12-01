package com.kewldevs.sathish.faceless;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.kewldevs.sathish.faceless.R.id.UcardAvailable;
import static com.kewldevs.sathish.faceless.R.id.UcardDesc;
import static com.kewldevs.sathish.faceless.R.id.UcardExpiry;
import static com.kewldevs.sathish.faceless.R.id.UcardHideLayout;
import static com.kewldevs.sathish.faceless.R.id.UcardImg;
import static com.kewldevs.sathish.faceless.R.id.UcardName;
import static com.kewldevs.sathish.faceless.R.id.UcardPostedTime;

/**
 * Created by sathish on 10/18/16.
 */

public class UserFoodCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView NAME,DESC,AVAIL,EXP,POSTON,TIME_OF_COOK,TYPE;
    ImageView IMG;
    LinearLayout HIDDENLAYOUT;

    public UserFoodCardsViewHolder(View itemView) {
        super(itemView);
        IMG = (ImageView) itemView.findViewById(UcardImg);
        NAME = (TextView) itemView.findViewById(UcardName);
        DESC = (TextView) itemView.findViewById(UcardDesc);
        AVAIL = (TextView) itemView.findViewById(UcardAvailable);
        EXP = (TextView) itemView.findViewById(UcardExpiry);
        POSTON = (TextView) itemView.findViewById(UcardPostedTime);
        TIME_OF_COOK = (TextView) itemView.findViewById(R.id.UcardTimeOfCook);
        TYPE = (TextView) itemView.findViewById(R.id.UcardType);
        HIDDENLAYOUT = (LinearLayout) itemView.findViewById(UcardHideLayout);
        HIDDENLAYOUT.setVisibility(View.GONE);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(HIDDENLAYOUT.getVisibility() == View.VISIBLE) {
            HIDDENLAYOUT.setVisibility(View.GONE);
        }else HIDDENLAYOUT.setVisibility(View.VISIBLE);
    }
}
