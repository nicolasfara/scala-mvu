package it.unibo.pps.mvu.model

/**
 * Represents the model of the application.
 * More precisely, it represents the state of the application.
 * In this case our "state" is just a counter holding the current value.
 */
enum Model:
  /**
   * The only value of our model (state) is a counter holding the current [[value]].
   */
  case Counter(value: Int)
