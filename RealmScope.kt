package cn.scent.common.realm

import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class RealmScope private constructor():ContinuationInterceptor{
    override val key = ContinuationInterceptor

    private var handlerThread=HandlerThread("RealmThread")
    private lateinit var realmExt:RealmExt
    private lateinit var handler :Handler
    companion object{
        fun launch(
                   context: CoroutineContext = EmptyCoroutineContext,
                   start: CoroutineStart = CoroutineStart.DEFAULT,
                   block:suspend (RealmExt)->Unit): Job {
            val realmScope = RealmScope()
            realmScope.start()
            return GlobalScope.launch(context,start) {
                val job=launch(realmScope) {
                    block.invoke(realmScope.realmExt)
                }
                try {
                    job.join()
                } catch (e: Throwable) {
                    throw e
                } finally {
                    realmScope.stop()
                }
            }
        }
    }

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        return RealmContinuation(handler,continuation)
    }

    private fun start() {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        handler.post { realmExt = RealmExt.getDefaultInstance() }
    }

    private fun stop() {
        handler.post {
            realmExt.close()
            handlerThread.quitSafely()
        }
    }
    class RealmContinuation<T>(val handler: Handler,private val continuation:Continuation<T>):Continuation<T>{
        override val context: CoroutineContext = continuation.context

        override fun resumeWith(result: Result<T>) {
            handler.post {
                continuation.resumeWith(result)
            }
        }
    }
}