package com.pepsi.battleofthebands.adapter;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.entity.Feedback;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class FeedbackAdapter extends ArrayAdapter<Feedback> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<Feedback> values;

    public FeedbackAdapter(Context context, int textViewResourceId, ArrayList<Feedback> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Feedback getItem(int position) {
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

        TextView title = row.findViewById(R.id.textViewTitle);
        title.setText(values.get(position).getType().toUpperCase());
        title.setTextColor(context.getResources().getColor(R.color.white));
        title.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        title.setPadding(Utils.getDpiFromPixel(context, 16), 0, 0, 0);
        return row;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = inflator.inflate(R.layout.spinner_view_feedback, null);

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
        TextView title = row.findViewById(R.id.textViewTitle);
        title.setTextColor(context.getResources().getColor(R.color.white));
        title.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        title.setText(Utils.getFirstLetterCapital(values.get(position).getType().toUpperCase()));
        title.setTextColor(Color.BLACK);
        title.setPadding(Utils.getDpiFromPixel(context, 16), 0, 0, 0);

        layout.setAlpha(0.95f);
        return row;
    }
}