package com.example.fauzi_chalange_chapter6.ui.listplayer


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.fauzi_chalange_chapter6.MainDispatcherRule
import com.example.fauzi_chalange_chapter6.domain.model.Player
import com.example.fauzi_chalange_chapter6.domain.repository.PlayerRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ListPlayerFragmentViewModelTesting {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mock<PlayerRepository>()
    private val viewModel = ListPlayerFragmentViewModel(repository)

    private val clubsObserver = mock<Observer<List<Player>>>()
    private val loadingObserver = mock<Observer<Boolean>>()
    private val errorObserver = mock<Observer<String>>()

    private val clubsCaptor = argumentCaptor<List<Player>>()
    private val loadingCaptor = argumentCaptor<Boolean>()
    private val errorCaptor = argumentCaptor<String>()

    @Test
    fun success_updatesClubs() = runTest {
        // Given
        val expectedPlayers = listOf(
            Player(image = "image1", nama = "Player 1"),
            Player(image = "image2", nama = "Player 2")
        )
        whenever(repository.fetchData()).thenReturn(expectedPlayers)

        // When
        viewModel.clubs.observeForever(clubsObserver)
        viewModel.loading.observeForever(loadingObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.retriveClubData()

        // Then
        verify(repository).fetchData()
        verify(loadingObserver, times(2)).onChanged(loadingCaptor.capture())
        verify(clubsObserver).onChanged(clubsCaptor.capture())
        Assert.assertEquals(listOf(true, false), loadingCaptor.allValues)
        Assert.assertEquals(expectedPlayers, clubsCaptor.firstValue)
        verify(errorObserver, times(0)).onChanged(errorCaptor.capture()) // Tidak ada error
    }

    @Test
    fun error_updatesError() = runTest {
        // Given
        val expectedErrorMessage = "Network error"
        whenever(repository.fetchData()).thenThrow(RuntimeException(expectedErrorMessage))

        // When
        viewModel.clubs.observeForever(clubsObserver)
        viewModel.loading.observeForever(loadingObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.retriveClubData()

        // Then
        verify(repository).fetchData()
        verify(loadingObserver, times(2)).onChanged(loadingCaptor.capture())
        verify(errorObserver).onChanged(errorCaptor.capture())
        Assert.assertEquals(listOf(true, false), loadingCaptor.allValues)
        verify(clubsObserver, times(0)).onChanged(clubsCaptor.capture()) // Tidak ada perubahan pada clubs
        Assert.assertEquals(expectedErrorMessage, errorCaptor.firstValue)
    }
}