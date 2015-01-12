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

public class loginTest
        extends ActivityInstrumentationTestCase2<login>
{
    private login mActivity;
    private EditText inputUser;
    private EditText inputPassword;
    private Button logging_in;
    private Solo solo;

    public loginTest() {
        super("com.example.selena_wang.frontend.login", login.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
        mActivity = this.getActivity();
        inputUser =  (EditText)mActivity.findViewById(R.id.inputUser);
        inputPassword = (EditText) mActivity.findViewById(R.id.inputPass);
    }

    public void testPreconditions() {
        assertNotNull(mActivity);
        assertNotNull(inputUser);
        assertNotNull(inputPassword);
    }

    @MediumTest
    public void testLogin(){
        logging_in = (Button) mActivity.findViewById(R.id.loginButton);
        assertNotNull(logging_in);
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                inputUser.setText("byran8");
                inputPassword.setText("byran");
                logging_in.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();

    }

    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
        super.tearDown();
    }

}