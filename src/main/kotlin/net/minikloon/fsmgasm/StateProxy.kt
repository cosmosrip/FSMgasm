package net.minikloon.fsmgasm

import kotlin.time.Duration

abstract class StateProxy(private val series: StateSeries) : State() {
    override val length: Duration = Duration.ZERO

    /**
     * Create the states associated with this proxy.
     * @return the created states.
     */
    abstract fun createStates(): Collection<State>

    override fun onStart() {
        series.addNext(createStates())
    }
}

fun stateProxy(series: StateSeries, create: () -> List<State>) = LambdaStateProxy(series, create)
class LambdaStateProxy(series: StateSeries, val create: () -> List<State>) : StateProxy(series) {
    override fun createStates() = create()
}