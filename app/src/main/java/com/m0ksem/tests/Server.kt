package com.m0ksem.tests

object Server {
    var serverIP = "192.168.0.102"//"192.168.0.100"
    var serverPort = "3000"

    var addTestURL = "http://$serverIP:$serverPort/database/tests/add"
    var getAllTestURL = "http://$serverIP:$serverPort/database/tests/getAll"
    var editTestURL = "http://$serverIP:$serverPort/database/tests/edit"
    var deleteTestURL = "http://$serverIP:$serverPort/database/tests/delete"

    var registerUrl = "http://$serverIP:$serverPort/register"
    var loginUrl = "http://$serverIP:$serverPort/login"
}