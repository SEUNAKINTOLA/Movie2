package oluwaseun.akintola.movies.utilities;
/**
 * Created by AKINTOLA OLUWASEUN on 4/25/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;


public class InternetReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isConnected()){
            Toast.makeText(context, "Internet Connection off",
                    Toast.LENGTH_LONG).show();
        }

    }

    private boolean isConnected(){
        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
