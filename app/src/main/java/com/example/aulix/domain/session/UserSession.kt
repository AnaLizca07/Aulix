package com.example.aulix.domain.session

import com.example.aulix.domain.model.User

object UserSession {
    var currentUser: User? = null
    fun login(user: User) { currentUser = user }
    fun logout() { currentUser = null }
    fun isLoggedIn(): Boolean = currentUser != null
}
