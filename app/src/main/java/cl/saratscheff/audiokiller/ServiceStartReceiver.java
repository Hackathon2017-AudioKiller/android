package cl.saratscheff.audiokiller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, FilesObservingService.class));
    }
}
