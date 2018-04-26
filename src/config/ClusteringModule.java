package config;

import io.log.Log;
import io.log.TextLog;
import kde.kernel.GaussianKernel;
import kde.kernel.Kernel;

import com.google.inject.AbstractModule;

public class ClusteringModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Log.class).to(TextLog.class);
    bind(Kernel.class).to(GaussianKernel.class);
  }

}
