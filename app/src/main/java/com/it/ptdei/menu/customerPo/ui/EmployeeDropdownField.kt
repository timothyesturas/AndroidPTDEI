package com.it.ptdei.menu.customerPo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it.ptdei.menu.customerPo.features.getEmpList.EmployeeModel
import kotlinx.coroutines.delay

@Composable
fun EmployeeDropdownField(
    label: String,
    value: EmployeeModel?,
    suggestions: List<EmployeeModel>,
    onValueChange: (EmployeeModel?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                showDialog = true
            }
    ) {
        OutlinedTextField(
            value = value?.empName ?: "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = label,
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color(0xFF212121),
                disabledLabelColor = Color(0xFF757575),
                disabledBorderColor = Color(0xFFBDBDBD),
                disabledContainerColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontSize = 12.sp
            ),
        )
    }

    if (showDialog) {
        EmployeeSelectionDialog(
            employees = suggestions,
            selectedEmployee = value,
            onEmployeeSelected = { employee ->
                onValueChange(employee)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
private fun EmployeeSelectionDialog(
    employees: List<EmployeeModel>,
    selectedEmployee: EmployeeModel?,
    onEmployeeSelected: (EmployeeModel) -> Unit,
    onDismiss: () -> Unit
) {
    var searchText by remember {
        mutableStateOf("")
    }

    val focusRequester = remember {
        FocusRequester()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val filteredEmployees = remember(searchText, employees) {
        if (searchText.isBlank()) {
            employees
        } else {
            employees.filter { employee ->
                employee.empName?.contains(
                    searchText,
                    ignoreCase = true
                ) == true ||
                        employee.empNo?.contains(
                            searchText,
                            ignoreCase = true
                        ) == true
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Select Employee",
                fontWeight = FontWeight.Bold
            )
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // Search
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = {
                        Text("Search employee")
                    },
                    placeholder = {
                        Text("Name or employee number")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (filteredEmployees.isEmpty()) {

                    Text(
                        text = if (searchText.isBlank()) {
                            "No employees found."
                        } else {
                            "No matching employees found."
                        },
                        color = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            items = filteredEmployees,
                            key = { employee ->
                                employee.empNo
                                    ?: employee.empName
                                    ?: ""
                            }
                        ) { employee ->

                            TextButton(
                                onClick = {
                                    onEmployeeSelected(employee)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                ) {
                                    Text(
                                        text = employee.empName ?: "-",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF212121)
                                    )

                                    if (!employee.empNo.isNullOrBlank()) {
                                        Text(
                                            text = employee.empNo,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}