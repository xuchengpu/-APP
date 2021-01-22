package com.cainiao.login

import com.cainiao.common.network.KtRetrofit
import com.cainiao.login.net.LoginService
import com.cainiao.login.repo.ILoginResource
import com.cainiao.login.repo.LoginRepository
import com.cniao5.common.utils.getBaseHost
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Created by 许成谱 on 2021/1/15 18:28.
 * qq:1550540124
 * 热爱生活每一天！
 */
val loginModules = module {

    //service retrofit
    single {
        get<KtRetrofit> { parametersOf(getBaseHost()) }.getService(LoginService::class.java)
    }
    // respository
    single {
        LoginRepository(get())
    } bind ILoginResource::class

    // viewModel
    viewModel { LoginViewModel(get()) }
}