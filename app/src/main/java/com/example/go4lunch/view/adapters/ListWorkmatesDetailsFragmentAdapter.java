package com.example.go4lunch.view.adapters;

import android.app.Activity;
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
import com.example.go4lunch.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesDetailsFragmentAdapter extends RecyclerView.Adapter<ListWorkmatesDetailsFragmentAdapter.ListWorkmatesViewHolder>
{
    private RequestManager glide;
    private Activity activity;
    private List<User> userList;

    public ListWorkmatesDetailsFragmentAdapter (List<User> userList,RequestManager glide, Activity activity)
    {
        this.userList = userList;
        this.glide = glide;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ListWorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.item_list_workmates, parent, false);
        return new ListWorkmatesDetailsFragmentAdapter.ListWorkmatesViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ListWorkmatesViewHolder holder, int position)
    {
        holder.updateUI(userList.get(position), glide);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ListWorkmatesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_list_workmates_image)
        ImageView imageView;
        @BindView(R.id.item_list_workmates_txt)
        TextView textView;
        private Activity activity;

        private ListWorkmatesViewHolder(@NonNull View itemView, Activity activity) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.activity = activity;
        }

        private void updateUI(User user, RequestManager glide)
        {
            glide.load(user.getIllustration()).apply(RequestOptions.circleCropTransform()).into(imageView);
            String textString = activity.getResources().getString(R.string.list_workmates_adapter_is_joining);
            String finalText = user.getName().split(" ")[0] + " " + textString;
            textView.setText(finalText);
        }
    }
}
