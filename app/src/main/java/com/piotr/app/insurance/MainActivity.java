package com.piotr.app.insurance;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    private ActionBar mActionBar;

    public String fileToRestore = "";

    Fragment mFormFragment = new FormFragment();

    Fragment mCalculationsFragment= new CalculationsFragment();

    Fragment mPoliciesFragment = new PolisyFragment();

    Fragment mWeb1Fragment = new Web1Fragment();

    Fragment mWeb2Fragment = new Web2Fragment();


    public void setFileToRestore(String file) {

        this.fileToRestore = file;

    }

    public String getFileToRestore() {

        return fileToRestore;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFormFragment)
                    .commit();
        }

        // find the action bar
        mActionBar = getSupportActionBar();

        mActionBar.setIcon(R.drawable.amabee_icon);

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mActionBar.setDisplayShowTitleEnabled(true);

        // First Tab of the Activity
        ActionBar.Tab mTab = mActionBar.newTab().setText("Oblicz").setTabListener(new TabListener(mFormFragment));
        mActionBar.addTab(mTab);
        mActionBar.selectTab(mTab);

        // Second Tab of the Activity
        mTab = mActionBar.newTab().setText("Moje kalkulacje").setTabListener(new TabListener(mCalculationsFragment));
        mActionBar.addTab(mTab);

        mTab = mActionBar.newTab().setText("Polisy").setTabListener(new TabListener(mPoliciesFragment));
        mActionBar.addTab(mTab);

        mTab = mActionBar.newTab().setText("Baza wiedzy").setTabListener(new TabListener(mWeb1Fragment));
        mActionBar.addTab(mTab);

        mTab = mActionBar.newTab().setText("Szkoda").setTabListener(new TabListener(mWeb2Fragment));
        mActionBar.addTab(mTab);

    }


    public void setTab(int tab) {

        mActionBar.selectTab(mActionBar.getTabAt(tab));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {



    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */

}
