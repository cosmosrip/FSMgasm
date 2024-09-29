package net.minikloon.fsmgasm

import net.minikloon.kloggs.logger
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

private val log = logger<State>()
abstract class State {
    var started: Boolean = false
        private set
    
    var ended: Boolean = false
        private set

    open var frozen: Boolean = false // prevents the state from ending
    
    private lateinit var startInstant: Instant
    private val lock = Any()
    
    open fun start() {
        synchronized(lock) {
            if(started || ended)
                return
            started = true
        }
        
        startInstant = Instant.now()
        try {
            onStart()
        } catch(e: Throwable) {
            log.error(e) { "Exception during ${javaClass.name} start" }
        }
    }
    
    protected open fun onStart() { }
    
    private var updating = false
    open fun update() {
        synchronized(lock) {
            if(!started || ended || updating)
                return
            updating = true
        }
        
        if(isReadyToEnd() && !frozen) {
            end()
            return
        }
        
        try {
            onUpdate()
        } catch(e: Throwable) {
            log.error(e) { "Exception during ${javaClass.name} update" }
        }
        updating = false
    }
    
    open fun onUpdate() { }

    open fun end() {
        synchronized(lock) {
            if(!started || ended)
                return
            ended = true
        }
        
        try {
            onEnd()
        } catch(e: Throwable) {
            log.error(e) { "Exception during ${javaClass.name} end" }
        }
    }

    open fun isReadyToEnd() : Boolean {
        return ended || remainingDuration == Duration.ZERO
    }
    
    protected open fun onEnd() { }
    
    abstract val duration: Duration
    
    val remainingDuration: Duration
        get() {
            val sinceStart = java.time.Duration.between(startInstant, Instant.now())
            val remaining = duration - sinceStart.toKotlinDuration()
            return if(remaining.isNegative()) Duration.ZERO else remaining
        }
}