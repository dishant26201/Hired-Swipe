package com.example.hiredswipe

data class Recruiter(
    var name: String? = "",
    var swipedLeft: List<String>? = emptyList(),
    var swipedRight: List<String>? = emptyList(),
    var id: String? = ""
) { }