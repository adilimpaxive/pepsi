package com.pepsi.battleofthebands.adapter;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.entity.Seasons;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SeasonAdapter extends ArrayAdapter<Seasons> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<Seasons> values;

    public SeasonAdapter(Context context, int textViewResourceId, ArrayList<Seasons> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Seasons getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = inflator.inflate(R.layout.spinner_view_selected, null);
        RelativeLayout layout = row.findViewById(R.id.layoutMain);
        layout.setBackgroundColor(Color.TRANSPARENT);
        View view = row.findViewById(R.id.viewLine);
        view.setBackgroundColor(Color.TRANSPARENT);
        ImageView imageViewMenu = row.findViewById(R.id.imageViewMenu);
        imageViewMenu.setVisibility(View.GONE);
        TextView title = row.findViewById(R.id.textViewTitle);
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        title.setTextColor(context.getResources().getColor(R.color.white));
        title.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        title.setText(values.get(position).getName().toUpperCase());
        title.setPadding(Utils.getDpiFromPixel(context, 16), 0, 0, 0);
        return row;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = inflator.inflate(R.layout.spinner_view_selected, null);

        RelativeLayout layout = row.findViewById(R.id.layoutMain);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 49));
        layout.setLayoutParams(params);
        layout.setBackgroundColor(Color.WHITE);
        View view = row.findViewById(R.id.viewLine);
        view.setBackgroundColor(Color.BLACK);
        if (position == values.size() - 1) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        ImageView imageViewMenu = row.findViewById(R.id.imageViewMenu);
        imageViewMenu.setVisibility(View.GONE);
        TextView title = row.findViewById(R.id.textViewTitle);
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        title.setTextColor(context.getResources().getColor(R.color.white));
        title.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        title.setText(Utils.getFirstLetterCapital(values.get(position).getName()));
        title.setTextColor(Color.BLACK);
        title.setPadding(Utils.getDpiFromPixel(context, 16), 0, 0, 0);
        // And finally return your dynamic (or custom) view for each spinner item
        layout.setAlpha(0.95f);
        return row;
    }
}