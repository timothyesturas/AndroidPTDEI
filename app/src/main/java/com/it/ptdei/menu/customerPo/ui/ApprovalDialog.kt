package com.it.ptdei.menu.customerPo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.it.ptdei.menu.customerPo.CustomerPOViewModel
import com.it.ptdei.menu.customerPo.features.getEmpList.EmployeeModel
import com.it.ptdei.menu.customerPo.features.getRecords.CustPoRecordModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalDialog(
    record: CustPoRecordModel,
    onDismiss: () -> Unit,
    onSave: (
        checkedBy: EmployeeModel?,
        packedBy: EmployeeModel?,
        assistedBy: EmployeeModel?
    ) -> Unit,
    scannedState: List<CustPoRecordModel>,
    viewModel: CustomerPOViewModel
) {
    var checkedBy by remember { mutableStateOf(record.checkedBy) }
    var packedBy by remember { mutableStateOf(record.packedBy) }
    var assistedBy by remember { mutableStateOf(record.assistedBy) }

    LaunchedEffect(Unit) {
        viewModel.getEmpList()
    }

    val employeeSuggestions by viewModel.empList.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Header
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Verify Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Text(
                        text = "Please verify the information before saving.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = Color(0xFFE5E5E5)
                )

                // Record details
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(
                        label = "CP No.",
                        value = record.cpNo
                    )

                    DetailRow(
                        label = "Order Date",
                        value = formatOrderDate(record.orderDate)
                    )

                    DetailRow(
                        label = "Received From",
                        value = record.rcvFrom?.empName
                    )

                    DetailRow(
                        label = "Business Unit",
                        value = record.busName
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = Color(0xFFE5E5E5)
                )

                // Checked By
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmployeeDropdownField(
                        label = "Checked By",
                        value = checkedBy,
                        suggestions = employeeSuggestions,
                        onValueChange = {
                            checkedBy = it
                        },
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { checkedBy = null }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Checked By",
                            tint = Color.Gray
                        )
                    }
                }

                // Packed By
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmployeeDropdownField(
                        label = "Packed By",
                        value = packedBy,
                        suggestions = employeeSuggestions,
                        onValueChange = {
                            packedBy = it
                        },
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { packedBy = null }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Packed By",
                            tint = Color.Gray
                        )
                    }
                }

                // Assisted By
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmployeeDropdownField(
                        label = "Assisted By",
                        value = assistedBy,
                        suggestions = employeeSuggestions,
                        onValueChange = {
                            assistedBy = it
                        },
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { assistedBy = null }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Assisted By",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSave(
                                checkedBy,
                                packedBy,
                                assistedBy
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

fun formatOrderDate(date: String?): String {
    if (date.isNullOrBlank()) {
        return "-"
    }

    return try {
        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.S",
            Locale.US
        )

        val outputFormat = SimpleDateFormat(
            "MMMM dd, yyyy",
            Locale.US
        )

        val parsedDate = inputFormat.parse(date)

        outputFormat.format(parsedDate!!)

    } catch (e: Exception) {
        date
    }
}