package se.racetime.homeautomation.app;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.GraphViewSeries;
//import com.jjoe64.graphview.LineGraphView;

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
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {

    }

    enum HttpTask { basic, idHour };
    GraphView graphView;
    float x1,x2;
    float y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        graphView = new LineGraphView(this,"Temperature");
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
                    Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_LONG).show();
                }

                // if right to left sweep event on screen
                if (x1 > x2)
                {
                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_LONG).show();
                }

                // if UP to Down sweep event on screen
                if (y1 < y2)
                {
                    Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_LONG).show();
                }

                //if Down to UP sweep event on screen
                if (y1 > y2)
                {
                    Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return false;
    }

    public void onButtonPress(View v)
    {
        TextView textViewGraphHeader = (TextView)findViewById( R.id.textViewGraphHeader );

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
        Integer width = graphView.getWidth();
        Integer height = graphView.getHeight();

        Integer w = graphView.getMeasuredWidth();
        Integer h = graphView.getMeasuredHeight();

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
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            Date d = new Date((long) value);
                            return dateFormat.format(d);
                        }
                        return null; // let graphview generate Y-axis label for us
                    }
                });

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
