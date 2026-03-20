package com.incedo.personalhealth.integration.samsunghealth

import android.app.Activity
import android.content.Context
import com.incedo.personalhealth.core.health.HealthDataGateway
import com.incedo.personalhealth.core.health.HealthReadRequest
import com.incedo.personalhealth.core.health.HealthRecord
import com.samsung.android.sdk.health.data.HealthDataService
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission

class SamsungHealthGateway(
    private val context: Context
) : HealthDataGateway {
    private val store by lazy { HealthDataService.getStore(context) }

    fun availability(): SamsungHealthSdkAvailability = currentSamsungHealthAvailability(context)

    fun requiredReadPermissions(): Set<Permission> = samsungReadableDataTypes.mapTo(linkedSetOf()) { dataType ->
        Permission.of(dataType, AccessType.READ)
    }

    suspend fun hasRequiredPermissions(): Boolean {
        val required = requiredReadPermissions()
        return store.getGrantedPermissions(required).containsAll(required)
    }

    suspend fun requestReadPermissions(activity: Activity): Boolean {
        val required = requiredReadPermissions()
        return store.requestPermissions(required, activity).containsAll(required)
    }

    override suspend fun readRecords(request: HealthReadRequest): List<HealthRecord> {
        if (!availability().isReady || !hasRequiredPermissions()) return emptyList()

        return samsungRecordReaders.flatMap { reader ->
            if (reader.supports(request.metrics)) reader.read(store, request) else emptyList()
        }.sortedByDescending { it.endEpochMillis }
            .take(request.limit)
    }
}
