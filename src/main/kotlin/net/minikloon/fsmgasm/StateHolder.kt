package net.minikloon.fsmgasm

abstract class StateHolder(states: Collection<State> = emptyList()) : State(), Iterable<State> {
    protected val states = states.toMutableList()

    override var frozen: Boolean = false
        set(value) {
            states.forEach { it.frozen = value }
            field = value
        }

    fun add(state: State) {
        states.add(state)
    }

    fun addAll(newStates: Collection<State>) {
        states.addAll(newStates)
    }

    override fun iterator(): Iterator<State> {
        return states.iterator()
    }
}