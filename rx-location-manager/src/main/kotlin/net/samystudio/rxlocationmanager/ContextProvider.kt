package net.samystudio.rxlocationmanager

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY

/**
 * https://firebase.googleblog.com/2016/12/how-does-firebase-initialize-on-android.html
 */
@RestrictTo(LIBRARY)
class ContextProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        applicationContext = context!!
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ) = null

    override fun insert(uri: Uri, values: ContentValues?) = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ) = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = 0

    override fun getType(uri: Uri) = null

    companion object {
        lateinit var applicationContext: Context
    }
}