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


    /*
    OnItemClickListenerRecycler clickListener;
    View view;
    Context context;


    @Override
    public FoodCardsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foods_card_view,parent,false);

        return new FoodCardsViewHolder(view);
    }

    public void setClickListener(OnItemClickListenerRecycler clickListener){
        this.clickListener = clickListener;
    }




    @Override
    public void onBindViewHolder(final FoodCardsViewHolder holder, final int position) {


    }

    @Override
    protected void populateViewHolder(final FoodCardsViewHolder holder, final Food cards, final int position) {
        holder.NAME.setText(cards.getFood_name());
        holder.DESC.setText(cards.getFood_desc());
        holder.IMG.setImageResource(R.mipmap.ic_launcher);
        holder.AVAIL.setText(cards.getFood_avail_for()+" people(s)");
        holder.EXP.setText("Expire by: "+context.getResources().getStringArray(R.array.expire_time_array)[Integer.parseInt(cards.getFood_expiry())]);
        holder.POSTON.setText(cards.getFood_post_on());

        holder.VIEWBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTON VIEW EVENT","Position:"+position);
                holder.HIDDENLAYOUT.setVisibility(View.GONE);
                context.startActivity(new Intent(context,FoodViewActivity.class).putExtra("FOOD_BUNDLE",cards);

            }
        });

        holder.DELETEBUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTON DELETE EVENT","Position:"+position);

                Snackbar.make(v,cards.getFood_name()+" deleted!!",Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                holder.HIDDENLAYOUT.setVisibility(View.GONE);
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return foodArrayList.size();
    }


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

          /*  if(clickListener!=null){
                clickListener.onClick(v,getAdapterPosition());
            }*/
            if(HIDDENLAYOUT.getVisibility() == View.VISIBLE) {
                HIDDENLAYOUT.setVisibility(View.GONE);
            }else HIDDENLAYOUT.setVisibility(View.VISIBLE);
        }

    }
