---
sidebar_position: 6
title: 和开发人员的约定
---

Java库API设计中，链式编程风格是一种常见的设计。

链式API设计有两种动机:

1. 不修改当前对象，创建新的对象。常见于不可变对象，例如`String`

    ```java
    String value = " Hello world  "
        .trim() // α
        .toUpperCase() // β
        .replace(' ', '-'); // γ
    ```

    例子中`α`、`β`和`γ`三处方法调用均不修改当前字符串，而是创建新的字符串。

    对这种用法而言，链式API本身就是设计目标。用户必须重视每次调用的返回值，丢弃返回值会让方法调用毫无意义。

2. 修改当前对象，并返回当前对象自身。常见于可变对象，例如`StringBuilder`

    ```java
    StringBuilder value = new StringBuilder()
        .append("Hello ") // α
        .append(' ') // β
        .append(System.getProperty("user")); // γ
    ```

    例子中`α`、`β`和`γ`三处方法调用均返回当前对象自身，从未创建新的对象，代码自始至终都在操作同一个`StringBuilder`对象。

    对这种用法而言，链式API本身并非设计目标，仅仅为了简化代码。对其返回值，用户选择利用还是丢弃无所谓。

    :::note
    这种设计动机是为了解决Java本身表达能力的小缺陷，在更现代的语言中，无需此技巧。
    :::

风格一致的链式API，却对应着两种截然不同的设计动机，对应着完全不一样的理解和用法。

除了`String`和`StringBuilder`这类普及度很高的基础类型外，对于绝大部分框架而言，某个链式API究竟是对应何哪种动机往往成为了用户学习成本的一部分。甚至，有时还会成为了混乱的根源。比如[JPA Criteria的Predicate.not方法](https://docs.oracle.com/javaee/7/api/javax/persistence/criteria/Predicate.html#not--)，从文档看，其设计动机应该是`2`，但是无法阻止某些JPA vender按照`1`来实现。

jimmer不想让鉴别某个链式API的设计动机成为用户学习成本的一部分，定义了两个注解。

1. `@org.babyfish.jimmer.lang.NewChain`
2. `@org.babyfish.jimmer.lang.OldChain`

:::note
这两个注解的RetentionPolicy是SOURCE，不会影响字节码，更不会影响反射行为，仅作为函数签名的一部分。

- @NewChain对应动机1，表示当前链式API总是创建新对象，而不会修改当前对象。
- @OldChian对应动机2，表示当前链式API总是修改并返回当前对象，而不会创建新对象。
:::

以jimmer-sql子项目的两个接口为例

```java title="MutableQuery.java"
package org.babyfish.jimmer.sql.ast.query;

import org.babyfish.jimmer.lang.OldChain;

public interface MutableRootQuery<T extends Table<?>> 
    extends MutableQuery, RootSelectable<T> {

    // highlight-next-line
    @OldChain
    MutableRootQuery<T> where(
        Predicate... predicates
    );

    // highlight-next-line
    @OldChain
    @Override
    default MutableRootQuery<T> orderBy(
        Expression<?> expression
    );

    // highlight-next-line
    @OldChain
    default MutableRootQuery<T> orderBy(
        Expression<?> expression, 
        OrderMode orderMode
    );

    // highlight-next-line
    @OldChain
    MutableRootQuery<T> orderBy(
        Expression<?> expression, 
        OrderMode orderMode, 
        NullOrderMode nullOrderMode
    );

    // highlight-next-line
    @OldChain
    MutableRootQuery<T> groupBy(
        Expression<?>... expressions
    );

    // highlight-next-line
    @OldChain
    MutableRootQuery<T> having(
        Predicate... predicates
    );
}
```

```java title="ConfigurableTypedRootQuery.java"
package org.babyfish.jimmer.sql.ast.query;

import org.babyfish.jimmer.lang.NewChain;
import java.util.function.BiFunction;

public interface ConfigurableTypedRootQuery<T extends Table<?>, R> 
    extends TypedRootQuery<R> {

    // highlight-next-line
    @NewChain
    <X> ConfigurableTypedRootQuery<T, X> reselect(
            BiFunction<
                MutableRootQuery<T>, 
                T, 
                ConfigurableTypedRootQuery<T, X>
            > block
    );

    // highlight-next-line
    @NewChain
    ConfigurableTypedRootQuery<T, R> distinct();

    // highlight-next-line
    @NewChain
    ConfigurableTypedRootQuery<T, R> limit(int limit, int offset);

    // highlight-next-line
    @NewChain
    ConfigurableTypedRootQuery<T, R> withoutSortingAndPaging();

    // highlight-next-line
    @NewChain
    ConfigurableTypedRootQuery<T, R> forUpdate();
}
```

:::tip
jimmer的链式API会频繁地使用这两个注解，了解此约定后，再无这方面的学习成本。
:::