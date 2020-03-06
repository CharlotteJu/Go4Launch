package com.example.go4lunch.view.adapters;

import android.content.Context;
import android.media.Image;
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
import com.example.go4lunch.model.User;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesAdapter extends RecyclerView.Adapter<ListWorkmatesAdapter.ListWorkmatesViewHolder>
{
    private List<User> users;
    private RequestManager glide;
    private Context context;

    public ListWorkmatesAdapter(List<User> users, RequestManager glide)
    {
        this.users = users;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ListWorkmatesAdapter.ListWorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.item_list_workmates, parent, false);
        return new ListWorkmatesAdapter.ListWorkmatesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListWorkmatesAdapter.ListWorkmatesViewHolder holder, int position)
    {
        holder.updateUI(users.get(position), glide, context);
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    static class ListWorkmatesViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.item_list_workmates_image)
        ImageView imageView;
        @BindView(R.id.item_list_workmates_txt)
        TextView textView;

        public ListWorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        private void updateUI(User user, RequestManager glide, Context context)
        {
            glide.load(user.getIllustration()).apply(RequestOptions.centerCropTransform()).into(imageView);

            if (user.isChooseRestaurant())
            {
                textView.setText(user.getName() + " is eating " + user.getRestaurantChoose().getType() + " (" + user.getRestaurantChoose().getName() + ") ");

                if(Build.VERSION.SDK_INT < 23)
                {
                    textView.setTextAppearance(context, R.style.item_list_workmates_choose_txt);
                }
                else
                {
                    textView.setTextAppearance(R.style.item_list_workmates_choose_txt);
                }
            }
            else
            {
                textView.setText(user.getName() + " hasn't decided yet");

                if(Build.VERSION.SDK_INT < 23)
                {
                    textView.setTextAppearance(context, R.style.item_list_workmates_no_choose_txt);
                }
                else
                {
                    textView.setTextAppearance(R.style.item_list_workmates_no_choose_txt);
                }
            }
        }
    }
}
