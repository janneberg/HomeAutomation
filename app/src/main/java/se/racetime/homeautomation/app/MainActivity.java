package se.racetime.homeautomation.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.GraphViewSeries;
//import com.jjoe64.graphview.LineGraphView;

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
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity
{
    enum HttpTask { basic, idHour };
    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphView = new LineGraphView(this,"Temperature");

        /*
        {
            @Override
            protected String formatLabel(double value, boolean isValueX)
            {
                if (isValueX)   // transform number to time
                    return dateTimeFormatter.format(new Date((long) value*1000));
                else
                    return super.formatLabel(value, isValueX);
            }
        };*/


        /*        // init example series data




        LinearLayout layout = (LinearLayout) findViewById(R.id.test);
        layout.addView(graphView);*/
    }


    @Override
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
    }

    public void onButtonPress(View v)
    {
        switch (v.getId()) {
            case R.id.buttonTempAttic:
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/9/12");
                break;
            case R.id.buttonTempBasement:
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/43/12");
                break;
            case R.id.buttonTempInside:
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/151/12");
                break;
            case R.id.buttonTempOutside1:
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/181/12");
                break;
            case R.id.buttonTempOutside2:
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/140/12");
                break;
            case R.id.buttonTempPadio:
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/180/12");
                break;
            case R.id.buttonTempUnderhouse:
                new ReadTemperatureJSONFeedTask(HttpTask.idHour).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/139/12");
                break;
        }

        //int number = Integer.parseInt(text.toString());
        //total += number;

        //TextView textViewTotal = (TextView)findViewById( R.id.textViewTotal );
        //textViewTotal.setText(Integer.toString(total));
    }

    public void onButtonUpdatePress(View v)
    {
        Button button = (Button)v;
        CharSequence text = button.getText();

        new ReadTemperatureJSONFeedTask(HttpTask.basic).execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature");
    }

    public String readJSONFeed(String URL)
    {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try
        {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200)
            {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }
                inputStream.close();
            }
            else
            {
                Log.d("JSON", "Failed to download file");
            }
        }
        catch (Exception e)
        {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
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
            return readJSONFeed(urls[0]);
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

                //final java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(mActivity);

                GraphView.GraphViewData graphViewData[] = new GraphView.GraphViewData[jsonArray.length()];

                for(int i = 0; i < jsonArray.length(); i++)
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


                GraphViewSeries exampleSeries = new GraphViewSeries(graphViewData);  // init data
                graphView.addSeries(exampleSeries); // data

                LinearLayout layout = (LinearLayout) findViewById(R.id.test);
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
                        textViewTotal = (TextView)findViewById(R.id.textViewTempOutside2);
                    else if(sensorId.contains("139"))
                        textViewTotal = (TextView)findViewById(R.id.textViewTempUnderhouse);
                    else if(sensorId.contains("180"))
                        textViewTotal = (TextView)findViewById(R.id.textViewTempPadio);
                    else if(sensorId.contains("9"))
                        textViewTotal = (TextView)findViewById(R.id.textViewTempAttic);
                    else if(sensorId.contains("151"))
                        textViewTotal = (TextView)findViewById(R.id.textViewTempInside);
                    else if(sensorId.contains("181"))
                        textViewTotal = (TextView)findViewById(R.id.textViewTempOutside1);
                    else if(sensorId.contains("43"))
                        textViewTotal = (TextView)findViewById(R.id.textViewTempBasement);

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
