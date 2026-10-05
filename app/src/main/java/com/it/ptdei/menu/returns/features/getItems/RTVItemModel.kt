package com.it.ptdei.menu.returns.features.getItems

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class RTVItemModel(

    @SerializedName("CANCEL_TYPE")//
    val cancelType: String,

    @SerializedName("CUST_CD") //
    val custCd: String,

    @SerializedName("CUST_BR")//
    val custBr: String,

    @SerializedName("CUST_NAME")//
    val custName: String,

    @SerializedName("BRANCH_NAME")//
    val branchName: String,

    @SerializedName("PO_NO")//
    val poNo: String,

    @SerializedName("DR_REF_NO")//
    val drRefNo: String,

    @SerializedName("IMG_FILE")//
    val imgFiles: List<String>?,

    @SerializedName("REVERSE_IMAGE")//
    val revImages: List<String>?,


    @SerializedName("PROD_NAME")//
    val prodName: String?,

    @SerializedName("RTV_NO")//
    val rtvNo: String,

    @SerializedName("RTV_TYPE")//
    val rtvType: String?,

    @SerializedName("PROD_CD")//
    val prodCd: String,

    @SerializedName("QTY")//
    val qty: BigDecimal?,

    @SerializedName("UNIT_CD")//
    val unitCd: String?,

    @SerializedName("UNIT_PRICE")//
    val unitPrice: BigDecimal?,

    @SerializedName("ITEM_NO")
    val itemNo: Int,

    @SerializedName("SRET_NO")//
    val sretNo: String?,

    @SerializedName("SRET_ITEM_NO")//
    val sretItemNo: BigDecimal?,

//    @SerializedName("DISC")
//    val disc: String?,
//
//    @SerializedName("DISC_PRICE")
//    val discPrice: BigDecimal?,
//
//
//    @SerializedName("COLOR_CD")
//    val colorCd: String?,
//
//    @SerializedName("PRICE_UNIT_CD")
//    val priceUnitCd: String?,
//
//    @SerializedName("SIZE_CD")
//    val sizeCd: String?,
//
//    @SerializedName("SKU")
//    val sku: String?,
//
//    @SerializedName("QTY_PCS")
//    val qtyPcs: BigDecimal?,
//
//    @SerializedName("QTY_PCS_UOP")
//    val qtyPcsUop: BigDecimal?,
//
//    // S1–S10 (VARCHAR)
//    @SerializedName("S1") val s1: String?,
//    @SerializedName("S2") val s2: String?,
//    @SerializedName("S3") val s3: String?,
//    @SerializedName("S4") val s4: String?,
//    @SerializedName("S5") val s5: String?,
//    @SerializedName("S6") val s6: String?,
//    @SerializedName("S7") val s7: String?,
//    @SerializedName("S8") val s8: String?,
//    @SerializedName("S9") val s9: String?,
//    @SerializedName("S10") val s10: String?,
//
//    // Q1–Q10 (NUMBER)
//    @SerializedName("Q1") val q1: BigDecimal?,
//    @SerializedName("Q2") val q2: BigDecimal?,
//    @SerializedName("Q3") val q3: BigDecimal?,
//    @SerializedName("Q4") val q4: BigDecimal?,
//    @SerializedName("Q5") val q5: BigDecimal?,
//    @SerializedName("Q6") val q6: BigDecimal?,
//    @SerializedName("Q7") val q7: BigDecimal?,
//    @SerializedName("Q8") val q8: BigDecimal?,
//    @SerializedName("Q9") val q9: BigDecimal?,
//    @SerializedName("Q10") val q10: BigDecimal?,
//
//    @SerializedName("UNIT_PRICE_CURR_CD")
//    val unitPriceCurrCd: String?,
//
//    @SerializedName("UNIT_PRICE_CURR_COUNTRY_CD")
//    val unitPriceCurrCountryCd: String?,
//
//    @SerializedName("QTY_SRET_RECEIVED")
//    val qtySretReceived: BigDecimal?,
//
//    @SerializedName("SYNC_ROW_ID")
//    val syncRowId: String?, // ROWID → String
//
//    @SerializedName("LAST_UPDATE_DATE")
//    val lastUpdateDate: LocalDate?, // safer than Date for Gson
//
//    @SerializedName("LAST_EXTRACT_DATE")
//    val lastExtractDate: LocalDate?,
//
//    @SerializedName("TOTAL_BRK_QTY")
//    val totalBrkQty: BigDecimal?,
//
//    @SerializedName("BATCH_NO")
//    val batchNo: String?,
//
//    @SerializedName("LOT_NO")
//    val lotNo: String?,
//
//    @SerializedName("MFG_DATE")
//    val mfgDate: LocalDate?,
//
//    @SerializedName("XPRY_DATE")
//    val xpryDate: LocalDate?,
//
//    @SerializedName("INVOICE_ITEM_NO")
//    val invoiceItemNo: BigDecimal?
)
