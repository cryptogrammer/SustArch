package com.example.utkarshgagrg.sustarch;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class WatsonResultsPage extends Activity {
    private static ArrayAdapter<String> answerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);

        //TextView textView = new TextView(this);
        //textView.setPadding(84,84,84,84);

        //scrollView.addView(textView);

        //setContentView(scrollView);

        String result = getIntent().getStringExtra("result");
        //textView.setText(result);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.statusBarColor));
        window.setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        //getFragmentManager().beginTransaction()
        //            .add(R.id.container, new ResultsFragment())
        //            .commit();

        setContentView(R.layout.activity_watson_results_page2);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ResultsFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_watson_results_page, menu);
        return true;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ResultsFragment extends Fragment {

        public ResultsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_watson_results_page, container, false);
            if (getActivity().getIntent().getStringArrayListExtra("resultList").size() != 0){
                answerListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.answer_list_view_layout, R.id.list_item_answer_textview, getActivity().getIntent().getStringArrayListExtra("resultList"));
            ListView listView = (ListView) rootView.findViewById(R.id.list_view_answers);
            listView.setAdapter(answerListAdapter);
            listView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String answer = answerListAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), DetailedAnswerView.class)
                            .putExtra(Intent.EXTRA_TEXT, answer);
                    startActivity(intent);

                }
            });
        }
            else{
                Toast.makeText(getActivity(), "Please try another query", Toast.LENGTH_LONG).show();
            }
            //getActivity().setContentView(rootView);
            return rootView;
        }
    }
}
