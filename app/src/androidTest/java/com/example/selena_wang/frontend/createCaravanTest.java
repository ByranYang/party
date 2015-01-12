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

public class createCaravanTest
        extends ActivityInstrumentationTestCase2<create_caravan>
{
    private create_caravan mActivity;
    private Button inputDestination;
    private EditText inputFriend;
    private Solo solo;

    public createCaravanTest() {
        super("com.example.selena_wang.frontend.create_caravan", create_caravan.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
        mActivity = this.getActivity();
        inputDestination =  (Button)mActivity.findViewById(R.id.caravanMap);
//        inputFriend = (EditText) mActivity.findViewById(R.id.addFriendInput);
    }

    public void testPreconditions() {
        assertNotNull(mActivity);
        assertNotNull(inputDestination);
//        assertNotNull(inputFriend);
    }

//    @MediumTest
//    public void testCreate(){
//        logging_in = (Button) mActivity.findViewById(R.id.loginButton);
//        assertNotNull(logging_in);
//        mActivity.runOnUiThread(new Runnable() {
//            public void run() {
//                inputUser.setText("byran8");
//                inputPassword.setText("byran");
//                logging_in.performClick();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//
//    }

    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
        super.tearDown();
    }

}