package fi.timo.synchronousvolleysample;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    private void loadHtmlInToTextViewSynchronously(final WebView webView) {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Object[] responseHolder = new Object[1];

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://google.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseHolder[0] = response;
                countDownLatch.countDown();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseHolder[0] = error;
                countDownLatch.countDown();
            }
        });
        queue.add(stringRequest);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (responseHolder[0] instanceof VolleyError) {
            final VolleyError volleyError = (VolleyError) responseHolder[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadData(volleyError.getMessage(), "text", "UTF-8");
                }
            });
        } else if (responseHolder[0] instanceof String) {
            final String response = (String) responseHolder[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadData(response, "text/html", "UTF-8");
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WebView webView = findViewById(R.id.webview);
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                loadHtmlInToTextViewSynchronously(webView);
                return null;
            }
        }.execute();
    }
}
