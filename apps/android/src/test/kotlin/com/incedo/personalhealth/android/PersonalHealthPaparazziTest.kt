package com.incedo.personalhealth.android

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.incedo.personalhealth.shared.PersonalHealthApp
import org.junit.Rule
import org.junit.Test

class PersonalHealthPaparazziTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun personalHealthAppRenders() {
        paparazzi.snapshot {
            PersonalHealthApp()
        }
    }
}
