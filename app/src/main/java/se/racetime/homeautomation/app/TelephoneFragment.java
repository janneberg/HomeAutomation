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
import android.widget.GridView;
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
                //String item = lstViewPhone.gett.view.((GridView) view).getItemAtPosition(position).te.getText().toString();
                Toast.makeText(activity.getBaseContext(), "Clicked", Toast.LENGTH_LONG).show();
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
                    UpdateAllTemperatures(result);

            }
            catch (Exception e)
            {
                Log.d("ReadTemperatureJSONFeedTask", e.getLocalizedMessage());
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
                Log.d("ReadTemperatureJSONFeedTask", e.getLocalizedMessage());
            }
        }

        private void UpdateAllTemperatures(String result)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(result);

                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String sensorId = jsonObject.getString("sensorId");
                    String temperature = jsonObject.getString("temperature");
                    String humidity = jsonObject.getString("humidity");
                    String createdDate = jsonObject.getString("createDate");

                    TextView textViewTotal = null;
                    if(sensorId.contains("140"))
                        textViewTotal = (TextView) view.findViewById(R.id.textViewTempOutside2);
                    else if(sensorId.contains("139"))
                        textViewTotal = (TextView) view.findViewById(R.id.textViewTempUnderhouse);
                    else if(sensorId.contains("180"))
                        textViewTotal = (TextView) view.findViewById(R.id.textViewTempPadio);
                    else if(sensorId.contains("9"))
                        textViewTotal = (TextView) view.findViewById(R.id.textViewTempAttic);
                    else if(sensorId.contains("151"))
                        textViewTotal = (TextView) view.findViewById(R.id.textViewTempInside);
                    else if(sensorId.contains("181"))
                        textViewTotal = (TextView) view.findViewById(R.id.textViewTempOutside1);
                    else if(sensorId.contains("43"))
                        textViewTotal = (TextView) view.findViewById(R.id.textViewTempBasement);

                    if(textViewTotal != null)
                        textViewTotal.setText(temperature);
                }
            }
            catch (Exception e)
            {
                Log.d("ReadTemperatureJSONFeedTask", e.getLocalizedMessage());
            }
        }
    }
}
