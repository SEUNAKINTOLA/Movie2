/**
 * Created by AKINTOLA OLUWASEUN on 4/24/2017.
 */

package oluwaseun.akintola.movies;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity{
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.settings);
    }
}
