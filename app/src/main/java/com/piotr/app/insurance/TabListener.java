package com.piotr.app.insurance;

/**
 * Created by Piotr on 2015-08-15.
 */

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar.Tab;

public class TabListener implements ActionBar.TabListener {

    private Fragment fragment;

    public TabListener(Fragment mFormFragment) {

        this.fragment = mFormFragment;

    }

    // When a tab is tapped, the FragmentTransaction replaces
    // the content of our main layout with the specified fragment;
    // that's why we declared an id for the main layout.
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (fragment.isDetached()) {
            ft.attach(fragment);
        } else {

            ft.replace(R.id.container, fragment);

            //FormFragment frag = (FormFragment)fragment;

            //frag.getFormFromCache();

        }
    }

    // When a tab is unselected, we have to hide it from the user's view.
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        //if (tab.getPosition() == 0) {

            //FormFragment frag = (FormFragment)fragment;

            //frag.cacheForm();

        //}

        ft.detach(fragment);
    }

    // Nothing special here. Fragments already did the job.
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }
}