package com.gmail.kenzhang0.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.gmail.kenzhang0.AppExecutors
import com.gmail.kenzhang0.ENDPOINT
import com.gmail.kenzhang0.api.QueryListConverter
import com.gmail.kenzhang0.api.WeatherService
import com.gmail.kenzhang0.repository.CityRepo
import com.gmail.kenzhang0.repository.WeatherRepo
import com.gmail.kenzhang0.vo.Query
import com.securepreferences.SecurePreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val CITY_LIST_FILE_NAME = "city.list.min.json"

val ApiModule: Module = module {
    //Network
    single { provideMoshi() }
    single { provideLoggingInterceptor() }
    single { provideHttpUrl(ENDPOINT.HOST) }
    single { provideApiClient(get()) }
    single { provideRetrofit(get(), get(), get()) }

    //Executors
    single { provideAppExecutors() }

    //Services and Repos
    single { provideWeatherService(get()) }
    single { provideWeatherRepo(get(), get()) }
    single { provideCityRepo(androidApplication(), CITY_LIST_FILE_NAME) }

    //Preferences
    single { provideSharedPreferences(androidApplication()) }
    single { provideRxSharedPreferences(get()) }
    single { provideQueriesPreference(get(), get()) }
}

internal fun provideRetrofit(
    baseUrl: HttpUrl, client: OkHttpClient,
    moshi: Moshi
): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
}

private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val loggingInterceptor = HttpLoggingInterceptor { message -> Log.v("OkHttp", message) }
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return loggingInterceptor
}

fun provideApiClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}

internal fun provideMoshi(): Moshi {
    return Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

internal fun provideWeatherService(retrofit: Retrofit): WeatherService {
    return retrofit.create(WeatherService::class.java)
}

internal fun provideWeatherRepo(
    appExecutors: AppExecutors,
    weatherService: WeatherService
): WeatherRepo {
    return WeatherRepo(appExecutors, weatherService)
}

fun provideHttpUrl(url: String): HttpUrl {
    return HttpUrl.parse(url)!!
}

fun provideCityRepo(context: Context, fileName: String): CityRepo {
    return CityRepo(context, fileName)
}

fun provideAppExecutors(): AppExecutors {
    return AppExecutors()
}

internal fun provideSharedPreferences(app: Application): SharedPreferences {
    return SecurePreferences(app, "", "Weather")
}

internal fun provideRxSharedPreferences(prefs: SharedPreferences): RxSharedPreferences {
    return RxSharedPreferences.create(prefs)
}

fun provideQueriesPreference(prefs: RxSharedPreferences, moshi: Moshi): Preference<List<Query>> {
    return prefs.getObject("queryHistories", listOf(), QueryListConverter(moshi))
}