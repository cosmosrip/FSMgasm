package net.minikloon.fsmgasm

open class StateSwitch {
    /** The current state of this switch. */
    protected var state: State? = null

    /**
     * Change the state to a new one and end the old one.
     * @param next the new state.
     */
    fun changeState(next: State) {
        state?.end()
        state = next
        next.start()
    }

    /**
     * Update the currently active state.
     */
    fun update() {
        state?.update()
    }
}