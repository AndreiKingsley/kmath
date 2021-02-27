package space.kscience.kmath.units

import space.kscience.kmath.ast.MST
import space.kscience.kmath.ast.MST.*
import space.kscience.kmath.operations.*

public typealias Measure = MST

public data class Measurement<T>(val measure: MST, val value: T)

public object MeasureAlgebra : Algebra<Measure> {
    public const val MULTIPLY_OPERATION: String = "*"
    public const val DIVIDE_OPERATION: String = "/"

    public val pure: Measure = Numeric(1)
    public val m: Measure = Symbolic("m")
    public val kg: Measure = Symbolic("kg")
    public val s: Measure = Symbolic("s")
    public val A: Measure = Symbolic("A")
    public val K: Measure = Symbolic("K")
    public val mol: Measure = Symbolic("mol")
    public val cd: Measure = Symbolic("cd")

    public fun multiply(a: Measure, b: Measure): Measure = Binary(MULTIPLY_OPERATION, a, b)
    public fun divide(a: Measure, b: Measure): Measure = Binary(DIVIDE_OPERATION, a, b)

    public operator fun Measure.times(b: Measure): Measure = multiply(this, b)
    public operator fun Measure.div(b: Measure): Measure = divide(this, b)

    public override fun bindSymbol(value: String): Measure = when (value) {
        "m" -> m
        "kg" -> kg
        "s" -> s
        "A" -> A
        "K" -> K
        "mol" -> mol
        "cd" -> cd
        else -> super.bindSymbol(value)
    }

    override fun binaryOperationFunction(operation: String): (left: Measure, right: Measure) -> Measure =
        when (operation) {
            MULTIPLY_OPERATION -> ::multiply
            DIVIDE_OPERATION -> ::divide
            else -> super.binaryOperationFunction(operation)
        }
}

public val MeasureAlgebra.rad: Measure get() = pure
public val MeasureAlgebra.sr: Measure get() = pure
public val MeasureAlgebra.degC: Measure get() = K
public val MeasureAlgebra.Hz: Measure get() = pure / s
public val MeasureAlgebra.N: Measure get() = kg * m * (pure / (s * s))
public val MeasureAlgebra.J: Measure get() = N * m
public val MeasureAlgebra.W: Measure get() = J / s
public val MeasureAlgebra.Pa: Measure get() = N / (m * m)
public val MeasureAlgebra.lm: Measure get() = cd * sr
public val MeasureAlgebra.lx: Measure get() = lm / (m * m)
public val MeasureAlgebra.C: Measure get() = A * s
public val MeasureAlgebra.Ω: Measure get() = V / A
public val MeasureAlgebra.V: Measure get() = J / C
public val MeasureAlgebra.F: Measure get() = C / V
public val MeasureAlgebra.Wb: Measure get() = kg * m * m * (pure / (s * s)) * (pure / A)
public val MeasureAlgebra.T: Measure get() = Wb / (m * m)
public val MeasureAlgebra.H: Measure get() = kg * m * m * (pure / (s * s)) * (pure / (A * A))
public val MeasureAlgebra.S: Measure get() = pure / Ω
public val MeasureAlgebra.Bq: Measure get() = pure / s
public val MeasureAlgebra.Gy: Measure get() = J / kg
public val MeasureAlgebra.Sv: Measure get() = J / kg
public val MeasureAlgebra.kat: Measure get() = mol / s

public open class MeasurementSpace<T>(public open val algebra: Space<T>) : Space<Measurement<T>> {
    public override val zero: Measurement<T>
        get() = Measurement(MeasureAlgebra.pure, algebra.zero)

    public override fun add(a: Measurement<T>, b: Measurement<T>): Measurement<T> {
        require(a.measure == b.measure) { "The units are incompatible." }
        return Measurement(a.measure, algebra { a.value + b.value })
    }

    public override fun multiply(a: Measurement<T>, k: Number): Measurement<T> =
        Measurement(a.measure, algebra { a.value * k })

    public operator fun Pair<T, Measure>.unaryPlus(): Measurement<T> = Measurement(second, first)
}

public open class MeasurementRing<T>(override val algebra: Ring<T>) : MeasurementSpace<T>(algebra),
    Ring<Measurement<T>> {
    public override val one: Measurement<T>
        get() = Measurement(MeasureAlgebra.pure, algebra.one)

    public override fun multiply(a: Measurement<T>, b: Measurement<T>): Measurement<T> =
        Measurement(MeasureAlgebra { a.measure * b.measure }, algebra { a.value * b.value })
}

public open class MeasurementField<T>(public override val algebra: Field<T>) : MeasurementRing<T>(algebra),
    Field<Measurement<T>> {
    public override fun divide(a: Measurement<T>, b: Measurement<T>): Measurement<T> =
        Measurement(MeasureAlgebra { a.measure / b.measure }, algebra { a.value / b.value })
}

public open class MeasurementExtendedField<T>(public override val algebra: ExtendedField<T>) :
    MeasurementField<T>(algebra),
    ExtendedField<Measurement<T>> {
    public override fun number(value: Number): Measurement<T> = Measurement(MeasureAlgebra.pure, algebra.number(value))
    public override fun sin(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.sin(arg.value))
    public override fun cos(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.cos(arg.value))
    public override fun tan(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.tan(arg.value))
    public override fun asin(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.asin(arg.value))
    public override fun acos(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.acos(arg.value))
    public override fun atan(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.atan(arg.value))

    public override fun power(arg: Measurement<T>, pow: Number): Measurement<T> =
        (this as Field<Measurement<T>>).power(arg, pow.toInt())

    public override fun exp(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.exp(arg.value))
    public override fun ln(arg: Measurement<T>): Measurement<T> = Measurement(arg.measure, algebra.ln(arg.value))
}
