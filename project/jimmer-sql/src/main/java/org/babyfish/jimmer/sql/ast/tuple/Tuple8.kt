package org.babyfish.jimmer.sql.ast.tuple

import org.babyfish.jimmer.sql.ast.impl.TupleImplementor
import java.util.function.BiFunction

data class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(
    val _1: T1,
    val _2: T2,
    val _3: T3,
    val _4: T4,
    val _5: T5,
    val _6: T6,
    val _7: T7,
    val _8: T8
) : TupleImplementor {

    override fun size(): Int = 8

    override operator fun get(index: Int): Any? =
        when (index) {
            0 -> _1
            1 -> _2
            2 -> _3
            3 -> _4
            4 -> _5
            5 -> _6
            6 -> _7
            7 -> _8
            else -> throw IllegalArgumentException("Index must between 0 and ${size() - 1}")
        }

    override fun convert(block: BiFunction<Any?, Int, Any?>): TupleImplementor =
        Tuple8(
            block.apply(_1, 0),
            block.apply(_2, 1),
            block.apply(_3, 2),
            block.apply(_4, 3),
            block.apply(_5, 4),
            block.apply(_6, 5),
            block.apply(_7, 6),
            block.apply(_8, 7)
        )

    companion object {
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> projection1(tuples: Collection<Tuple8<T, *, *, *, *, *, *, *>>) : Collection<T> =
            TupleImplementor.projection(tuples, 0) as Collection<T>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> projection2(tuples: Collection<Tuple8<*, T, *, *, *, *, *, *>>) : Collection<T> =
            TupleImplementor.projection(tuples, 1) as Collection<T>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> projection3(tuples: Collection<Tuple8<*, *, T, *, *, *, *, *>>) : Collection<T> =
            TupleImplementor.projection(tuples, 2) as Collection<T>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> projection4(tuples: Collection<Tuple8<*, *, *, T, *, *, *, *>>) : Collection<T> =
            TupleImplementor.projection(tuples, 3) as Collection<T>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> projection5(tuples: Collection<Tuple8<*, *, *, *, T, *, *, *>>) : Collection<T> =
            TupleImplementor.projection(tuples, 4) as Collection<T>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> projection6(tuples: Collection<Tuple8<*, *, *, *, *, T, *, *>>) : Collection<T> =
            TupleImplementor.projection(tuples, 5) as Collection<T>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> projection7(tuples: Collection<Tuple8<*, *, *, *, *, *, T, *>>) : Collection<T> =
            TupleImplementor.projection(tuples, 6) as Collection<T>

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> projection8(tuples: Collection<Tuple8<*, *, *, *, *, *, *, T>>) : Collection<T> =
            TupleImplementor.projection(tuples, 7) as Collection<T>
    }
}