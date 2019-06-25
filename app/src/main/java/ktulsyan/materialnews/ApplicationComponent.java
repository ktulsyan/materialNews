package ktulsyan.materialnews;

import javax.inject.Singleton;

import dagger.Component;
import ktulsyan.materialnews.modules.ApplicationModule;
import ktulsyan.materialnews.modules.DataModule;
import ktulsyan.materialnews.ui.MainActivity;

@Singleton
@Component(modules = {DataModule.class, ApplicationModule.class})
public interface ApplicationComponent {
    void inject (MainActivity activity);
}
