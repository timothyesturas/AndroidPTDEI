package com.it.ptdei.menu.returns.ui


import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it.ptdei.menu.returns.UserIpDataModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateReportDialog(
    context: Context,

    fromDate: String,
    toDate: String,
    customerCode: String,
    customerBranch: String,
    returnType: String,

    returnTypes: List<String>,

    expanded: Boolean,
    selectedPc: UserIpDataModel,
    ipList: List<UserIpDataModel>,

    onFromDateChange: (String) -> Unit,
    onToDateChange: (String) -> Unit,
    onCustomerCodeChange: (String) -> Unit,
    onCustomerBranchChange: (String) -> Unit,
    onReturnTypeChange: (String) -> Unit,

    onExpandedChange: (Boolean) -> Unit,
    onPcSelected: (UserIpDataModel) -> Unit,

    onGenerate: () -> Unit,
    onDismiss: () -> Unit,
) {

    val isFormValid =
        fromDate.isNotBlank() && toDate.isNotBlank() && customerCode.isNotBlank() && customerBranch.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Generate Report")
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ---------------------------------------------------------
                // DATE RANGE
                // ---------------------------------------------------------

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Date Range", style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        val calendar = Calendar.getInstance()

                        // FROM DATE
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {

                            OutlinedTextField(
                                value = fromDate,
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text("From Date")
                                },
                                trailingIcon = {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select From Date"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {

                                        DatePickerDialog(
                                            context,
                                            { _, year, month, day ->

                                                onFromDateChange(
                                                    String.format(
                                                        "%02d/%02d/%04d", month + 1, day, year
                                                    )
                                                )
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    })
                        }

                        // TO DATE
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {

                            OutlinedTextField(
                                value = toDate,
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text("To Date")
                                },
                                trailingIcon = {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select To Date"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {

                                        DatePickerDialog(
                                            context,
                                            { _, year, month, day ->

                                                onToDateChange(
                                                    String.format(
                                                        "%02d/%02d/%04d", month + 1, day, year
                                                    )
                                                )
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    })
                        }
                    }
                }

                // ---------------------------------------------------------
                // CUSTOMER
                // ---------------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = customerCode, onValueChange = onCustomerCodeChange, label = {
                            Text("Customer Code:")
                        }, isError = customerCode.isBlank(), modifier = Modifier.weight(1f)
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    OutlinedTextField(
                        value = customerBranch, onValueChange = onCustomerBranchChange, label = {
                            Text("Branch Code:")
                        }, isError = customerBranch.isBlank(), modifier = Modifier.weight(1f)
                    )
                }

                // ---------------------------------------------------------
                // RETURN TYPE
                // ---------------------------------------------------------

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    returnTypes.forEachIndexed { index, type ->

                        SegmentedButton(
                            selected = returnType == type,

                            onClick = {
                                onReturnTypeChange(type)
                            },

                            shape = SegmentedButtonDefaults.itemShape(
                                index = index, count = returnTypes.size
                            ),

                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = Color(0xFF1976D2),
                                activeContentColor = Color.White,
                                inactiveContainerColor = Color(0xFFF5F5F5),
                                inactiveContentColor = Color.Black
                            )
                        ) {

                            Text(
                                text = when (type) {
                                    "NOT DELIVERED" -> "Cancelled\nNot Delivered"

                                    "DELIVERED" -> "RTS\nDelivered"

                                    else -> "Reverse\nAwaiting Return"
                                }, fontSize = 10.sp
                            )
                        }
                    }
                }

                // ---------------------------------------------------------
                // SELECT PC
                // ---------------------------------------------------------

                ExposedDropdownMenuBox(
                    expanded = expanded,

                    onExpandedChange = {
                        onExpandedChange(!expanded)
                    }) {

                    OutlinedTextField(
                        value = selectedPc.name, onValueChange = {}, readOnly = true,

                        label = {
                            Text("Select PC")
                        },

                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },

                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable, enabled = true
                            )
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,

                        onDismissRequest = {
                            onExpandedChange(false)
                        }) {

                        ipList.forEach { pc ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${pc.name} (${pc.userIp} : ${pc.userPort})"
                                    )
                                },

                                onClick = {
                                    onPcSelected(pc)
                                    onExpandedChange(false)
                                })
                        }
                    }
                }
            }
        },

        // -------------------------------------------------------------
        // GENERATE
        // -------------------------------------------------------------

        confirmButton = {

            Button(
                onClick = onGenerate, enabled = isFormValid
            ) {
                Text("Generate")
            }
        },

        // -------------------------------------------------------------
        // CANCEL
        // -------------------------------------------------------------

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        })
}