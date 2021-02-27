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

public val rad: Measure get() = MeasureAlgebra.pure
public val sr: Measure get() = MeasureAlgebra.pure
public val degC: Measure get() = MeasureAlgebra.K
public val Hz: Measure get() = MeasureAlgebra { pure / s }
public val N: Measure get() = MeasureAlgebra { kg * m * (pure / (s * s)) }
public val J: Measure get() = MeasureAlgebra { N * m }
public val W: Measure get() = MeasureAlgebra { J / s }
public val Pa: Measure get() = MeasureAlgebra { N / (m * m) }
public val lm: Measure get() = MeasureAlgebra { cd * sr }
public val lx: Measure get() = MeasureAlgebra { lm / (m * m) }
public val C: Measure get() = MeasureAlgebra { A * s }
public val Ω: Measure get() = MeasureAlgebra { V / A }
public val V: Measure get() = MeasureAlgebra { J / C }
public val F: Measure get() = MeasureAlgebra { C / V }
public val Wb: Measure get() = MeasureAlgebra { kg * m * m * (pure / (s * s)) * (pure / A) }
public val T: Measure get() = MeasureAlgebra { Wb / (m * m) }
public val H: Measure get() = MeasureAlgebra { kg * m * m * (pure / (s * s)) * (pure / (A * A)) }
public val S: Measure get() = MeasureAlgebra { pure / Ω }
public val Bq: Measure get() = MeasureAlgebra { pure / s }
public val Gy: Measure get() = MeasureAlgebra { J / kg }
public val Sv: Measure get() = MeasureAlgebra { J / kg }
public val kat: Measure get() = MeasureAlgebra { mol / s}

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
