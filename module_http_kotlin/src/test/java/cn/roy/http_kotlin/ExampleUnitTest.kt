package cn.roy.http_kotlin

import cn.roy.zlib.http.metrics.StatisticsAnalysis.Companion.instance
import cn.roy.zlib.http.metrics.StatisticsEvent
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        for (i in 0..9) {
            val statisticsEvent = StatisticsEvent(UUID.randomUUID().toString())
            statisticsEvent.setType(StatisticsEvent.INIT)
            instance.post(statisticsEvent)
        }
    }
}