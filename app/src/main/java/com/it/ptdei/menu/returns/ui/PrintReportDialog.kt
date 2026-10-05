package com.it.ptdei.menu.returns.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.it.ptdei.menu.returns.UserIpDataModel
import com.it.ptdei.menu.returns.features.getReport.GetReportResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintReportDialog(
    printRef: String,
    onPrintRefChange: (String) -> Unit,

    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,

    selectedPc: UserIpDataModel,
    ipList: List<UserIpDataModel>,
    onPcSelected: (UserIpDataModel) -> Unit,

    report: GetReportResponse?,

    onCheck: () -> Unit,
    onGenerate: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Generate Report")
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                // Print Reference Number
                OutlinedTextField(
                    value = printRef, onValueChange = onPrintRefChange, label = {
                        Text("Enter Print Ref No:")
                    }, isError = printRef.isBlank(), modifier = Modifier.fillMaxWidth()
                )

                // Select PC
                ExposedDropdownMenuBox(
                    expanded = expanded, onExpandedChange = {
                        onExpandedChange(!expanded)
                    }) {

                    OutlinedTextField(
                        value = selectedPc.name,
                        onValueChange = {},
                        readOnly = true,
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
                        expanded = expanded, onDismissRequest = {
                            onExpandedChange(false)
                        }) {

                        ipList.forEach { pc ->

                            DropdownMenuItem(text = {
                                Text(
                                    "${pc.name} (${pc.userIp} : ${pc.userPort})"
                                )
                            }, onClick = {
                                onPcSelected(pc)
                                onExpandedChange(false)
                            })
                        }
                    }
                }

                // Report information
                if (!report?.cancelType.isNullOrEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            OutlinedTextField(
                                value = report.cancelType,
                                onValueChange = {},
                                label = {
                                    Text("CANCEL TYPE")
                                },
                                modifier = Modifier.weight(1f),
                                readOnly = true
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            OutlinedTextField(
                                value = report.recordCount ?: "",
                                onValueChange = {},
                                label = {
                                    Text("RECORD COUNT")
                                },
                                modifier = Modifier.weight(1f),
                                readOnly = true
                            )
                        }

                        OutlinedTextField(
                            value = report.custName ?: "",
                            onValueChange = {},
                            label = {
                                Text("CUSTOMER")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = report.courier ?: "",
                            onValueChange = {},
                            label = {
                                Text("COURIER")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = report.createdBy ?: "",
                            onValueChange = {},
                            label = {
                                Text("PRINTED BY")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                    }
                }
            }
        },

        confirmButton = {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onCheck, enabled = printRef.isNotBlank()
                ) {
                    Text("Check")
                }

                Button(
                    onClick = onGenerate, enabled = selectedPc.name.isNotBlank()
                ) {
                    Text("Generate")
                }
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        })
}