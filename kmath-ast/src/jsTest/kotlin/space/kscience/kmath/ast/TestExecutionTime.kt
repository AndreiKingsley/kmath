package space.kscience.kmath.ast

import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.expressionInExtendedField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.RealField
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.measureTime
import space.kscience.kmath.estree.compile as estreeCompile
import space.kscience.kmath.wasm.compile as wasmCompile

internal class TestExecutionTime {
    private companion object {
        private const val times = 1_000_000
        private val algebra: ExtendedField<Double> = RealField

        val functional = RealField.expressionInExtendedField {
            bindSymbol("x") * const(2.0) + const(2.0) / bindSymbol("x") - const(16.0) / sin(bindSymbol("x"))
        }

        val mst = RealField.mstInExtendedField {
            bindSymbol("x") * number(2.0) + number(2.0) / bindSymbol("x") - number(16.0) / sin(bindSymbol("x"))
        }

        val wasm = mst.wasmCompile()
        val estree = mst.estreeCompile()

        // In JavaScript, the expression below is implemented like
        //   _no_name_provided__125.prototype.invoke_178 = function (args) {
        //    var tmp = getValue(args, raw$_get_x__3(this._$x$delegate_2)) * 2.0 + 2.0 / getValue(args, raw$_get_x__3(this._$x$delegate_2));
        //    var tmp0_sin_0_5 = getValue(args, raw$_get_x__3(this._$x$delegate_2));
        //    return tmp - 16.0 / Math.sin(tmp0_sin_0_5);
        //  };

        val raw = run {
            val x by symbol

            Expression<Double> { args ->
                args.getValue(x) * 2.0 + 2.0 / args.getValue(x) - 16.0 / kotlin.math.sin(args.getValue(x))
            }
        }
    }

    private fun invokeAndSum(name: String, expr: Expression<Double>) {
        println(name)
        val rng = Random(0)
        var sum = 0.0
        measureTime { repeat(times) { sum += expr("x" to rng.nextDouble()) } }.also(::println)
    }

    @Test
    fun functionalExpression() = invokeAndSum("functional", functional)

    @Test
    fun mstExpression() = invokeAndSum("mst", mst)

    @Test
    fun wasmExpression() = invokeAndSum("wasm", wasm)

    @Test
    fun estreeExpression() = invokeAndSum("estree", wasm)

    @Test
    fun rawExpression() = invokeAndSum("raw", raw)
}