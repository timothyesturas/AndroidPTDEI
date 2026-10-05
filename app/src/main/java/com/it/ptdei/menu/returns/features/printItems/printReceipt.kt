package com.it.ptdei.menu.returns.features.printItems

//@SuppressLint("DefaultLocale")
//suspend fun printReceipt(
//    context: Context,
//    data: List<DataToPrint>,
//    user: String
//) = withContext(Dispatchers.IO) {
//
//
//    try {
//        if (printer == null) {
//            Log.e("PrinterHelper", "Printer not initialized!")
//            return@withContext
//        }
//
//        val formatCenter = PrnStrFormat().apply {
//            textSize = 35; ali = mapToAlignment(PrnAlignTypeEnum.ALIGN_CENTER)
//        }
//        val formatCenterSmaller = PrnStrFormat().apply {
//            textSize = 25; ali = mapToAlignment(PrnAlignTypeEnum.ALIGN_CENTER)
//        }
//        val formatCenterEnd = PrnStrFormat().apply {
//            textSize = 25; ali = mapToAlignment(PrnAlignTypeEnum.ALIGN_CENTER)
//        }
//        val formatLeft = PrnStrFormat().apply {
//            textSize = 30; ali = mapToAlignment(PrnAlignTypeEnum.ALIGN_LEFT)
//        }
//        val formatLeftSmaller = PrnStrFormat().apply {
//            textSize = 28; ali = mapToAlignment(PrnAlignTypeEnum.ALIGN_LEFT)
//        }
//        val formatLeftSmaller2 = PrnStrFormat().apply {
//            textSize = 22; ali = mapToAlignment(PrnAlignTypeEnum.ALIGN_LEFT)
//        }
//        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//
//        printer?.apply {
//
//            setPrintAppendString(
//                today,
//                formatLeftSmaller
//            )
//
//            setPrintAppendString(
//                "CANCELLED RETURNS",
//                formatCenter
//            )
//            setPrintAppendString(
//                user,
//                formatCenterSmaller
//            )
//            setPrintAppendString(
//                "==============================",
//                formatCenterSmaller
//            )
//
//
//            val grouped = data.groupBy {
//                Triple(it.trackingNo, it.sretNo, it.rtvNo)
//            }
//
//            var groupIndex = 1
//            var totalQty = 0
//
//            grouped.forEach { (key, items) ->
//
//                val (trackingNo, sretNo, rtvNo) = key
//
//                val groupQty = items.sumOf { it.qty }
//                totalQty += groupQty
//
//                setPrintAppendString(
//                    "No. $groupIndex ------------------------------------",
//                    formatCenter
//                )
//                setPrintAppendString(
//                    "TRACK: $trackingNo",
//                    formatLeftSmaller
//                )
//
//                setPrintAppendString(
//                    "SRET : $sretNo",
//                    formatLeftSmaller
//                )
//
//                setPrintAppendString(
//                    "RTV  : $rtvNo",
//                    formatLeftSmaller
//                )
//
//                items.forEach { item ->
//
//                    setPrintAppendString(
//                        "${PrinterHelper.trim(item.prodName, 25)} - QTY: ${item.qty}",
//                        formatLeftSmaller2
//                    )
//                }
//                groupIndex++
//
//            }
//
//            setPrintAppendString(
//                "==============================",
//                formatCenterSmaller
//            )
//
//            setPrintAppendString(
//                "TOTAL WAYBILL: ${grouped.size}",
//                formatLeftSmaller
//            )
//
//            setPrintAppendString(
//                "TOTAL QTY: $totalQty",
//                formatLeftSmaller
//            )
//
//            // =========================
//            // FOOTER
//            // =========================
//            setPrintAppendString(
//                "\n\n",
//                formatLeft
//            )
//            setPrintAppendString(
//                today,
//                formatLeftSmaller
//            )
//            setPrintAppendString(
//                "\n\n\n",
//                formatLeft
//            )
//            setPrintStart()
//
//        }
//
//    } catch (e: Exception) {
//        Log.e("PrinterHelper", "Printing error: ${e.message}", e)
//    }
//}
