package com.example.agrinova.di
import androidx.lifecycle.ViewModel
import com.example.agrinova.ui.home.screens.ProfileViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {
    // Tus otros ViewModels...

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @MapKey
    annotation class ViewModelKey(val value: KClass<out ViewModel>)
}