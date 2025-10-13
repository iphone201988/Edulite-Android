package com.edu.lite.base.location

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}