package com.example.utkarshgagrg.sustarch;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WatsonQueryPageFragment extends Fragment {

    private final String mLogTag = "WatsonQueryFragment: ";
    private String mWatsonQueryString = "";
    private String mWatsonAnswerString = "";
    private boolean mIsQuerying = false;
    private Button askWatsonButton;
    private EditText watsonQueryField;

    private ProgressDialog dialog;


    private WatsonQueryPage mCallbacks;
    private WatsonQuery mQuery;

    public WatsonQueryPageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_watson_query_page, container, false);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.statusBarColor));
        window.setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        ActionBar bar = getActivity().getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        watsonQueryField = (EditText) rootView.findViewById(R.id.watsonQueryField);
        askWatsonButton = (Button) rootView.findViewById(R.id.askWatsonButton);
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setMessage("Querying...\nPlease wait...");
        askWatsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWatsonQueryString = watsonQueryField.getText().toString();
                WatsonQuery query = new WatsonQuery();
                query.execute();

            }
        });
        return rootView;
    }

    private class WatsonQuery extends AsyncTask<Void, Integer, String> {

        private SSLContext context;
        private HttpsURLConnection connection;
        private String jsonData;

        @Override
        protected void onPreExecute() {
            if (dialog != null)
                dialog.show();
        }

        @Override
        protected String doInBackground(Void... ignore) {

            // establish SSL trust (insecure for demo)
            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (java.security.KeyManagementException e) {
                e.printStackTrace();
            } catch (java.security.NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                // Default HTTPS connection option values
                URL watsonURL = new URL(getString(R.string.user_watson_server_instance));
                int timeoutConnection = 30000;
                connection = (HttpsURLConnection) watsonURL.openConnection();
                connection.setSSLSocketFactory(context.getSocketFactory());
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(timeoutConnection);
                connection.setReadTimeout(timeoutConnection);

                // Watson specific HTTP headers
                connection.setRequestProperty("X-SyncTimeout", "30");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Basic " + getEncodedValues(getString(R.string.user_id), getString(R.string.user_password)));
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Cache-Control", "no-cache");

                OutputStream out = connection.getOutputStream();
                String query = "{\"question\": {\"questionText\": \"" + mWatsonQueryString + "\"}}";
                out.write(query.getBytes());
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            int responseCode;
            try {
                if (connection != null) {
                    responseCode = connection.getResponseCode();
                    Log.i(mLogTag, "Server Response Code: " + Integer.toString(responseCode));

                    switch(responseCode) {
                        case 200:
                            // successful HTTP response state
                            InputStream input = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                            String line;
                            StringBuilder response = new StringBuilder();
                            while((line = reader.readLine()) != null) {
                                response.append(line);
                                response.append('\r');
                            }
                            reader.close();

                            Log.i(mLogTag, "Watson Output: " + response.toString());
                            jsonData = response.toString();

                            break;
                        default:
                            // Do Stuff
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // received data, deliver JSON to PostExecute
            if(jsonData != null) {
                return jsonData;
            }

            // else, hit HTTP error, handle in PostExecute by doing null check
            return null;
        }



        @Override
        protected void onPostExecute(String json) {
            mIsQuerying = false;
            dialog.dismiss();

            try {
                if(json != null) {
                    JSONObject watsonResponse = new JSONObject(json);
                    JSONObject question = watsonResponse.getJSONObject("question");
                    JSONArray evidenceArray = question.getJSONArray("evidencelist");
                    JSONObject mostLikelyValue = evidenceArray.getJSONObject(0);
                    mWatsonAnswerString = mostLikelyValue.get("text").toString();
                    Log.i("ANSWER TEXT", mWatsonAnswerString);
                    //TextView textView = (TextView) getActivity().findViewById(R.id.watson_answer_text);
                    //textView.setText(mWatsonAnswerString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // No valid answer
                //printTryDifferentQuestion();
            }

            Intent watsonResultsPage = new Intent(getActivity(), WatsonResultsPage.class);
            watsonResultsPage.putExtra("result", mWatsonAnswerString);
            startActivity(watsonResultsPage);
        }

        /*
         *  Accepts all HTTPS certs. Do NOT use in production!!!
         */
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
    }

    /*private void printTryDifferentQuestion() {
        TextView textView = (TextView) getActivity().findViewById(R.id.watson_answer_text);
        textView.setText("Please try a different question.");
        textView.setVisibility(View.VISIBLE);
    }*/

    private String getEncodedValues(String user_id, String user_password) {
        String textToEncode = user_id + ":" + user_password;
        byte[] data = null;
        try {
            data = textToEncode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }

}