package com.example.fauzi_chalange_chapter6.data.datasource.remote

import com.example.fauzi_chalange_chapter6.data.datasource.remote.retrofit.ApiPlayerService
import com.example.fauzi_chalange_chapter6.data.datasource.remote.retrofit.model.res.ApiPlayerResponse
import com.example.fauzi_chalange_chapter6.data.datasource.remote.retrofit.model.res.PlayerResponse
import com.example.fauzi_chalange_chapter6.domain.model.Player
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

import java.io.IOException

class PlayerRemoteDataSourceImplTesting {

    private val apiPlayerService = mock<ApiPlayerService>()
    private val dataSource = PlayerRemoteDataSourceImpl(apiPlayerService)

    @Test
    fun fetchDataMappedData() = runTest {
        // Given
        val expectedPlayers = listOf(
            Player(id = 1, nama = "Player 1", image = "photo_url_1"),
            Player(id = 2, nama = "Player 2", image = "photo_url_2")
        )
        val apiPlayerResponse = ApiPlayerResponse(
            data = listOf(
                PlayerResponse(player_id = 1, player_name = "Player 1", player_photo = "photo_url_1"),
                PlayerResponse(player_id = 2, player_name = "Player 2", player_photo = "photo_url_2")
            )
        )

        // When
        whenever(apiPlayerService.getPlayersData()).thenReturn(apiPlayerResponse)
        val actualPlayers = dataSource.fetchData()

        // Then
        Assert.assertEquals(expectedPlayers, actualPlayers)
    }

    @Test
    fun fetchDataReturnsEmptyList() = runTest {
        // Given
        val apiPlayerResponse = ApiPlayerResponse(data = emptyList())
        whenever(apiPlayerService.getPlayersData()).thenReturn(apiPlayerResponse)

        // When
        val actualPlayers = dataSource.fetchData()

        // Then
        Assert.assertTrue(actualPlayers.isEmpty())
    }

}