package com.tech.thrithvam.spoffice;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class Enquiries extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static Spinner listDurationSpinner;
    static ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiries);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Enquiries.this,EnquiryInput.class));
            }
        });

        //Spinner
        ArrayList<String> statisticsDuration = new ArrayList<String>();
        statisticsDuration.add(getResources().getString(R.string.days90));
        statisticsDuration.add(getResources().getString(R.string.days180));
        statisticsDuration.add(getResources().getString(R.string.days365));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_small_white, statisticsDuration);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        listDurationSpinner =(Spinner)findViewById(R.id.list_duration);
        listDurationSpinner.setAdapter(dataAdapter);
        listDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // getChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void followUpClick(View view){
        if(view.getTag().toString().equals("")) return;
        Intent intent = new Intent(Enquiries.this, FollowUp.class);
        intent.putExtra(Common.ENQUIRYID,view.getTag().toString());
        startActivity(intent);
    }
    public void callClick(View view){
        if(view.getTag().toString().equals("")) return;
        Uri number = Uri.parse("tel:" + view.getTag().toString());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }

    SearchView searchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        //Searching-------------------
        searchView=(SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                listDurationSpinner.setVisibility(View.GONE);
               /* if(adapter!=null){//for searching
                    adapter.getFilter(1).filter(searchView.getQuery().toString().trim());
                }*/
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listDurationSpinner.setVisibility(View.VISIBLE);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_enquiries, container, false);
            if( getArguments().getInt(ARG_SECTION_NUMBER)==1) {//Open
                Common.toastMessage(getContext(),"Open");
                getEnquiries("OE",rootView);
            }
            else if( getArguments().getInt(ARG_SECTION_NUMBER)==2) {//Converted
                Common.toastMessage(getContext(),"Converted");
                getEnquiries("CE",rootView);
            }
            else if( getArguments().getInt(ARG_SECTION_NUMBER)==3) {//NotConverted
                Common.toastMessage(getContext(),"Not Converted");
                getEnquiries("NCE",rootView);
            }
            return rootView;
        }
        public void getEnquiries(String enquiryStatusCode,View rootView){
            final ListView invoiceList=(ListView)rootView.findViewById(R.id.enquiry_list);
            final TextView noItems=(TextView)rootView.findViewById(R.id.no_items);
            noItems.setVisibility(View.GONE);
            invoiceList.setVisibility(View.GONE);
            int duration=0;
            if(listDurationSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.days90))){
                duration = 90;
            }
            else if(listDurationSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.days180))){
                duration=180;
            }
            else if(listDurationSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.days365))){
                duration=180;
            }
            //Threading------------------------------------------------------------------------------------------------------
            final Common common = new Common();
            String webService = "API/Enquiry/GetEnquiryListForMobile";
            String postData = "{\"duration\":\""+duration+"\",\"EnquiryStatus\":\""+enquiryStatusCode+"\"}";
            AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.loading_indicator);
            String[] dataColumns = {"ID",//0
                    "EnquiryNo",//1
                    "EnquiryDate",//2
                    "ContactTitle",//3
                    "ContactName",//4
                    "CompanyName",//5
                    "Mobile"//6
            };
            Runnable postThread = new Runnable() {
                @Override
                public void run() {
                    if(common.dataArrayList.size()==0){
                        noItems.setVisibility(View.VISIBLE);
                        return;
                    }
                    CustomAdapter adapter=new CustomAdapter(getContext(),common.dataArrayList,Common.ENQUIRYLIST);
                    invoiceList.setAdapter(adapter);
                    invoiceList.setVisibility(View.VISIBLE);
                }
            };
            Runnable postThreadFailed = new Runnable() {
                @Override
                public void run() {
                    Common.toastMessage(getContext(), R.string.failed_server+ common.msg);
                }
            };

            common.AsynchronousThread(getContext(),
                    webService,
                    postData,
                    loadingIndicator,
                    dataColumns,
                    postThread,
                    postThreadFailed);
            asyncTasks.add(common.asyncTask);
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Open";
                case 1:
                    return "Converted";
                case 2:
                    return "Not Converted";
            }
            return null;
        }
    }
    @Override
    public void onBackPressed() {
        for(int i=0;i<asyncTasks.size();i++){
            asyncTasks.get(i).cancel(true);
        }
        super.onBackPressed();
    }
}
