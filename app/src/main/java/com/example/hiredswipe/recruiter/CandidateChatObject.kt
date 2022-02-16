package com.example.hiredswipe.recruiter

class CandidateChatObject {

    var firstName: String? = ""
    var lastName: String? = ""
    var matched: List<String>? = emptyList()
    var uid: String? = ""

    constructor() {}

    constructor(firstName: String?, lastName: String?, matched: List<String>?, uid: String?) {
        this.firstName = firstName
        this.lastName = lastName
        this.matched = matched
        this.uid = uid
    }
}