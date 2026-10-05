package com.it.ptdei.menu.returns.features.returnItems

data class AttachmentItem(
    val index: Int,
    val filePath: String,
    val type: String,
    val url: String,
    val checked: Boolean = false
)