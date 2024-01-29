package com.makuta.simplenotes

import android.text.Editable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import java.security.MessageDigest

object Utils {

    fun View.gone() {
        this.visibility = GONE
    }

    fun View.visible() {
        this.visibility = VISIBLE
    }

    fun String.toEditable(): Editable {
        return Editable.Factory().newEditable(this)
    }

    fun String.sha256(): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val bytes = messageDigest.digest(toByteArray())
        return buildString {
            for (byte in bytes) {
                append(String.format("%02x", byte))
            }
        }
    }

}