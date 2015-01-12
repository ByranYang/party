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

import org.w3c.dom.Text;

import java.util.Stack;

/**
 * Created by selena_wang on 11/23/14.
 */

public class friendsTest
        extends ActivityInstrumentationTestCase2<friends>
{
    private friends mActivity;
    private Button friendsButton;
    private View findfriends;
    private ListView friendlist;
    private Solo solo;

    public friendsTest() {
        super("com.example.selena_wang.frontend.friends", friends.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
        mActivity = this.getActivity();
        findfriends =  mActivity.findViewById(R.id.findFriendsFragment);
        friendlist = (ListView) mActivity.findViewById(R.id.friend_list);
    }

    public void testPreconditions() {
        assertNotNull(mActivity);
        assertNotNull(findfriends);
        assertNotNull(friendlist);
        assertNotNull(friendlist);
    }

    @MediumTest
    public void testFindFriendsButton(){
        friendsButton = (Button) mActivity.findViewById(R.id.findFriends);
        assertNotNull(friendsButton);
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                friendsButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();

    }

    @MediumTest
    public void testFindFriendsFragment(){
        TextView search_friend = (TextView) mActivity.findViewById(R.id.search_friend);
        EditText input = (EditText) mActivity.findViewById(R.id.inputFriend);
        Button searchButton = (Button) mActivity.findViewById(R.id.searchButton);
        TextView friendInfo = (TextView) mActivity.findViewById(R.id.friend_info);
        Button addButton = (Button) mActivity.findViewById(R.id.addButton);
        assertNotNull(search_friend);
        assertNotNull(input);
        assertNotNull(searchButton);
        assertNotNull(friendInfo);
        assertNotNull(addButton);
        assertEquals("Search friends isn't blank",search_friend.getText(),"Input Username Here");
        assertEquals("Friend info isn't blank", friendInfo.getText(),"");
    }

    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
        super.tearDown();
    }

}