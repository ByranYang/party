package com.example.selena_wang.frontend;


import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import android.widget.TextView;
import com.robotium.solo.Solo;

/**
 * Created by selena_wang on 11/23/14.
 */

public class mapTest
        extends ActivityInstrumentationTestCase2<caravan_map>
{
    private caravan_map mActivity;
    private View map;
    private Solo solo;

    public mapTest() {
        super("com.example.selena_wang.frontend.caravan_map", caravan_map.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
        mActivity = this.getActivity();
        map = mActivity.findViewById(R.id.map);
    }

    public void testPreconditions() {
        assertNotNull(mActivity);
        assertNotNull(map);
    }

    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
        super.tearDown();
    }

}