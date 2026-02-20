# INF222 V26 - Obligatory Assignment 1

This assignment is split into two parts. In the first part, you will use AspectJ to enable automated measurements conversions in certain kind of Java programs. In the second part you will use annotiations and AspectJ to implement logging functionality for a simple bank account system without cluttering up the bussiness logic.

## Prerequisites

To run the code examples provided in the tasks, ensure that you have the following installed on your system:

- Java Development Kit 21 (JDK)
- Maven

Make sure to have these tools properly set up and configured in your environment before proceeding.

## Running the tasks

Navigate to oblig folder in the terminal

For part 1, execute the following command:
```bash
mvn compile exec:java@measures
```

For part 2, execute the following command:
```bash
mvn compile exec:java@account
```

Run tests by executing the following command:
```bash
mvn test
```

# IMPORTANT INFORMATION ABOUT THE TESTS
- You are provided with some tests for this obligatory, but they are meant as an indication on whether your solution is on the right track.
- In addition, there are hidden tests that will run on CodeGrade.
- Even if all tests pass both locally and on CodeGrade, this does NOT automatically mean that you have passed the assignment. Please make sure that you are certain about the correctness of your solution!
- IMPORTANT: We will grade all submissions manually!

## Part 1 - Aspect-Oriented Programming in AspectJ

### 1.1 - Converting Measures

In the file [Measures.java](./src/main/java/inf222/aop/measures/Measures.java), you will find several arithmetic expressions involving static fields. Each of these fields has a _measure specifier_, such as `m` (for _meters_), or `yd` (for _yards_), appended directly to the name of the field (for example, `l_ft` ("`l` is a value in feet"), `h_cm` ("`h` is a value in centimeters"), etc.). Thus, a measure specifier indicates that a variable represents an amount in the specified measure.

When you run the code as it is, the compiler is obviously unaware of the fact variables' values are given in different measures and have to be converted to actually be accurate values. For example, if we add `l_ft + a_m`, where `l_ft` was declared as `double l_ft=10;` and `a_m` was declared as `a_m=5;`, the result should not simply be `15`. To make this correct, the measures should be converted to a common unit before performing arithmetic operations. We choose _meters_ as the common unit. In this case, assuming an conversion factor of `1 ft` is `0.3048 m`, the correct calculation would be:

> 10 ft + 5 m = (10 * 0.3048) m + 5 m = 3.048 m + 5 m = 8.048 m

Your task is to implement such measure conversion for variables **without modifying the original source code**, but rather using **Aspect-Oriented Programming** techniques in **AspectJ**.

Here is a simple example of Java code and what should be its output when it's run via the AspectJ weaver:

```java
double example_ft = 1.0;
System.out.println(example_ft); // should output 0.3048
```

#### Requirements

In the file [MeasureAspect.java](./src/main/java/inf222/aop/measures/MeasureAspect.java), **you need to write an advice that:**

