package se.racetime.homeautomation.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class MainActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        // init example series data
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphView.GraphViewData[] {
                new GraphView.GraphViewData(1, 2.0d)
                , new GraphView.GraphViewData(2, 1.5d)
                , new GraphView.GraphViewData(3, 2.5d)
                , new GraphView.GraphViewData(4, 1.0d)
        });

        GraphView graphView = new LineGraphView(
                this // context
                , "GraphViewDemo" // heading
        );
        graphView.addSeries(exampleSeries); // data


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
                new ReadTemperatureIdJSONFeedTask().execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature/9/12");
                break;
            case R.id.buttonTempBasement:
                // do something else
                break;
            case R.id.buttonTempInside:
                // i'm lazy, do nothing
                break;
            case R.id.buttonTempOutside1:
                // do something else
                break;
            case R.id.buttonTempOutside2:
                // do something else
                break;
            case R.id.buttonTempPadio:
                // do something else
                break;
            case R.id.buttonTempUnderhouse:
                // do something else
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

        new ReadTemperatureJSONFeedTask().execute("http://racetime.no-ip.org:8080/tellstickservice/Temperature");
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
        protected String doInBackground(String... urls)
        {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result)
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

    private class ReadTemperatureIdJSONFeedTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result)
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
