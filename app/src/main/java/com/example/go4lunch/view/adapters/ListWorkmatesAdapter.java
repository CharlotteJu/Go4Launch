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

   //private List<User> users;
    private RequestManager glide;
    private Context context;
    private Listener callback;
    private String emailCurrentUser;

    public ListWorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback, String emailCurrentUser)
    {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.emailCurrentUser = emailCurrentUser;
    }

    /*public ListWorkmatesAdapter(List<User> users, RequestManager glide)
    {
        this.users = users;
        this.glide = glide;
    }*/

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

        public ListWorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        private void updateUI(User user, RequestManager glide, Context context)
        {
            glide.load(user.getIllustration()).apply(RequestOptions.centerCropTransform()).into(imageView);

            if (user.isChooseRestaurant())
            {
                textView.setText(user.getName() + " is eating " + " (" + user.getRestaurantChoose().getName() + ") ");

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
