package br.com.zup.edu.httpclient.bcb.dto

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType,
) {
}