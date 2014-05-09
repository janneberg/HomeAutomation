package se.racetime.homeautomation.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import se.racetime.homeautomation.app.EnergyFragment;
import se.racetime.homeautomation.app.TelephoneFragment;
import se.racetime.homeautomation.app.TemperatureFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter
{
    public TabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int index)
    {
        switch (index)
        {
            case 0:
                return new TemperatureFragment();   // Top Rated fragment activity
            case 1:
                return new EnergyFragment();        // Games fragment activity
            case 2:
                return new TelephoneFragment();     // Movies fragment activity
        }

        return null;
    }

    @Override
    public int getCount()
    {
        return 3;    // get item count - equal to number of tabs
    }
}