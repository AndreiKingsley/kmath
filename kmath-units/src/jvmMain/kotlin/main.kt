import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.units.MeasureAlgebra
import space.kscience.kmath.units.MeasurementExtendedField
import space.kscience.kmath.units.N

public fun main() {
    val res = with(MeasurementExtendedField(RealField)) {
        +(1.0 to N) + +(23.0 to MeasureAlgebra { kg * m * (pure / (s * s)) })
    }

    println(res)
}
