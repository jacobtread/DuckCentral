package com.jacobtread.duck.utils

/**
 * SimpleResult Similar to the kotlin Result class
 * but this does not take a value for success cases
 * only a value for failure cases
 *
 * @constructor Create empty SimpleResult
 */
open class SimpleResult {

    /**
     * Success Represents a success
     *
     * @constructor Create empty Success
     */
    class Success : SimpleResult()

    /**
     * Failure Represents a failure will contain an exception
     * for the cause of this failure
     *
     * @property cause The exception that caused this failure
     * @constructor Create empty Failure
     */
    class Failure(val cause: Exception) : SimpleResult()


    /**
     * throwIfFailure Function for throwing the exception
     * provided to Failure if the result is a Failure
     */
    @Throws(Exception::class)
    fun throwIfFailure() {
        if (this is Failure) {
            throw this.cause
        }
    }
}
