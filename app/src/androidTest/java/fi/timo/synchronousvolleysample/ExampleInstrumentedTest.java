package fi.timo.synchronousvolleysample;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testSynchronousVolley() {
        //I'm running this in an instrumentation test, in real life you'd ofc obtain the context differently...
        final Context context = InstrumentationRegistry.getTargetContext();
        final RequestQueue queue = Volley.newRequestQueue(context);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Object[] responseHolder = new Object[1];

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://google.com", new Response.Listener<String>() {
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
            return;
            //TODO: Handle error...
        } else if (responseHolder[0] instanceof String) {
            final String response = (String) responseHolder[0];
            return;
            //TODO: Handle response...
        }

        throw new RuntimeException("Test failed we, never got a valid response...");
    }
}
