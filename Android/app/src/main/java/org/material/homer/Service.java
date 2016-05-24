package org.material.homer;

import android.util.Log;

import com.ecv.freetime.services.ServiceRunnable;
import com.ecv.freetime.services.ServicesManager;
import com.ecv.freetime.utils.GeneralUtils;
import com.ecv.freetime.webservices.RequestConfig;
import com.ecv.freetime.webservices.RequestResult;
import com.ecv.freetime.webservices.Requester;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by Pedro on 24/05/2016.
 */
public class Service implements ServiceRunnable {

    /**
     * Runnable
     *
     * @param servicesManager
     */
    @Override
    public void run(final ServicesManager servicesManager) {
        final Requester req = new Requester(servicesManager.getContext(), servicesManager);
        final RequestConfig config = new RequestConfig();
        config.connectionTimeout = 1000;
        config.readTimeout = 1000;
        GeneralUtils.SafetyLockedLooper.loop(new GeneralUtils.SafetyLockedLooper.Interception() {

            @Override
            public boolean onIntercept() {
                RequestResult result = req.prepare(Requester.RequestType.GET)
                                            .setUrl("http://192.168.10.100:3000/values")
                                            .setMode(Requester.RequestMode.IMMEDIATE)
                                            .request();
                if(result.isSuccessfullyResponse()) {
                    try {
                        MainActivity.State state = new MainActivity.State();

                        JSONObject obj = new JSONObject(result.getResponse());
                        JSONArray array = obj.getJSONArray("users");
                        state.renewD = BigDecimal.valueOf(obj.getDouble("renewd")).multiply(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_CEILING).floatValue();
                        state.batery = BigDecimal.valueOf(obj.getDouble("batt")).setScale(2, BigDecimal.ROUND_CEILING).floatValue();

                        for(int i=0; i<array.length(); i++) {
                            JSONObject room = array.getJSONObject(i);
                            state.rooms[i].use = BigDecimal.valueOf(room.getDouble("powerd")).multiply(BigDecimal.valueOf(1000)).setScale(2, BigDecimal.ROUND_CEILING).floatValue();
                            state.rooms[i].swt = (room.getString("sw").equals("false") || room.getString("sw").equals("0")) ? 0 : 1;
                        }

                        servicesManager.postMessage(MainActivity.COMMUNICATION_STATE, state);

                    } catch (Exception e) {

                        Log.d("LogTest", "Error: " + e.getMessage());
                        servicesManager.postMessage(MainActivity.COMMUNICATION_ERROR);
                        return true;
                    }
                } else {
                    servicesManager.postMessage(MainActivity.CONNECTION_ERROR);
                    return true;
                }
                try {
                    Thread.sleep(200);
                } catch(Exception e) {
                    return true;
                }
                return false;
            }
        });
    }
}
