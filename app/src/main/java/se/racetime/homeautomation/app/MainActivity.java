package se.racetime.homeautomation.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;


public class MainActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init example series data
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
        layout.addView(graphView);
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
                // do something
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

        TextView textViewTotal = (TextView)findViewById( R.id.textViewHeader );
        textViewTotal.setText(text.toString());
    }

}
