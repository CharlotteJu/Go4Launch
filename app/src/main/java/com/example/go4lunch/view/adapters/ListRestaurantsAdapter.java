package com.example.go4lunch.view.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
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
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.utils.UtilsListRestaurant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListRestaurantsAdapter extends RecyclerView.Adapter<ListRestaurantsAdapter.ListRestaurantsViewHolder>
{
    // FOR DATA
    private OnClickListenerItemList onClickListenerItemList;
    private List<Restaurant> restaurantsFromPlaces;
    private RequestManager glide;
    private Activity activity;

    public ListRestaurantsAdapter(RequestManager glide, OnClickListenerItemList onClickListenerItemList, Activity activity)
    {
        this.glide = glide;
        this.onClickListenerItemList = onClickListenerItemList;
        this.activity = activity;
        this.restaurantsFromPlaces = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListRestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.item_list_restaurants, parent, false);
        return new ListRestaurantsViewHolder(v, this.onClickListenerItemList, this.activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRestaurantsViewHolder holder, int position)
    {
        holder.updateUI(this.restaurantsFromPlaces.get(position), glide);
    }


    @Override
    public int getItemCount() {
        return this.restaurantsFromPlaces.size();
    }

    public void updateList(List<Restaurant> restaurantList)
    {
        this.restaurantsFromPlaces = restaurantList;
        this.notifyDataSetChanged();
    }

    static class ListRestaurantsViewHolder extends RecyclerView.ViewHolder
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
        TextView numberWorkmatesTxt;
        @BindView(R.id.item_list_restaurant_people_rating_image)
        ImageView peopleWorkmatesImage;
        @BindView(R.id.item_list_restaurant_star_1_image)
        ImageView star1;
        @BindView(R.id.item_list_restaurant_star_2_image)
        ImageView star2;
        @BindView(R.id.item_list_restaurant_star_3_image)
        ImageView star3;
        @BindView(R.id.item_list_restaurant_illustration_image)
        ImageView illustration;

        private OnClickListenerItemList onClickListenerItemList;
        private Activity activity;
        private int numberWorkmates = 0;

        private ListRestaurantsViewHolder(@NonNull View itemView, OnClickListenerItemList onClickListenerItemList, Activity activity) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.onClickListenerItemList = onClickListenerItemList;
            this.activity = activity;
        }

        private void updateUI(Restaurant restaurant, RequestManager glide)
        {
            name.setText(restaurant.getName());
            address.setText(restaurant.getAddress());
            glide.load(restaurant.getIllustration()).apply(RequestOptions.centerCropTransform()).into(illustration);
            this.displayWorkmates();
            this.updateHours(restaurant);
            this.updateNumberWorkmates(restaurant);
            this.updateDistance(restaurant);
            UtilsListRestaurant.updateRating(star1, star2, star3, restaurant);
        }

        private void updateDistance(Restaurant restaurant)
        {
            String distanceString = restaurant.getDistanceCurrentUser() + "m";
            this.distance.setText(distanceString);
        }

        /**
         * Update hours with restaurant's boolean getOpenNow()
         */
        private void updateHours(Restaurant restaurant)
        {
           if (restaurant.getOpenNow())
           {
               hours.setText(activity.getResources().getString(R.string.list_restaurants_adapter_open_now));
               if (Build.VERSION.SDK_INT < 23) {
                   hours.setTextAppearance(activity.getApplicationContext(), R.style.item_list_restaurant_hours_open_txt);
               } else {
                   hours.setTextAppearance(R.style.item_list_restaurant_hours_open_txt);
               }
           }
           else
           {
               hours.setText(activity.getResources().getString(R.string.list_restaurants_adapter_close_now));
               if (Build.VERSION.SDK_INT < 23) {
                   hours.setTextAppearance(activity.getApplicationContext(), R.style.item_list_restaurant_hours_close_txt);
               } else {
                   hours.setTextAppearance(R.style.item_list_restaurant_hours_close_txt);
               }
           }
        }

        //TODO : Pas moyen de faire 1 ELSE pour les 2 ?
        /**
         * Update the workmate's number with documentSnapshot from Firebase
         */
        private void updateNumberWorkmates (Restaurant restaurant)
        {
            if (restaurant.getUserList() != null)
            {
                if (restaurant.getUserList().size() > 0)
                {
                    numberWorkmates = restaurant.getUserList().size();
                    String numberWorkmatesString = "(" + numberWorkmates + ")";
                    numberWorkmatesTxt.setText(numberWorkmatesString);
                    displayWorkmates();
                }
                else
                {
                    numberWorkmates = 0;
                    displayWorkmates();
                }
            }
            else
            {
                numberWorkmates = 0;
                displayWorkmates();
            }
        }

        /**
         * Update the visibility of numberWorkmatesTxt and peopleWorkmatesImage if numberWorkmates > 0
         */
        private void displayWorkmates()
        {
            if (numberWorkmates > 0)
            {
                this.numberWorkmatesTxt.setVisibility(View.VISIBLE);
                this.peopleWorkmatesImage.setVisibility(View.VISIBLE);
            }
            else
            {
                this.numberWorkmatesTxt.setVisibility(View.INVISIBLE);
                this.peopleWorkmatesImage.setVisibility(View.INVISIBLE);
            }
        }

        @OnClick(R.id.item_list_restaurant_card_view)
        void onClickItem()
        {
            onClickListenerItemList.onClickListener(getAdapterPosition());
        }
    }
}




