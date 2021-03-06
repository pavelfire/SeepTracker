/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*
//https://stackoverflow.com/questions/50824330/how-to-pass-edittext-value-to-viewmodel-and-livedata-kotlin
/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    private var viewModelJob = Job()

    override fun onCleared(){
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var tonight = MutableLiveData<SleepNight?>()

    val nights = database.getAllNights()

    val nightsString = Transformations.map(nights){ nights ->
        formatNights(nights, application.resources)
    }

    val startButtonVisible = Transformations.map(tonight){
        null == it
    }

    //if tonight is a value, the stop should be visible
    val stopButtonVisible = Transformations.map(tonight){
        null != it
    }

    //the clear button should only be visible if there are nights to clear.
    val clearButtonVisible = Transformations.map(nights){
        it?.isNotEmpty()
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar(){
        _showSnackbarEvent.value = false
    }

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    fun doneNavigating(){
        _navigateToSleepQuality.value = null
    }

    init{
        initializeTonight()
    }

    private fun initializeTonight(){
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight?{
        return withContext(Dispatchers.IO){
            var night = database.getTonight()
            if(night?.endTimeMilli != night?.startTimeMilli){
                night = null
            }
            night
        }
    }

    fun onStartTracking(){
        Log.i("MyLog", "on start onStartTracking()")
        uiScope.launch {
            val newNight = SleepNight()

            insert(newNight)
            tonight.value = getTonightFromDatabase()
            Log.i("MyLog", "in launch onStartTracking()")
        }
        Log.i("MyLog", "on end in onStartTracking()")
    }

    private suspend fun insert(night: SleepNight){
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    fun onStopTracking(){
        Log.i("MyLog", "on start onStopTracking()")
        uiScope.launch{
            //In Kotlin, the return@label syntax is used for specifying which function among
            //several nested ones this statement returns from.
            //In this case, we are specifying to return from launch(),
            //not the lambda.
            val oldNight = tonight.value ?: return@launch

            //Update the night in the database to add the end time.
            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)

            _navigateToSleepQuality.value = oldNight
            Log.i("MyLog", "in launch onStopTracking()")
        }
        Log.i("MyLog", "on end onStopTracking()")
    }

    private suspend fun update(night: SleepNight){
        withContext(Dispatchers.IO){
            database.update(night)
        }
    }

    fun onClear(){
        uiScope.launch {
            //clear the database table
            clear()

            //and clear tonight since it's no longer in the database
            tonight.value = null

            _showSnackbarEvent.value = true
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

    private val _navigateToSleepDataQuality = MutableLiveData<Long>()
    val navigateToSleepDataQuality
        get() = _navigateToSleepDataQuality

    fun onSleepNightClicked(id: Long){
        _navigateToSleepDataQuality.value = id
    }
    fun onSleepDataQualityNavigated(){
        _navigateToSleepDataQuality.value = null
    }

    private var _rubLi = MutableLiveData<Long>()
    val rubLi: LiveData<Long>
        get() = _rubLi
    fun update(rubL: Long){
        _rubLi.value = rubL
    }

}

