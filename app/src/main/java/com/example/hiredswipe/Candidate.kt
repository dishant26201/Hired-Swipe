package com.example.hiredswipe

data class Candidate(
    var email: String? = "",
    var firstName: String? = "",
    var lastName: String? = "",
    var location: String? = "",
    var swipedLeft: List<String>? = emptyList(),
    var swipedRight: List<String>? = emptyList(),
    var id: String? = "",
    var educationList: ArrayList<CandidateEducation> = arrayListOf(),
    var workExpList: ArrayList<CandidateWorkExp> = arrayListOf()
)