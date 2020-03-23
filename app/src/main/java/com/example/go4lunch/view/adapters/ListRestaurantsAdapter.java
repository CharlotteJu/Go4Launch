package com.example.go4lunch.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.DetailPOJO;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.view.fragments.OnClickListener;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListRestaurantsAdapter extends RecyclerView.Adapter<ListRestaurantsAdapter.ListRestaurantsViewHolder>
{

    private OnClickListener onClickListener;
    private List<Restaurant> restaurants;
    private RequestManager glide;

    public ListRestaurantsAdapter(List<Restaurant> restaurants, RequestManager glide, OnClickListener onClickListener)
    {
        this.restaurants = restaurants;
        this.glide = glide;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ListRestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.item_list_restaurants, parent, false);


        return new ListRestaurantsViewHolder(v, this.onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRestaurantsViewHolder holder, int position)
    {
        holder.updateUI(this.restaurants.get(position), glide);
    }


    @Override
    public int getItemCount() {
        return this.restaurants.size();
    }

    static class ListRestaurantsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.item_list_restaurant_name_txt)
        TextView name;
        @BindView(R.id.item_list_restaurant_address_txt)
        TextView address;
        @BindView(R.id.item_list_restaurant_hours_txt)
        TextView hours;
        @BindView(R.id.item_list_restaurant_distance_txt)
        TextView distance;
        @BindView(R.id.item_list_restaurant_number_rating_txt)
        TextView numberRating;
        @BindView(R.id.item_list_restaurant_people_rating_image)
        ImageView peopleRating;
        @BindView(R.id.item_list_restaurant_star_1_image)
        ImageView star1;
        @BindView(R.id.item_list_restaurant_star_2_image)
        ImageView star2;
        @BindView(R.id.item_list_restaurant_star_3_image)
        ImageView star3;
        @BindView(R.id.item_list_restaurant_illustration_image)
        ImageView illustration;

        OnClickListener onClickListener;


        public ListRestaurantsViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.onClickListener = onClickListener;
        }


        private void updateUI(Restaurant restaurant, RequestManager glide)
        {
            name.setText(restaurant.getName());
            address.setText(restaurant.getAddress());
            glide.load(restaurant.getIllustration()).apply(RequestOptions.centerCropTransform()).into(illustration);
            this.updateRating(restaurant);
            this.updateHours(restaurant);

            /*if (restaurant.getOpeningHours() != null)
            {
                List<DetailPOJO.Period> periodList = restaurant.getOpeningHours().getPeriods();
                updateHours(periodList);
            }*/

            //TODO : Distance
        }

        private void updateHours(Restaurant restaurant)
        {
           if (restaurant.getOpenNow())
           {
               hours.setText("Open now");
           }
           else
           {
               hours.setText("Close now");
           }


            /*int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -1 ;

            String hourClose = periods.get(day).getClose().getTime();
            int closeInt = Integer.parseInt(hourClose);

            String time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "" + Calendar.getInstance().get(Calendar.MINUTE);
            int timeInt = Integer.parseInt(time);

            if (closeInt - timeInt < 60)
            {
                hours.setText("Closing soon");
            }
            else
            {
                hours.setText("Open until " + closeInt);
            }*/

        }

        private void updateRating(Restaurant restaurant)
        {
            double rating = restaurant.getRating();

            if (rating > 3.75)
            {
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);

            }
            else if (rating > 2.5)
            {
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.INVISIBLE);

            }
            else if (rating > 1.25)
            {
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.INVISIBLE);
                star3.setVisibility(View.INVISIBLE);
            }
            else
            {
                star1.setVisibility(View.INVISIBLE);
                star2.setVisibility(View.INVISIBLE);
                star3.setVisibility(View.INVISIBLE);
            }

        }


        @OnClick(R.id.item_list_restaurant_illustration_image)
        void test(View v)
        {
            onClickListener.onClickListener(getAdapterPosition());
        }
        @Override
        public void onClick(View view)
        {
            onClickListener.onClickListener(getAdapterPosition());
        }
    }
}




