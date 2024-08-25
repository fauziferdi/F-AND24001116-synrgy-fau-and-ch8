package com.example.fauzi_chalange_chapter6.data.datasource.remote

import com.example.fauzi_chalange_chapter6.data.datasource.remote.retrofit.ReqresService
import com.example.fauzi_chalange_chapter6.data.datasource.remote.retrofit.model.req.LoginBody
import com.example.fauzi_chalange_chapter6.data.datasource.remote.retrofit.model.res.LoginResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class AuthRemoteDataSourceImplTesting {

        // Given
        private val reqresService = mock<ReqresService>()
        private val dataSource = AuthRemoteDataSourceImpl(
            reqresService = reqresService,
        )

        @Test
        fun loginHardCodeTest() = runTest {
            // given
            val username = "fauzi"
            val password = "12345"

            // when
            val expected = "dashdy23622g2ysy2w27s288s282"
            val actual = dataSource.login(username, password)

            // then
            Assert.assertEquals(expected, actual)
        }

        @Test
        fun loginReqresTest() = runTest {
            // given
            val username: String = "username"
            val password: String = "password"
            val loginBody = LoginBody(username, password)
            val loginResponse = LoginResponse("id", "token")

            // when
            whenever(reqresService.login(loginBody)).thenReturn(loginResponse)
            val expected = "token"
            val actual = dataSource.login(username, password)

            // then
            Assert.assertEquals(expected, actual)
        }
}