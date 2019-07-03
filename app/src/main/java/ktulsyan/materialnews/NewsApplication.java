package ktulsyan.materialnews;

import android.app.Application;

import com.facebook.stetho.Stetho;

import ktulsyan.materialnews.modules.ApplicationModule;
import lombok.Getter;
import timber.log.Timber;

public class NewsApplication extends Application {
    @Getter
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {

        }
        Stetho.initializeWithDefaults(this);
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }


}
