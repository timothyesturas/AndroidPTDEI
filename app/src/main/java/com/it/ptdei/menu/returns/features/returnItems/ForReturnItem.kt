package com.it.ptdei.menu.returns.features.returnItems

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

//Used @Parcelize para hindi magcrush kapag inopen yung image or pdf

/*
plugins {
    id 'kotlin-parcelize'
}
 */
@Parcelize
data class ForReturnItem(
    val rtvNo: String,
    val itemNo: Int
) : Parcelable
