package com.fajar.neartrace.ui.screens;

import androidx.lifecycle.SavedStateHandle;
import com.fajar.neartrace.data.repository.DeviceRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class TrackingViewModel_Factory implements Factory<TrackingViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<DeviceRepository> repositoryProvider;

  public TrackingViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DeviceRepository> repositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public TrackingViewModel get() {
    return newInstance(savedStateHandleProvider.get(), repositoryProvider.get());
  }

  public static TrackingViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DeviceRepository> repositoryProvider) {
    return new TrackingViewModel_Factory(savedStateHandleProvider, repositoryProvider);
  }

  public static TrackingViewModel newInstance(SavedStateHandle savedStateHandle,
      DeviceRepository repository) {
    return new TrackingViewModel(savedStateHandle, repository);
  }
}
