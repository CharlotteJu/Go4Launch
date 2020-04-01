package com.example.go4lunch.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import butterknife.BindView;

public class InfoWindowMapAdapter implements GoogleMap.InfoWindowAdapter
{
    private Context context;
    private LayoutInflater inflater;

    @BindView(R.id.info_window_map_txt)
    TextView textView;

    public InfoWindowMapAdapter(Context context)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
       View v = inflater.inflate((R.layout.info_window_map), null);

       textView.setText(marker.getTitle());



        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
