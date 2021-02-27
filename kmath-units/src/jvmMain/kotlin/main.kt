import space.kscience.kmath.operations.RealField
import space.kscience.kmath.units.MeasureAlgebra.kg
import space.kscience.kmath.units.MeasureAlgebra.m
import space.kscience.kmath.units.MeasureAlgebra.s
import space.kscience.kmath.units.MeasurementExtendedField

public fun main() {
    val res = with(MeasurementExtendedField(RealField)) {
        +(42.0 to kg) * (+(42.0 to m) / +(2.0 to s))
    }

    println(res)
}
