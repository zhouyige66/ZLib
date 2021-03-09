package cn.roy.zlib.http.metrics

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Semaphore

/**
 * @Description: 统计分析器
 * @Author: Roy Z
 * @Date: 2021/03/09
 * @Version: v1.0
 */
class StatisticsAnalysis private constructor() {
    private val eventContainer: ConcurrentLinkedQueue<StatisticsEvent> =
            ConcurrentLinkedQueue()
    private val costEventContainer: ConcurrentLinkedQueue<RequestCostEvent> =
            ConcurrentLinkedQueue()
    private val statisticsLock = Semaphore(0)
    private val analysisLock = Object()
    private var isStatisticsStart = false
    private var isAnalysisStart = false
    private val statistician = Thread {
        while (true) {
            try {
                statisticsLock.acquire()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val poll = eventContainer.poll()
            if (poll != null) {
                // 模拟数据处理
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private val assayer = Thread {
        while (true) {
            synchronized(analysisLock) {
                val poll = costEventContainer.poll()
                if (poll != null) {
                    // 模拟数据处理
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                } else {
                    // 等待被唤醒
                    try {
                        analysisLock.wait()
                    } catch (e: InterruptedException) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    companion object {
        val instance: StatisticsAnalysis by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            StatisticsAnalysis()
        }
    }

    fun post(event: StatisticsEvent) {
        eventContainer.add(event)
        if (!isStatisticsStart) {
            isStatisticsStart = true
            statistician.start()
        }
        statisticsLock.release()
    }

    fun analysis(event: RequestCostEvent) {
        costEventContainer.add(event)
        if (!isAnalysisStart) {
            isAnalysisStart = true
            assayer.start()
        }
        synchronized(analysisLock) {
            analysisLock.notify()
        }
    }

}