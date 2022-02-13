package com.example.hiredswipe

data class Candidate(
    var firstName: String? = "",
    var lastName: String? = "",
    var swipedLeft: List<String>? = emptyList(),
    var swipedRight: List<String>? = emptyList(),
    var id: String? = ""
) { }