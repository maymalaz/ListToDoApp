package com.example.listtodoapp.sharedPref

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SPApp@Inject constructor(@ApplicationContext val context: Context)  {

    var uid: String
        get() {
            return context.loadSp("uid") ?: ""
        }
        set(value) = context.saveSp("uid", value)
}