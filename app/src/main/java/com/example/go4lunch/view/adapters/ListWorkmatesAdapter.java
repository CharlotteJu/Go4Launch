package com.example.go4lunch.view.adapters;

import android.app.Activity;
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

    private RequestManager glide;
    private Context context;
    private Activity activity;


    public ListWorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, Activity activity )
    {
        super(options);
        this.glide = glide;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ListWorkmatesAdapter.ListWorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.item_list_workmates, parent, false);
        return new ListWorkmatesAdapter.ListWorkmatesViewHolder(v, activity);
    }

    @Override
    protected void onBindViewHolder(@NonNull ListWorkmatesViewHolder viewHolder, int i, @NonNull User user)
    {
        viewHolder.updateUI(user, glide, context);
    }


    static class ListWorkmatesViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.item_list_workmates_image)
        ImageView imageView;
        @BindView(R.id.item_list_workmates_txt)
        TextView textView;

        private Activity activity;

        private ListWorkmatesViewHolder(@NonNull View itemView, Activity activity) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.activity = activity;
        }

        private void updateUI(User user, RequestManager glide, Context context)
        {
            glide.load(user.getIllustration()).apply(RequestOptions.circleCropTransform()).into(imageView);

            String textString;
            String finalText;
            String firstName = user.getName().split(" ")[0];

            if (user.isChooseRestaurant()) {

                textString = activity.getResources().getString(R.string.list_workmates_adapter_is_eating);
                String textStringEnd = activity.getResources().getString(R.string.list_workmates_adapter_parenthesis);

                finalText = firstName + " " + textString + user.getRestaurantChoose().getName() + textStringEnd;

                textView.setText(finalText);

                if (Build.VERSION.SDK_INT < 23) {
                    textView.setTextAppearance(context, R.style.item_list_workmates_choose_txt);
                } else {
                    textView.setTextAppearance(R.style.item_list_workmates_choose_txt);
                }
            } else {

                textString = " " + activity.getResources().getString(R.string.list_workmates_adapter_hasnt_decided_yed);
                finalText = firstName + textString;

                textView.setText(finalText);

                if (Build.VERSION.SDK_INT < 23) {
                    textView.setTextAppearance(context, R.style.item_list_workmates_no_choose_txt);
                } else {
                    textView.setTextAppearance(R.style.item_list_workmates_no_choose_txt);
                }
            }


        }
    }
}
