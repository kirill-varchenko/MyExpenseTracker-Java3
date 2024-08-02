# MyExpenseTracker

## Description

It's the final project for the <i>Java Erweiterungskurs</i>. The main goal is to remake the previous project with frameworks from this course part.

## Plan

### Functionality and structure
- Gradle project.
- Spring Boot / Vaadin application.
- Authentication.
- Managing expenses, incomes, transfers and exchanges.
- Managing accounts, currencies, categories and tags.
- Storing data in a Postgres database.
- DB migrations.
- DB manipulations Spring Data JpaRepositories.
- Dashboard with diagrams and calculated tables.
- Secrets in ENV variables

### Deliberately not planned
- JSR 354 / Currency and Money API
- Complex validation
- REST API
- Tests :/

## What was done
- Everything that was planned :)
- Comparing to the previous project domain models were heavily refactored.
- Nice features:
  - Separated Domain models and DB entities with dedicated mappers
  - Flyway migrations
  - NamedEntityGraph for loading complex entities

## Used frameworks and libraries
- Spring Boot, Security, Data
- Vaadin
- Lombok
- MapStruct
- Flyway
- ApexCharts

## Problems encountered
- N+1 loading problem with Record->Entry->Tag collections. **Solved** by declaring EntityGraph.
- *MultipleBagFetchException* while loading Record->Entry->Tag collections. **Workaround**: declare Entry tags as a set.
- *HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!* warning. **Solved** with two steps: 1) query paginated list of ids, 2) query entities by list of ids. 
- Integrity Error while updating Records, because in detached Entries record_id were first set to null and then Entries were deleted as orphans. **Solved** by making record_id nullable.
- Cannot "out of the box" combine Specification, Pageable and custom result type. **Solved** by implementing custom Record repository interface using CriteriaQuery.

## Useful links

[Ultimate Guide to N+1 Loading Problem in Hibernate/JPA](https://medium.com/@chikim79/ultimate-guide-to-n-1-loading-problem-in-hibernate-jpa-42e8e6cfb9f3)

[One-to-One Relationship in JPA](https://www.baeldung.com/jpa-one-to-one)

[Hibernate @CreationTimestamp and @UpdateTimestamp](https://www.baeldung.com/hibernate-creationtimestamp-updatetimestamp)

[A Guide to MultipleBagFetchException in Hibernate](https://www.baeldung.com/java-hibernate-multiplebagfetchexception)

[Spring Data JPA Pagination HHH000104](https://stackoverflow.com/questions/64799564/spring-data-jpa-pagination-hhh000104)

[MapStruct 1.6.0.RC1 Reference Guide](https://mapstruct.org/documentation/1.6/reference/html/)

[Spring Data JPA Repository for Database View](https://www.baeldung.com/spring-data-jpa-repository-view)

[Mastering Spring Data JPA Specifications for Robust Data Access](https://medium.com/hprog99/mastering-spring-data-jpa-specifications-for-robust-data-access-24e7626d169a)