package com.example.hiredswipe.candidate

class RecruiterChatObject {

    var name: String? = ""
    var matched: List<String>? = emptyList()
    var uid: String? = ""

    constructor() {}

    constructor(name: String?, matched: List<String>?, uid: String?) {
        this.name = name
        this.matched = matched
        this.uid = uid
    }
}