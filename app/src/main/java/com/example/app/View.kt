package com.example.app

class View {

    var date:String = ""
    var obNum:String = ""
    var time:String = ""
    var occurBk:String = ""
    var sign:String = ""
    var btn_save:String = ""
    var rec_id:String = ""


    constructor(date: String, amount: String, description: String, rec_id: String) {
        this.date = date
        this.obNum = amount
        this.time = description
        this.occurBk = description
        this.sign = description
        this.btn_save = description
        this.rec_id = rec_id
    }

    constructor()

}