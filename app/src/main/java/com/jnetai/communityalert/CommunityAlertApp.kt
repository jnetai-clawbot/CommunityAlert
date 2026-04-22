package com.jnetai.communityalert

import android.app.Application
import com.jnetai.communityalert.data.database.AlertDatabase

class CommunityAlertApp : Application() {
    val database: AlertDatabase by lazy { AlertDatabase.getDatabase(this) }
}