package dev.avadhut.wist.client.util

import kotlinx.coroutines.CancellationException
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConnectivityTest {

    @Test
    fun apiException_401_isNotConnectivity() {
        assertFalse(ApiException("nope", httpStatusCode = 401).isLikelyConnectivityFailure())
    }

    @Test
    fun apiException_403_isNotConnectivity() {
        assertFalse(ApiException("nope", httpStatusCode = 403).isLikelyConnectivityFailure())
    }

    @Test
    fun apiException_408_isConnectivity() {
        assertTrue(ApiException("timeout", httpStatusCode = 408).isLikelyConnectivityFailure())
    }

    @Test
    fun cancellation_isNotConnectivity() {
        assertFalse(CancellationException("cancelled").isLikelyConnectivityFailure())
    }

    @Test
    fun exception_typeName_containsTimeout_isConnectivity() {
        class SocketTimeoutExceptionFake : Exception()
        assertTrue(SocketTimeoutExceptionFake().isLikelyConnectivityFailure())
    }
}
