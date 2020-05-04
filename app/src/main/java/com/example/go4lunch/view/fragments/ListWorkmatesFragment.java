package com.example.go4lunch.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view.adapters.ListWorkmatesAdapter;
import com.example.go4lunch.view.adapters.OnClickListenerItemList;
import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.injection.Injection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesFragment extends Fragment implements OnClickListenerItemList {

    //FOR DESIGN
    @BindView(R.id.fragment_list_workmates_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar_layout)
    ConstraintLayout progressBarLayout;

    //FOR DATA
    private ViewModelGo4Lunch viewModelGo4Lunch;
    private List<User> usersList;
    private ListWorkmatesAdapter adapter;

    public ListWorkmatesFragment() {}

    public static ListWorkmatesFragment newInstance()
    {
        return new ListWorkmatesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_list_workmates, container, false);
        ButterKnife.bind(this, v);
        this.progressBarLayout.setVisibility(View.VISIBLE);
        configRecyclerView();
        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.configViewModel();
    }

    ////////////////////////////////////////// VIEW MODEL ///////////////////////////////////////////

    private void configViewModel()
    {
        ViewModelFactoryGo4Lunch viewModelFactoryGo4Lunch = Injection.viewModelFactoryGo4Lunch();
        viewModelGo4Lunch = ViewModelProviders.of(this, viewModelFactoryGo4Lunch).get(ViewModelGo4Lunch.class);
        this.getUserList();
    }

    private void getUserList()
    {
        this.viewModelGo4Lunch.getUsersListMutableLiveData().observe(this, userList ->
        {
            this.usersList = userList;
            adapter.updateList(usersList);
            this.progressBarLayout.setVisibility(View.INVISIBLE);
        });
    }

    ////////////////////////////////////////// CONFIGURE ///////////////////////////////////////////

    private void configRecyclerView()
    {
        adapter = new ListWorkmatesAdapter(Glide.with(this), getActivity(), this);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onClickListener(int position)
    {
        if (usersList.get(position).isChooseRestaurant())
        {
            Intent intent = new Intent(getContext(), DetailsActivity.class);
            intent.putExtra("placeId", usersList.get(position).getRestaurantChoose().getPlaceId());
            startActivity(intent);
        }
    }
}

