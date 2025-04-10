package com.sakthi.tickly

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        DataStoreManager(
            get()
        )
    }

    viewModel {
        MainViewModel(
            get()
        )
    }

}