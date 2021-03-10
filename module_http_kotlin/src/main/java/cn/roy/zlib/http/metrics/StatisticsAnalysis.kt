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
    private var statisticEventContainer: ConcurrentLinkedQueue<StatisticsEvent> =
            ConcurrentLinkedQueue()
    private val analysisEventContainer: ConcurrentLinkedQueue<AnalysisEvent> =
            ConcurrentLinkedQueue()
    private val statisticsLock = Semaphore(0)
    private val analysisLock = Object()
    private var isStatisticsStart = false
    private var isAnalysisStart = false
    private var isStop = false
    private val statistician = Thread {
        while (!isStop) {
            try {
                statisticsLock.acquire()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val poll = statisticEventContainer.poll()
            if (poll != null) {
                RequestMetrics.instance.statistics(poll)
            }
        }
    }
    private val assayer = Thread {
        while (!isStop) {
            synchronized(analysisLock) {
                val poll = analysisEventContainer.poll()
                if (poll != null) {
                    RequestMetrics.instance.analysisRequest(poll)
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

    fun statistics(event: StatisticsEvent) {
        if (isStop) {
            return
        }
        statisticEventContainer.add(event)
        if (!isStatisticsStart) {
            isStatisticsStart = true
            statistician.start()
        }
        // 释放一个许可
        statisticsLock.release()
    }

    fun analysis(event: AnalysisEvent) {
        if (isStop) {
            return
        }
        analysisEventContainer.add(event)
        if (!isAnalysisStart) {
            isAnalysisStart = true
            assayer.start()
        }
        // 通知线程进行处理
        synchronized(analysisLock) {
            analysisLock.notify()
        }
    }

    fun release() {
        isStop = true
        statisticEventContainer.clear()
        analysisEventContainer.clear()
    }

}