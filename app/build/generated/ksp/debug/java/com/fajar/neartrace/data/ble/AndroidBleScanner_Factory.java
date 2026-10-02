package com.fajar.neartrace.data.ble;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AndroidBleScanner_Factory implements Factory<AndroidBleScanner> {
  private final Provider<Context> contextProvider;

  public AndroidBleScanner_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AndroidBleScanner get() {
    return newInstance(contextProvider.get());
  }

  public static AndroidBleScanner_Factory create(Provider<Context> contextProvider) {
    return new AndroidBleScanner_Factory(contextProvider);
  }

  public static AndroidBleScanner newInstance(Context context) {
    return new AndroidBleScanner(context);
  }
}
