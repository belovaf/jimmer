---
sidebar_position: 4
title: Delete指令
---

## 基本用法

Delete指令是一个相对简单的概念。

```java
Collection<UUID> ids = Arrays.asList(
    UUID.fromString(
        "e110c564-23cc-4811-9e81-d587a13db634"
    ),
    UUID.fromString(
        "8f30bc8a-49f9-481d-beca-5fe2d147c831"
    ),
    UUID.fromString(
        "914c8595-35cb-4f67-bbc7-8029e9e6245a"
    ),
    UUID.fromString(
        "a62f7aa3-9490-4612-98b5-98aae0e77120"
    )
);

DeleteResult result = sqlClient
    .getEntities()
    .batchDelete(Book.class, ids);

System.out.println(
    
    "Affected row count: " + 
    result.getTotalAffectedRowCount() +

    "\nAffected row count of table 'BOOK': " +
    result.getAffectedRowCount(AffectedTable.of(Book.class)) +
    
    "\nAffected row count of middle table 'BOOK_AUTHOR_MAPPING': " +
    result.getAffectedRowCount(
        AffectedTable.of(BookTableEx.class, BookTableEx::authors)
    )
);
```

最终生成的SQL如下

1. 
    ```sql
    delete from BOOK_AUTHOR_MAPPING 
    where BOOK_ID in(?, ?, ?, ?)
    ```

2. 
    ```sql
    delete from BOOK 
    where ID in(?, ?, ?, ?)
    ```

## 删除多对一关联的父对象

从上面的论述可以看到，delete指令有可能导致多对多关联中间表的数据的被删除，这是比较简单的情况。

对于直接基于外键的一对一或一对多关联而言，需要处理的情况更复杂一些。

```java
UUID storeId = UUID.fromString(
    "d38c10da-6be8-4924-b9b9-5e81899612a0"
);

DeleteResult result = sqlClient
    .getEntities()
    .delete(BookStore.class, storeId);

System.out.println(

    "Affected row count: " +
            result.getTotalAffectedRowCount() +

            "\nAffected row count of table 'BOOK_STORE': " +
            result.getAffectedRowCount(AffectedTable.of(BookStore.class)) +

            "\nAffected row count of table 'BOOK': " +
            result.getAffectedRowCount(AffectedTable.of(Book.class)) +

            "\nAffected row count of middle table 'BOOK_AUTHOR_MAPPING': " +
            result.getAffectedRowCount(
                    AffectedTable.of(BookTableEx.class, BookTableEx::authors)
            )
);
```

这段代码删除一个`BookStore`对象。

由于`BookStore`对象存在一对多关联`BookStore.books`，如果被删除的对象在数据库中已经存在一些关联对象，jimmer-sql将抛弃这些对象。

一对多关联`BookStore.books`不是基于中间表的映射，而是基于外键映射。jimmer-sql将如何抛弃这些`Book`对象呢？

和JPA不同，jimmer-sql的不允许直接使用`@OneToOne`和`@OneToMany`进行关系映射，`@OneToOne`和`@OneToMany`必须使用`mappedBy`属性。可以参考[@OneToMany](../mapping#javaxpersistanceonetomany)以了解更多。

这表示，通过一对多关联`BookStore.books`一定能找到与之对应的多对一关联`Book.store`。

接下来，jimmer-sql会参考多对一关联属性`Book.store`上的[@OnDissociate](../mapping#orgbabyfishjimmersqlondissociate)注解。

1. 如果`Book.store`所对应的外键被[@OnDissociate](../mapping#orgbabyfishjimmersqlondissociate)注解配置为`SET_NULL`，则，执行如下SQL

    ```sql
    update BOOK set STORE_ID = null where STORE_ID in(?)
    ```
    其中参数为被删除对象的id。这样，这些被抛弃对象的外键就被设置为null了。

2. 否则，则先执行

    ```sql
    select ID from BOOK where STORE_ID in(?)
    ```
    其中参数为被删除对象的id。这样，就得到这些被抛弃对象的id了。

    > 如果查询没有返回任何数据，就忽略后续步骤。

    - 如果`Book.store`所对应的外键被[@OnDissociate](../mapping#orgbabyfishjimmersqlondissociate)注解配置为`DELETE`，
        运用新的delete指令删除这些被抛弃对象，其实这就是delete指令的自动递归执行能力。

    - 否则，抛出异常。

上面所讨论的这些情况，都需要开发人员在`Book.store`属性上使用注解[@OnDissociate](../mapping#orgbabyfishjimmersqlondissociate)。

然而，你也可以选择不使用[@OnDissociate](../mapping#orgbabyfishjimmersqlondissociate)注解，而动态地为delete指令指定dissociateAction配置。

```java
UUID storeId = ...;
DeleteResult result = sqlClient
    .getEntities()
    .deleteCommand(BookStore.class, storeId)
    .configure(it ->
            it
                // highlight-next-line
                .setDissociateAction(
                    BookTable.class,
                    BookTable::store,
                    DissociateAction.SET_NULL
                )
    )
    .execute();
```

这里，动态地调用指令的`setDissociateAction`方法，相比于静态地在`Book.store`属性上使用注解[@OnDissociate](../mapping#orgbabyfishjimmersqlondissociate)并指定级联删除，效果完全一样。

:::info
1. 如果`setDissociateAction`方法最后一个参数为`DissociateAction.SET_NULL`，则被设置关联属性必须可空，否则会导致异常。

2. 如果既动态地为save指令配置了删除规则，又静态地在实体接口中通过注解[@OnDissociate](../mapping#orgbabyfishjimmersqlondissociate)配置了删除规则，则动态配置优先。
:::



