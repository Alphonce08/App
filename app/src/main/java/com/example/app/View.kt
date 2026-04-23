package com.example.app

data class Occurrence(
    var rec_id: String? = "",
    var date: String? = "",
    var time: String? = "",
    var obNumber: String? = "",
    var occurence: String? = "",
    var sign: String? = "",
    var status: String = "pending" // ✅ ADD THIS

)