package com.test.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import web.abort.AbortController
import web.abort.Abortable
import web.timers.test.awaitTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.milliseconds

class MyCancellationResponseLibraryTest {
    private fun runCancellationTest(
        block: suspend CoroutineScope.() -> Unit,
    ) = runTest {
        val collector = PromiseRejectionCollector()

        val dataJob = launch(block = block)

        launch {
            awaitTimeout(100.milliseconds)
            dataJob.cancel()
        }

        awaitTimeout(300.milliseconds)

        val rejectExceptions = collector.leave()

        assertEquals(1, rejectExceptions.size)

        assertEquals(
            "ABORT ABORT",
            rejectExceptions.single().message,
        )
    }

    private fun runLateCancellationTest(
        block: suspend () -> Any?,
    ) = runTest {
        var data: Result<Any?>? = null
        val dataJob = launch {
            data = toResult(block)
        }

        launch {
            awaitTimeout(300.milliseconds)
            dataJob.cancel()
        }

        awaitTimeout(400.milliseconds)

        assertNotNull(data)

        val rejectException = data.exceptionOrNull()
        assertNotNull(rejectException)
        assertEquals(
            "REQUEST TIMEOUT ERROR",
            rejectException.message,
        )
    }

    @Test
    fun testGetCancellableResponseOnlyWithOptions_default() =
        runCancellationTest {
            getCancellableResponseOnlyWithOptions()
        }

    @Test
    fun testGetCancellableResponseOnlyWithOptions_default_lateCancellation() =
        runLateCancellationTest {
            getCancellableResponseOnlyWithOptions()
        }

    @Test
    fun testGetCancellableResponseOnlyWithOptions_emptyOptions() =
        runCancellationTest {
            getCancellableResponseOnlyWithOptions(Abortable())
        }

    @Test
    fun testGetCancellableResponseOnlyWithOptions_emptyOptions_lateCancellation() =
        runLateCancellationTest {
            getCancellableResponseOnlyWithOptions(Abortable())
        }

    @Test
    fun testGetCancellableResponseOnlyWithOptions_customSignal() =
        runCancellationTest {
            val controller = AbortController()
            getCancellableResponseOnlyWithOptions(Abortable(signal = controller.signal))
        }

    @Test
    fun testGetCancellableResponseOnlyWithOptions_customSignal_lateCancellation() =
        runLateCancellationTest {
            val controller = AbortController()
            getCancellableResponseOnlyWithOptions(Abortable(signal = controller.signal))
        }
}
