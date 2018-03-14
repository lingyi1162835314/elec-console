package com.csky.iot.web

import javax.websocket.*

@ClientEndpoint
class WebsocketClient {
    @OnOpen
    fun onOpen(session: Session) {
        println("Connected to endpoint: " + session.basicRemote)
    }

    @OnMessage
    fun onMessage(message: String) {
        println(message)
    }

    @OnError
    fun onError(t: Throwable) {
        t.printStackTrace()
    }
}