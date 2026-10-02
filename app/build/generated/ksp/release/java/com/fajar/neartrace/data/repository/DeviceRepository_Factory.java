package com.fajar.neartrace.data.repository;

import com.fajar.neartrace.data.ble.AndroidBleScanner;
import com.fajar.neartrace.data.local.DeviceDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DeviceRepository_Factory implements Factory<DeviceRepository> {
  private final Provider<AndroidBleScanner> scannerProvider;

  private final Provider<DeviceDao> daoProvider;

  public DeviceRepository_Factory(Provider<AndroidBleScanner> scannerProvider,
      Provider<DeviceDao> daoProvider) {
    this.scannerProvider = scannerProvider;
    this.daoProvider = daoProvider;
  }

  @Override
  public DeviceRepository get() {
    return newInstance(scannerProvider.get(), daoProvider.get());
  }

  public static DeviceRepository_Factory create(Provider<AndroidBleScanner> scannerProvider,
      Provider<DeviceDao> daoProvider) {
    return new DeviceRepository_Factory(scannerProvider, daoProvider);
  }

  public static DeviceRepository newInstance(AndroidBleScanner scanner, DeviceDao dao) {
    return new DeviceRepository(scanner, dao);
  }
}
