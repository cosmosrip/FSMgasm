package net.minikloon.fsmgasm

import net.minikloon.kloggs.logger
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

private val logger = logger<State>()

abstract class State {
    /** Whether the state has been started. */
    var started = false
        private set

    /** Whether the state has been ended. */
    var ended = false
        private set

    /** Whether the state is currently in the process of updating. */
    private var updating = false

    /** Whether the state has been frozen. */
    open var frozen = false

    /** The length this duration should last. */
    abstract val length: Duration

    /** The remaining time left in this state. */
    val remaining: Duration
        get() {
            val sinceStart = JavaDuration.between(
                startedAt,
                Instant.now()
            ).toKotlinDuration()
            val remaining = length - sinceStart

            return if(remaining.isNegative()) Duration.ZERO else remaining
        }

    /** When the [State] started. */
    private lateinit var startedAt: Instant

    private val lock = Any()

    /**
     * Starts the [State].
     */
    open fun start() {
        synchronized(lock) {
            if(started || ended) {
                return
            }

            started = true
        }

        startedAt = Instant.now()

        try {
            onStart()
        } catch(ex: Exception) {
            logger.error(ex) {
                "Exception during ${javaClass.name} start"
            }
        }
    }

    /**
     * Dispatched when the [State] has been started.
     */
    protected open fun onStart() { }

    /**
     * Updates the [State].
     */
    open fun update() {
        synchronized(lock) {
            if(!started || ended || updating) {
                return
            }
            updating = true
        }

        try {
            onUpdate()
        } catch(ex: Exception) {
            logger.error(ex) {
                "Exception during ${javaClass.name} update"
            }
        }

        updating = false
    }

    /**
     * Dispatched when the [State] is being updated.
     */
    protected open fun onUpdate() { }

    /**
     * Ends the [State].
     */
    open fun end() {
        synchronized(lock) {
            if(!started || ended) {
                return
            }
            ended = true
        }

        try {
            onEnd()
        } catch(ex: Exception) {
            logger.error(ex) {
                "Exception during ${javaClass.name} end"
            }
        }
    }

    /**
     * Dispatched when the [State] has ended.
     */
    protected open fun onEnd() { }

    /**
     * Retrieve if the state is ready to be ended.
     * @return if the state should end.
     */
    open fun isReadyToEnd(): Boolean {
        return ended
    }
}