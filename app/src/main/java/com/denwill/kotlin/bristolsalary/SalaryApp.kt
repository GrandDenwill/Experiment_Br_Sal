package com.denwill.kotlin.bristolsalary
import android.app.Application
import android.text.format.DateUtils
import androidx.work.*
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Instant
import java.util.concurrent.TimeUnit
class SalaryApp :Application(){
    private val koinModule = module {
        single(named("appScope")) { CoroutineScope(SupervisorJob()) }
        single { SalaryDatabase.newInstance(androidContext()) }
        single {
            SalaryRepository(
                get<SalaryDatabase>().todoStore(),
                get(named("appScope")),
            )
        }
        viewModel {
            RosterMotor(
                get(),
                androidApplication(),
                get(named("appScope")),
            )
        }
        viewModel { (modelId: String) -> SingleModelMotor(get(), modelId) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(){
            androidLogger()
            androidContext(this@SalaryApp)
            modules(koinModule)
        }
    }
}