package se.racetime.homeautomation.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import se.racetime.homeautomation.Com.Json;

public class TelephoneFragment extends Fragment
{
    Activity activity;
    View view;
    ListView lstViewPhone;
    enum HttpTask { standard, specific };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_telephone, container, false);

        lstViewPhone = (ListView) view.findViewById(R.id.listViewPhone);

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
                    UpdateTemperatureDiagram(result);
                else if(httpTask == HttpTask.specific)
                    UpdateAllTemperatures(result);

            }
            catch (Exception e)
            {
                Log.d("ReadTemperatureJSONFeedTask", e.getLocalizedMessage());
            }
        }

        private void UpdateTemperatureDiagram(String result)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(result);

                GraphView.GraphViewData graphViewData[] = new GraphView.GraphViewData[jsonArray.length()];

                for(int i = jsonArray.length() - 1; i > 0; i--)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String sensorId = jsonObject.getString("sensorId");
                    String temperature = jsonObject.getString("temperature");
                    String humidity = jsonObject.getString("humidity");
                    String createdDate = jsonObject.getString("createDate");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdf.parse(createdDate);
                    long millisecond = date.getTime();

                    graphViewData[i] = new GraphView.GraphViewData(millisecond, new Double(temperature.replace(',','.')));
                }

                LinearLayout layout = (LinearLayout) view.findViewById(R.id.test);
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
                    if(sensorId == "140")
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
