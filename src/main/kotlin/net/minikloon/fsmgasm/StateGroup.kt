package net.minikloon.fsmgasm

import kotlin.time.Duration

open class StateGroup(states: List<State> = emptyList()) : StateHolder(states) {
    override val length: Duration = states.maxByOrNull { it.length }
        ?.length
        ?: Duration.ZERO

    constructor(vararg states: State)
        : this(states.toList())

    override fun onStart() {
        states.forEach(State::start)
    }

    override fun onUpdate() {
        states.forEach(State::update)
        if(states.all { it.ended }) {
            end()
        }
    }

    override fun onEnd() {
        states.forEach(State::end)
    }

    override fun isReadyToEnd(): Boolean {
        return states.all(State::isReadyToEnd)
    }
}