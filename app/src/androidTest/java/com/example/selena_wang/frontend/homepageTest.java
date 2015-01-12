package com.example.selena_wang.frontend;


import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.w3c.dom.Text;

import java.util.Stack;

/**
 * Created by selena_wang on 11/23/14.
 */

public class homepageTest
        extends ActivityInstrumentationTestCase2<homepage>
{
    private homepage mActivity;
    private TextView username;
    private Button caravanButton;
    private TextView caravanInfo;
    private ListView homelist;
    private Solo solo;

    public homepageTest() {
        super("com.example.selena_wang.frontend.homepage", homepage.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
        mActivity = this.getActivity();
        username = (TextView) mActivity.findViewById(R.id.username);
        caravanInfo = (TextView) mActivity.findViewById(R.id.caravan_info);
        homelist = (ListView) mActivity.findViewById(R.id.home_list);
    }

    public void testPreconditions() {
        assertNotNull(mActivity);
        assertNotNull(username);
        assertNotNull(caravanInfo);
        assertNotNull(homelist);
    }

//    @MediumTest
//    public void testClickButton(){
//        caravanButton = (Button) mActivity.findViewById(R.id.caravan_button_home);
//        assertNotNull(caravanButton);
//        mActivity.runOnUiThread(new Runnable() {
//            public void run() {
//                caravanButton.performClick();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//    }

    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
        super.tearDown();
    }

}