package se.racetime.homeautomation.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import se.racetime.homeautomation.Com.Json;
import se.racetime.homeautomation.adapter.TellstickServiceAdapter;

import static se.racetime.homeautomation.Util.Constant.FIRST;
import static se.racetime.homeautomation.Util.Constant.SECOND;
import static se.racetime.homeautomation.Util.Constant.THIRD;

public class TelephoneFragment extends Fragment
{
    Activity activity;
    View view;
    ListView lstViewPhone;
    TellstickServiceAdapter adapter;
    enum HttpTask { standard, specific };
    private ArrayList<HashMap> list;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        list = new ArrayList<HashMap>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_telephone, container, false);
        lstViewPhone = (ListView) view.findViewById(R.id.listViewPhone);
        new ReadPhoneListJSONFeedTask(HttpTask.standard).execute("http://racetime.no-ip.org:8080/tellstickservice/Phone");

        lstViewPhone.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                HashMap item = list.get(position);
                String phoneNumber = item.get(FIRST).toString();

                Toast.makeText(activity.getBaseContext(), "Clicked " + phoneNumber, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private class ReadPhoneListJSONFeedTask extends AsyncTask<String, Void, String>
    {
        private HttpTask httpTask;

        public ReadPhoneListJSONFeedTask(HttpTask _httpTask)
        {
            httpTask = _httpTask;
        }

        protected String doInBackground(String... urls)
        {
            Json json = new Json();
            return json.readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result)
        {
            try
            {
                if(httpTask == HttpTask.standard)
                    UpdatePhoneList(result);
                else if(httpTask == HttpTask.specific)
                    UpdatePhoneNumber(result);

            }
            catch (Exception e)
            {
                Log.d("ReadPhoneListJSONFeedTask", e.getLocalizedMessage());
            }
        }

        private void UpdatePhoneList(String result)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(result);

                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String logDate = jsonObject.getString("logDate");
                    String phoneNumber = jsonObject.getString("phoneNumber");

                    JSONObject name = jsonObject.getJSONObject("name");
                    String firstName = name.getString("firstName");
                    String lastName = name.getString("lastName");

                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put(FIRST, phoneNumber);
                    item.put(SECOND, firstName + " " + lastName);
                    item.put(THIRD, logDate);

                    list.add(item);
                }

                adapter = new TellstickServiceAdapter(activity, list);
                lstViewPhone.setAdapter(adapter);

                //LinearLayout layout = (LinearLayout) view.findViewById(R.id.test);
                //layout.addView(graphView);
            }
            catch (Exception e)
            {
                Log.d("ReadPhoneListJSONFeedTask", e.getLocalizedMessage());
            }
        }

        private void UpdatePhoneNumber(String result)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(result);

                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String logDate = jsonObject.getString("logDate");
                }
            }
            catch (Exception e)
            {
                Log.d("ReadPhoneListJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }
}
