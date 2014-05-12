package se.racetime.homeautomation.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import se.racetime.homeautomation.app.R;
import static se.racetime.homeautomation.Util.Constant.FIRST;
import static se.racetime.homeautomation.Util.Constant.SECOND;
import static se.racetime.homeautomation.Util.Constant.THIRD;

public class TellstickServiceAdapter extends BaseAdapter
{
    public ArrayList<HashMap> list;
    Activity activity;

    public TellstickServiceAdapter(Activity activity, ArrayList<HashMap> list)
    {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        return list.size(); // return the number of records in cursor
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private class ViewHolder
    {
        TextView txtPhoneNumber;
        TextView txtName;
        TextView txtDateTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)    // getView method is called for each item of ListView
    {
        ViewHolder holder;
        LayoutInflater inflater =  activity.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.listview_eachitem, null);
            holder = new ViewHolder();
            holder.txtPhoneNumber = (TextView) convertView.findViewById(R.id.textViewPhoneNumber);
            holder.txtName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.txtDateTime = (TextView) convertView.findViewById(R.id.textViewDateTime);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap item = list.get(position);
        holder.txtPhoneNumber.setText(item.get(FIRST).toString());
        holder.txtName.setText(item.get(SECOND).toString());
        holder.txtDateTime.setText(item.get(THIRD).toString());

        return convertView;
    }
}

