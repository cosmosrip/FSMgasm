package net.minikloon.fsmgasm

import net.minikloon.kloggs.logger
import kotlin.time.Duration

private val logger = logger<StateSeries>()
open class StateSeries(states: Collection<State> = emptyList()) : StateHolder(states) {
    protected var current = 0

    protected var skipping = false

    override val length = states.fold(Duration.ZERO) { curr, state -> curr + state.length }

    constructor(vararg states: State)
            : this(states.toList())

    fun addNext(state: State) {
        states.add(current + 1, state)
    }

    fun addNext(newStates: Collection<State>) {
        newStates.forEachIndexed { index, state ->
            states.add(current + (index + 1), state)
        }
    }

    fun skip() {
        skipping = true
    }

    override fun onStart() {
        if(states.isEmpty()) {
            end()
            return
        }

        states.getOrNull(current)
            ?.start()
            ?: logger.warn { "State doesn't exist at index $current whilst starting." }
    }

    override fun onUpdate() {
        val state = states.getOrNull(current)
            ?: return logger.warn { "State doesn't exist at index $current whilst updating." }

        state.update()

        if(state.isReadyToEnd() && !state.frozen || skipping) {
            if(skipping) {
                skipping = false
            }

            state.end()
            if(++current >= states.size) {
                end()
                return
            }

            states.getOrNull(current)
                ?.start()
                ?: logger.warn { "Incremented state doesn't exist at index $current whilst updating." }
        }
    }

    override fun onEnd() {
        if(current < states.size) {
            states.getOrNull(current)
                ?.end()
                ?: logger.warn { "State doesn't exist at index $current whilst ending." }
        }
    }

    override fun isReadyToEnd(): Boolean {
        return (current == states.size - 1) && states[current].isReadyToEnd()
    }
}