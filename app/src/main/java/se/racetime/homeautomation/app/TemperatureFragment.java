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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.racetime.homeautomation.Com.Json;

public class TemperatureFragment extends Fragment implements View.OnClickListener
{
    Activity activity;
    View view;
    //Button btnUpdate, btnTempAttic;

    enum HttpTask { basic, idHour };
    GraphView graphView;
    float x1,x2;
    float y1, y2;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        graphView = new LineGraphView(activity, "Temperature");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_temperature, container, false);

        Button btnUpdate = (Button) view.findViewById(R.id.buttonUpdate);
        btnUpdate.setOnClickListener(this);

        Button btnTempAttic = (Button) view.findViewById(R.id.buttonTempAttic);
        btnTempAttic.setOnClickListener(this);

        Button btnTempBasement = (Button) view.findViewById(R.id.buttonTempBasement);
        btnTempBasement.setOnClickListener(this);

        Button btnTempInside = (Button) view.findViewById(R.id.buttonTempInside);
        btnTempInside.setOnClickListener(this);

        Button btnTempOutside1 = (Button) view.findViewById(R.id.buttonTempOutside1);
        btnTempOutside1.setOnClickListener(this);

        Button btnTempOutside2 = (Button) view.findViewById(R.id.buttonTempOutside2);
        btnTempOutside2.setOnClickListener(this);

        Button btnTempPadio = (Button) view.findViewById(R.id.buttonTempPadio);
        btnTempPadio.setOnClickListener(this);

        Button btnTempUnderhouse = (Button) view.findViewById(R.id.buttonTempUnderhouse);
        btnTempUnderhouse.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buttonUpdate:
                onButtonUpdatePress(v);
                break;
            default:
                onButtonPress(v);
                break;
        }
    }

    public void onButtonPress(View v)
    {
        TextView textViewGraphHeader = (TextView) view.findViewById( R.id.textViewGraphHeader );

        switch (v.getId()) {
            case R.id.buttonTempAttic:
                textViewGraphHeader.setText("TempAttic");
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/9/12");
                break;
            case R.id.buttonTempBasement:
                textViewGraphHeader.setText("TempBasement");
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/43/12");
                break;
            case R.id.buttonTempInside:
                textViewGraphHeader.setText("TempInside");
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/151/12");
                break;
            case R.id.buttonTempOutside1:
                textViewGraphHeader.setText("TempOutside1");
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/181/12");
                break;
            case R.id.buttonTempOutside2:
                textViewGraphHeader.setText("TempOutside2");
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/140/12");
                break;
            case R.id.buttonTempPadio:
                textViewGraphHeader.setText("TempPadio");
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/180/12");
                break;
            case R.id.buttonTempUnderhouse:
                textViewGraphHeader.setText("TempUnderhouse");
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/139/12");
                break;
        }

        //int number = Integer.parseInt(text.toString());
        //total += number;
    }

    public void onButtonUpdatePress(View v)
    {
        new ReadTemperatureJSONFeedTask(HttpTask.basic).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature");
    }

    private class ReadTemperatureJSONFeedTask extends AsyncTask<String, Void, String>
    {
        private HttpTask httpTask;

        public ReadTemperatureJSONFeedTask(HttpTask _httpTask)
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
                if(httpTask == HttpTask.basic)
                    UpdateAllTemperatures(result);
                else if(httpTask == HttpTask.idHour)
                    UpdateTemperatureDiagram(result);

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

                graphView.removeAllSeries();
                GraphViewSeries exampleSeries = new GraphViewSeries(graphViewData);  // init data
                graphView.addSeries(exampleSeries); // data

                graphView.getGraphViewStyle().setGridColor(Color.GREEN);
                graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
                graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
                graphView.getGraphViewStyle().setTextSize((float) 12.0);
                graphView.getGraphViewStyle().setNumHorizontalLabels(5);
                graphView.getGraphViewStyle().setNumVerticalLabels(5);
                graphView.getGraphViewStyle().setVerticalLabelsWidth(20);
                //graphView.setManualYAxis(true);
                //graphView.setManualYAxisBounds(20.0, -5);
                graphView.setTitle("");

                // date as label formatter
                final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                graphView.setCustomLabelFormatter(new CustomLabelFormatter()
                {
                    @Override
                    public String formatLabel(double value, boolean isValueX)
                    {
                        if (isValueX)
                        {
                            Date d = new Date((long) value);
                            return dateFormat.format(d);
                        }
                        return null; // let graphview generate Y-axis label for us
                    }
                });

                LinearLayout layout = (LinearLayout) view.findViewById(R.id.test);
                layout.addView(graphView);
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

    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
/*


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /*
    // onTouchEvent () method gets called when User performs any touch event on screen
    // Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent)
    {
        switch (touchevent.getAction())
        {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN:
            {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                x2 = touchevent.getX();
                y2 = touchevent.getY();

                //if left to right sweep event on screen
                if (x1 < x2)
                {
                    Toast.makeText(activity, "Left to Right Swap Performed", Toast.LENGTH_LONG).show();
                }

                // if right to left sweep event on screen
                if (x1 > x2)
                {
                    Toast.makeText(activity, "Right to Left Swap Performed", Toast.LENGTH_LONG).show();
                }

                // if UP to Down sweep event on screen
                if (y1 < y2)
                {
                    Toast.makeText(activity, "UP to Down Swap Performed", Toast.LENGTH_LONG).show();
                }

                //if Down to UP sweep event on screen
                if (y1 > y2)
                {
                    Toast.makeText(activity, "Down to UP Swap Performed", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return false;
    }


    */
}
