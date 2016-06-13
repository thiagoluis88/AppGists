package br.com.thiagoluis.appgists;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import br.com.thiagoluis.appgists.model.Gist;
import br.com.thiagoluis.appgists.net.CustomCallback;
import br.com.thiagoluis.appgists.net.RestServices;
import retrofit.client.Response;

/**
 * Created by thiagoluis on 6/13/16.
 */
@RunWith(AndroidJUnit4.class)
public class AppGistsTest extends InstrumentationTestCase{

    private List<Gist> receivedData;
    private CountDownLatch lock = new CountDownLatch(1);

    @Test
    public void test30ItemsPerPage() throws Exception {

        RestServices.getServices().getGists(0, new CustomCallback<List<Gist>>(getInstrumentation().getContext()) {
            @Override
            public void onError(ErrorResponse response) {
                Log.e("AppGistsTest", response.getMessage());
            }

            @Override
            public void onSuccess(List<Gist> gists, Response response) {
                receivedData = gists;
                lock.countDown();
            }
        });

        lock.await(2000, TimeUnit.MILLISECONDS);

        assertNotNull(receivedData);
        Assert.assertEquals(30, receivedData.size());
    }
}
