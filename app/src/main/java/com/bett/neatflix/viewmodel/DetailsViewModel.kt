package com.bett.neatflix.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bett.neatflix.data.remote.response.WatchProvider
import com.bett.neatflix.data.repository.FilmRepository
import com.bett.neatflix.model.Cast
import com.bett.neatflix.model.Film
import com.bett.neatflix.util.FilmType
import com.bett.neatflix.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(val repository: FilmRepository) : ViewModel() {
    private var _similarFilms = mutableStateOf<Flow<PagingData<Film>>>(emptyFlow())
    val similarMovies: State<Flow<PagingData<Film>>> = _similarFilms

    private var _filmCast = mutableStateOf<List<Cast>>(emptyList())
    val filmCast: State<List<Cast>> = _filmCast

    private var _watchProviders = mutableStateOf<WatchProvider?>(null)
    val watchProviders: MutableState<WatchProvider?> = _watchProviders

    fun getSimilarFilms(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            repository.getSimilarFilms(filmId, filmType).also {
                _similarFilms.value = it
            }.cachedIn(viewModelScope)
        }
    }

    fun getFilmCast(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            repository.getFilmCast(filmId = filmId, filmType).also {
                if (it is Resource.Success) {
                    _filmCast.value = it.data!!.castResult
                }
            }
        }
    }

    fun getWatchProviders(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            repository.getWatchProviders(filmType = filmType, filmId = filmId).also {
                if (it is Resource.Success) {
                    _watchProviders.value = it.data!!.results
                }
            }
        }
    }
}
