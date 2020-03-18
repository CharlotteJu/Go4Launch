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
import com.google.gson.internal.$Gson$Preconditions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListRestaurantsAdapter extends RecyclerView.Adapter<ListRestaurantsAdapter.ListRestaurantsViewHolder>
{


    private List<Restaurant> restaurants;
    private RequestManager glide;

    public ListRestaurantsAdapter(List<Restaurant> restaurants, RequestManager glide)
    {
        this.restaurants = restaurants;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ListRestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.item_list_restaurants, parent, false);
        return new ListRestaurantsViewHolder(v);
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

    static class ListRestaurantsViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.item_list_restaurant_name_txt)
        TextView name;
        @BindView(R.id.item_list_restaurant_type_txt)
        TextView type;
        @BindView(R.id.item_list_restaurant_adress_txt)
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

        public ListRestaurantsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            star1.setVisibility(View.INVISIBLE);
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
        }

        private void updateUI(Restaurant restaurant, RequestManager glide)
        {
            name.setText(restaurant.getName());
            type.setText(restaurant.getType() + " - ");
            address.setText(restaurant.getAddress());
            glide.load(restaurant.getIllustration()).apply(RequestOptions.centerCropTransform()).into(illustration);
            this.updateRating(restaurant);

            List<DetailPOJO.Period> periodList = restaurant.getOpeningHours().getPeriods();
            hours(periodList);


            //TODO : Distance, heure de fermeture
        }

        private void hours (List<DetailPOJO.Period> periods)
        {
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -1 ;

            String hourClose = periods.get(day).getClose().getTime();
            int closeInt = Integer.parseInt(hourClose);

            String time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "" + Calendar.getInstance().get(Calendar.MINUTE);
            int timeInt = Integer.parseInt(time);

            hours.setText("Open until " + closeInt);

            /*if (closeInt - timeInt < 60)
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
            if (restaurant.getNumberRating() != 0)
            {
                numberRating.setText(restaurant.getNumberRating());

                switch (restaurant.getRating())
                {
                    case 1 :
                        star1.setVisibility(View.VISIBLE);
                        break;
                    case 2 :
                        star1.setVisibility(View.VISIBLE);
                        star2.setVisibility(View.VISIBLE);
                        break;
                    case 3 :
                        star1.setVisibility(View.VISIBLE);
                        star2.setVisibility(View.VISIBLE);
                        star3.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}




