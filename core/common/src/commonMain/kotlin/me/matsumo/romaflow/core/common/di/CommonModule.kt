package me.matsumo.romaflow.core.common.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.matsumo.romaflow.core.common.formatter
import org.koin.dsl.module

// DI の構成元で Dispatcher を登録し、利用側へ注入するため
@Suppress("InjectDispatcher")
val commonModule = module {
    single { formatter }
    single<CoroutineDispatcher> { Dispatchers.IO }
}
