package space.kscience.kmath.tensors.core

import kotlin.math.abs
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestDoubleAnalyticTensorAlgebra {

    val shape = intArrayOf(2, 1, 3, 2)
    val buffer = doubleArrayOf(27.1, 20.0, 19.84, 23.123, 0.0, 1.0, 3.23, 133.7, 25.3, 100.3, 11.0, 12.012)
    val tensor = DoubleTensor(shape, buffer)

    fun DoubleArray.fmap(transform: (Double) -> Double): DoubleArray {
        return this.map(transform).toDoubleArray()
    }

    fun DoubleArray.deltaEqual(other: DoubleArray, delta: Double = 1e-5): Boolean {
        for ((elem1, elem2) in this.asSequence().zip(other.asSequence())) {
            if (abs(elem1 - elem2) > delta) {
                return false
            }
        }
        return true
    }

    @Test
    fun testExp() = DoubleAnalyticTensorAlgebra {
        tensor.exp().let {
            assertTrue { shape contentEquals it.shape }
            assertTrue { buffer.fmap(::exp).deltaEqual(it.buffer.array())}
        }
    }
}