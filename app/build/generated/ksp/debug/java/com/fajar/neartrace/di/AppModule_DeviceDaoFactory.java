package com.fajar.neartrace.di;

import com.fajar.neartrace.data.local.DeviceDao;
import com.fajar.neartrace.data.local.NearTraceDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class AppModule_DeviceDaoFactory implements Factory<DeviceDao> {
  private final Provider<NearTraceDatabase> databaseProvider;

  public AppModule_DeviceDaoFactory(Provider<NearTraceDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DeviceDao get() {
    return deviceDao(databaseProvider.get());
  }

  public static AppModule_DeviceDaoFactory create(Provider<NearTraceDatabase> databaseProvider) {
    return new AppModule_DeviceDaoFactory(databaseProvider);
  }

  public static DeviceDao deviceDao(NearTraceDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.deviceDao(database));
  }
}
