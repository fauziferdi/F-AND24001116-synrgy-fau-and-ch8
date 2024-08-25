package com.example.fauzi_chalange_chapter6.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.fauzi_chalange_chapter6.MainDispatcherRule
import com.example.fauzi_chalange_chapter6.domain.model.ReqresErrorResponse
import com.example.fauzi_chalange_chapter6.domain.usecase.LoginUseCase
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response

class LoginViewModelTesting {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Given
    private val loginUseCase = mock<LoginUseCase>()
    private val viewModel = LoginViewModel(
        loginUseCase = loginUseCase,
    )

    private val loadingObserver = mock<Observer<Boolean>>()
    private val successObserver = mock<Observer<Boolean>>()
    private val errorObserver = mock<Observer<String>>()

    private val loadingCaptor = argumentCaptor<Boolean>()
    private val successCaptor = argumentCaptor<Boolean>()
    private val errorCaptor = argumentCaptor<String>()

    @Test
    fun loginSuccess() = runTest {
        // given
        val username = "username"
        val password = "password"

        val loadingLiveData = viewModel.loading
        val successLiveData = viewModel.success
        val errorLiveData = viewModel.error

        loadingLiveData.observeForever(loadingObserver)
        successLiveData.observeForever(successObserver)
        errorLiveData.observeForever(errorObserver)

        // when
        whenever(loginUseCase.login(username, password)).thenReturn(Unit)
        viewModel.login(username, password)

        // verify
        verify(loadingObserver, times(2)).onChanged(loadingCaptor.capture())
        verify(successObserver).onChanged(successCaptor.capture())
        Assert.assertEquals(loadingCaptor.allValues, listOf(true, false))
        Assert.assertEquals(successCaptor.allValues, listOf(true))
    }

    @Test
    fun loginErrorHttp() = runTest {
        // given
        val username = "username"
        val password = "password"

        val loadingLiveData = viewModel.loading
        val successLiveData = viewModel.success
        val errorLiveData = viewModel.error

        loadingLiveData.observeForever(loadingObserver)
        successLiveData.observeForever(successObserver)
        errorLiveData.observeForever(errorObserver)

        // when
        val reqresErrorResponseJson = "{\"error\": \"terjadi kesalahan\"}"
        val httpException = HttpException(
            Response.error<ReqresErrorResponse>(
                400,
                ResponseBody.Companion.create(
                    "application/json".toMediaTypeOrNull(),
                    reqresErrorResponseJson,
                ),
            ),
        )
        whenever(loginUseCase.login(username, password)).thenThrow(httpException)
        viewModel.login(username, password)

        // verify
        verify(loadingObserver, times(2)).onChanged(loadingCaptor.capture())
        verify(errorObserver).onChanged(errorCaptor.capture())
        Assert.assertEquals(loadingCaptor.allValues, listOf(true, false))
        Assert.assertEquals(errorCaptor.allValues, listOf("terjadi kesalahan"))
    }

    @Test
    fun loginErrorGeneral() = runTest {
        // given
        val username = "username"
        val password = "password"

        val loadingLiveData = viewModel.loading
        val successLiveData = viewModel.success
        val errorLiveData = viewModel.error

        loadingLiveData.observeForever(loadingObserver)
        successLiveData.observeForever(successObserver)
        errorLiveData.observeForever(errorObserver)

        // when
        val throwable = UnsupportedOperationException("error")
        whenever(loginUseCase.login(username, password)).thenThrow(throwable)
        viewModel.login(username, password)

        // verify
        verify(loadingObserver, times(2)).onChanged(loadingCaptor.capture())
        verify(errorObserver).onChanged(errorCaptor.capture())
        Assert.assertEquals(loadingCaptor.allValues, listOf(true, false))
        Assert.assertEquals(errorCaptor.allValues, listOf("error"))
    }
}