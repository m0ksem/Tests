package com.example.m0ksem.imprtest

object Server {
    private const val serverIP =  "192.168.137.1"//"192.168.0.100"
    private const val serverPort = "3000"
    var addTestURL = "http://$serverIP:$serverPort/database/tests/add"
    var getAllTestURL = "http://$serverIP:$serverPort/database/tests/getAll"
    var editTestURL = "http://$serverIP:$serverPort/database/tests/edit"
    var deleteTestURL = "http://$serverIP:$serverPort/database/tests/delete"


    var registerUrl = "http://$serverIP:$serverPort/register"
    var loginUrl = "http://$serverIP:$serverPort/login"
}