- intercepts field accesses
- converts the value to `m`
- Note: Use [regular expressions](https://en.wikipedia.org/wiki/Regular_expression) to determine what measure the field corresponds to.

#### Conversion factors from various measurement units to meters

You can use the following conversion factors when converting to `m`:

| Measure   | Conversion rate to `m` |
| --------- | ---------------------- |
| `cm`      | 0.01                   |
| `ft`      | 0.3048                 |
| `in`      | 0.0254                 |
| `yd`      | 0.9144                 |

### 1.2 - Handling Field Modification

If you have implemented the advice in [task 1.1](#11---converting-measures),
field accesses should now be handled correctly.
However, this introduces a new problem: whenever we _modify_ a field, it will hold the wrong value.

Consider the following Java code where a variable is being modified:

```java
double example_yd = 1;
example_yd *= 2; // the value it holds now is actually 1.8288
System.out.println(example_yd) // will wrongly output 1.67225472
```

In this example, after modifying `example_yd`, the value that it holds is not what we expect. This happens because compound assignment operators like `*=`, `+=`, etc. implicitly reference the field:

Indeed, writing

```java
example_yd *= 2;
```

is equivalent to writing

```java
example_yd = example_yd * 2;
```

In this case, the variable's reference is implicitly converted to `m`, and the resulting value is computed in terms of `m` rather than `yd`.
As a result, the field holds an incorrect value (which is actually in `m`).

Your task is to solve this issue by converting the value back to the correct measure.

#### Requirements

In the file [MeasureAspect.java](./src/main/java/inf222/aop/measures/MeasureAspect.java), **you need to write an advice that:**

- intercepts field modifications
- does not intercept the initalization of fields inside the constructor
- converts the value back to its original measure from `m`

<details>
  <summary><b>Hints</b></summary>
  <ul>
    <li>To reference execution of a constructor, you can create a control flow pointcut: <code>cflow(execution(..................))</code></li>
    <li>To change the value a field is being set to you need to use the <code>jp.proceed</code> method. It can be used as follows:
      <pre><code class="language-java">@Around("......")
void someMethod(ProceedingJoinPoint jp) {
  jp.proceed(new Object[] {10.0});
}</code></pre>
    </li>

  </ul>
</details>

### 1.3 - Handling Negative Values

The program is now nearly complete, but there is one important edge case to address. Since we are dealing with measure values, they should never be negative (if we disregard subtractions). To ensure that our program behaves correctly, we need to add a validation that ensures that any measure value remains positive.

#### Requirements

For the last part, **you need to write an advice that:**

- intercepts field modifications
- checks whether the new value is positive
- if it is negative, throws an exception

## Part 2 - Logging with Aspect-Oriented Programming in AspectJ

In the file [Bank.java](./src/main/java/inf222/aop/account/Bank.java), you will find two methods -- `internationalTransfer` and `domesticTransfer` -- that we want to implement logging functionality for. More specifically, we want to be able to log three things:

- international transfers
- transfers above a certain value
- whether there are errors with a transfer.

The way this will be done is by creating an annotation `@Transfer` that has support several options (see **Requirements** below).

Here are examples of Java code where the annotation is used:
```java
@Transfer(logErrors = true, internationalTransfer = true)
public boolean internationalTransfer(Account from, Account to, Double amount) {
    // ...
    // business logic here
    // ...
}

@Transfer(logErrors = true, LogTransferAbove = 100_000)
public boolean domesticTransfer(Account from, Account to, Double amount) {
    // ...
    // business logic here
    // ...
}
```

And here is an example of how the output logs look like:

```bash
15:11:54.232 [inf222.aop.account.Launcher.main()] INFO inf222.aop.account.Bank -- International transfer from ac1 to ac2, 3.0 NOK converted to USD
15:11:54.234 [inf222.aop.account.Launcher.main()] INFO inf222.aop.account.Bank -- Transfer above 100000.0 from ac3 to ac4, amount: 160000.0
15:11:54.234 [inf222.aop.account.Launcher.main()] INFO inf222.aop.account.Bank -- Error in transfer from ac5 to ac6, amount: 50000.0 NOK, method: domesticTransfer(from, to, amount)
```

### Requirements

In the file [Transfer.java](./src/main/java/inf222/aop/account/annotation/Transfer.java), **you need to write an annotation that has support for the options:**

| option                  | description                                                            | default value      |
| ----------------------- | ---------------------------------------------------------------------- | ------------------ |
| `value`                 | specifies the logging level                                            | `Level.INFO`       |
| `internationalTransfer` | specifies whether the transfer is an international transfer            | `false`            |
| `LogTransferAbove`      | specifies a minimum value, above which every transfer should be logged | `Double.MAX_VALUE` |
| `logErrors`             | enables logging of error during a transfer                             | `false`            |


In the file [TransferAspect.java](./src/main/java/inf222/aop/account/aspect/TransferAspect.java), **you need to write an advice that implements logging for the following:**

* International transfers should log the following information:
    * Which accounts the transfer is between.
    * The amount that is transferred.
    * The currency that is transferred _from_ and the currency transferred _to_.
* Transfers above should log the following information:
    * The minimum value before logging.
    * Which accounts the transfer is between.
    * The amount that is transferred.
* When logging errors, log the following information:
    * Which accounts the transfer is between.
    * The amount that is transferred and its currency.
    * The name of the method that was called and its parameter names.

* Use `slf4j.Logger` to log the events