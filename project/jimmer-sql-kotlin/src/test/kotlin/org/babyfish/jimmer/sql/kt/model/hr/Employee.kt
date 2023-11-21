package org.babyfish.jimmer.sql.kt.model.hr

import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongConverter
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface Employee {

    @Id
    @JsonConverter(LongConverter::class)
    val id: Long

    val name: String

    @ManyToOne
    val department: Department?
}