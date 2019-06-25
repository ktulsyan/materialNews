package ktulsyan.materialnews.modules;

import android.content.Context;
import android.net.ConnectivityManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ktulsyan.materialnews.NewsApplication;

@Module
public class ApplicationModule {
    private final NewsApplication app;

    public ApplicationModule(NewsApplication app) {
        this.app = app;
    }

    @Provides
    Context provideContext() {
        return app;
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityMgr() {
        return (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

}
