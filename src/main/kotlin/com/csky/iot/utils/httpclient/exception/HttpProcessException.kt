package com.csky.iot.utils.httpclient.exception


class HttpProcessException : Exception {

    constructor(e: Exception) : super(e)

    /**
     * @param msg
     */
    constructor(msg: String) : super(msg)

    /**
     * @param message
     * *
     * @param e
     */
    constructor(message: String, e: Exception) : super(message, e)

}
