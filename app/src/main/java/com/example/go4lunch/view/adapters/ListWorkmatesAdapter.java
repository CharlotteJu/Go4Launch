package com.example.go4lunch.view.adapters;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.util.Log;
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
import com.example.go4lunch.view.fragments.DetailsFragment;
import com.example.go4lunch.view.fragments.ListWorkmatesFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesAdapter extends FirestoreRecyclerAdapter<User, ListWorkmatesAdapter.ListWorkmatesViewHolder>
{
    public interface Listener
    {
        void onDataChanged();
    }

    private RequestManager glide;
    private Context context;
    private Listener callback;


    public ListWorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback )
    {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }


    @NonNull
    @Override
    public ListWorkmatesAdapter.ListWorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.item_list_workmates, parent, false);
        return new ListWorkmatesAdapter.ListWorkmatesViewHolder(v, callback);
    }

    @Override
    protected void onBindViewHolder(@NonNull ListWorkmatesViewHolder viewHolder, int i, @NonNull User user)
    {
        viewHolder.updateUI(user, glide, context);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    static class ListWorkmatesViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.item_list_workmates_image)
        ImageView imageView;
        @BindView(R.id.item_list_workmates_txt)
        TextView textView;

        Listener callback;

        public ListWorkmatesViewHolder(@NonNull View itemView, Listener callback) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.callback = callback;
        }

        private void updateUI(User user, RequestManager glide, Context context)
        {
            glide.load(user.getIllustration()).apply(RequestOptions.centerCropTransform()).into(imageView);



            if (callback instanceof ListWorkmatesFragment) {

                if (user.isChooseRestaurant()) {
                    textView.setText(user.getName() + " is eating " + " (" + user.getRestaurantChoose().getName() + ") ");

                    if (Build.VERSION.SDK_INT < 23) {
                        textView.setTextAppearance(context, R.style.item_list_workmates_choose_txt);
                    } else {
                        textView.setTextAppearance(R.style.item_list_workmates_choose_txt);
                    }
                } else {
                    textView.setText(user.getName() + " hasn't decided yet");

                    if (Build.VERSION.SDK_INT < 23) {
                        textView.setTextAppearance(context, R.style.item_list_workmates_no_choose_txt);
                    } else {
                        textView.setTextAppearance(R.style.item_list_workmates_no_choose_txt);
                    }
                }
            }
            else if (callback instanceof DetailsFragment)
            {
                textView.setText(user.getName() + "is joining!");
            }
        }
    }
}
