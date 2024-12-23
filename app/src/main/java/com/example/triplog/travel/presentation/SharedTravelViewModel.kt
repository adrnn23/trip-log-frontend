package com.example.triplog.travel.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplog.travel.data.TravelData

class SharedTravelViewModel : ViewModel() {
    var isTravelToEdit by mutableStateOf(false)
        private set
    var tempTravelDataToEdit by mutableStateOf(TravelData())

    fun setTempTravelDataEdit(data: TravelData) {
        tempTravelDataToEdit = data.copy()
    }

    fun clearTempTravelDataEdit(){
        tempTravelDataToEdit = TravelData()
    }

    fun setTravelEdit(toEdit: Boolean) {
        isTravelToEdit = toEdit
    }
}
