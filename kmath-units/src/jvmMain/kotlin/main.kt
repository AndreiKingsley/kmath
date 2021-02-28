import space.kscience.kmath.operations.RealField
import space.kscience.kmath.units.*

public fun main() {
    var res = with(MeasurementExtendedField(RealField)) {
        val a = (2.0 * kg) / (3.0 * m) / (2.0 * with(MeasureAlgebra) { s * s })
        val b = (23.0 * Pa)
        a + b
    }

    println(res)

    res = with(MeasurementExtendedField(RealField)) {
        val a = (2.0 * G(m)) + (3.0 * M(m))
        val b = (3.0 * au)
        a + b
    }

    println(res)
}
