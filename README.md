# Class 5

## (Tail) Recursion

> In order to understand recursion, you must first understand recursion.

*Recursion* is when a subroutine is called from within itself.

Example:

```scala
def fac(n: Int): Int =
  if (n == 0) 1 else n * fac(n - 1)
```

Note that recursion requires a stack-based subroutine calling protocol.

What are some advantages and disadvantages of using recursion?

* *Advantages*: often conceptually easier; yields easier to understand
  code (compared to using loops).

* *Disadvantages*: usually slower due to overhead of executing call
  sequences; can lead to stack overflow.

There is one case when recursion can be implemented without using a
new activation record for every recursive call:

A *tail-recursive* subroutine is one in which no additional
computation ever follows a recursive call.

For tail-recursive subroutines, the compiler can *reuse* the current
activation record at the time of the recursive call, eliminating the
need to allocate a new one. This optimization is called *tail call
elimination*. 

Compilers for many modern programming languages perform tail call
elimination. This feature is particularly prevalent in compilers for
functional languages because functional programs tend to use recursion
in favor of loops.

Example:

```scala
def fac(n: Int): Int =
  if (n == 0) 1 else n * fac(n - 1)
```

This function is not tail-recursive, because the recursive call to
`fac` appears below the multiplication with `n`. Hence, there is still
work to be done when the recursive call returns. In particular, we
need to remember the current value of `n` until after the recursive
call. Hence, the current activation record must remain on the stack
and cannot be reused for the recursive call.

However, we can rewrite this function so that it becomes
tail-recursive:

```scala
def facTail(acc: Int, n: Int): Int =
  if (n == 0) acc else facTail(n * acc, n - 1)

def fac(n: Int): Int = facTail(1, n)
```
  
The Scala compiler will detect the tail recursion and performs
tail-call elimination, ensuring that `facLoop` and hence `fac` run in
constant stack space. Effectively, tail call elimination yields an
implementation that is equivalent to one that uses a loop:

```scala
def facLoop(acc0: Int, n0: Int): Int = {
  var acc = acc0
  var n = n0
  
  while (n != 0) {
    acc = n * acc
    n = n - 1
  }

  return acc
}

def fac(n: Int): Int = facLoop(1, n)
```

except that the tail-recursive code is simpler and does not use side
effects.

## Functional Programming

*Functional Programming* refers to a programming style in which every
procedure is *functional*, i.e. it computes a function of its
inputs with no side effects.

Functional programming languages are based on this idea, but they also
provide a number of interesting features that are often missing in
imperative languages.

One of the most important and interesting features of functional
programming languages is that functions are *first-class* values.  The
means that programs can create new functions at run-time. This can be
leveraged to build powerful *higher-order* functions: a higher-order
function either takes a function as an argument or returns a function
as a result (or both). Many modern programming languages and
frameworks draw heavily from these ideas, including Google's
Map/Reduce and TensorFlow frameworks as well as Apache Hadoop and Spark.

Functional languages in turn draw heavily on the *λ-calculus* (lambda
calculus) for inspiration. We will study the λ-calculus in a few
weeks. However, we'll first take a look at functional programming in
action by studying an actual functional programming language: OCaml.

## Introduction to OCaml 

OCaml belongs to the ML family of languages. ML stands for *Meta
Language*, which was originally developed by Robin Milner in the early
1970s for writing theorem provers (i.e., programs that can
automatically find and check the proofs of mathematical theorems). ML
took many inspirations from Lisp (the first functional programming
language; developed in 1958) but introduced several new language
features intended to make programming less error prone. In particular,
ML features a powerful and very well designed static type system.

Languages that belong to the ML family share the following features:

* clean syntax (few parenthesis)
* functions are first-class values
* declarations use static scoping and deep binding semantics
* function calls use call-by-value semantics (call-by-name can be
  simulated using closures)
* automated memory management via garbage collection
* a powerful type system with strong static typing
  * parametric polymorphism (similar to generics)
  * structural type equivalence
  * user-definable algebraic data types and pattern matching
  * all of this with automated static type inference!
* advanced module system with higher-order modules (*functors*)
* exceptions
* imperative features: mutable arrays, references, and record fields

Popular implementations and dialects of ML include

* Standard ML of New Jersey (SML/NJ)
* Poly/ML
* MLton
* OCaml
* F\# (Microsoft; derived from OCaml)
* Reason (Facebook; derived from and implemented on top of OCaml)

The design of Haskell was also strongly influenced by ML.

We will focus on OCaml, which is a general purpose programming
language that was initially developed in the late 1990s by a team of
French computer scientists. In addition to the features shared by all
languages in the ML family, OCaml also provides:

* a compiler that produces efficient native machine code for many
  architectures
* a class system that enables object-oriented programming 

We will ignore OCaml's class system and imperative features as we
study these concepts in Scala instead. So we will focus on the
functional core of OCaml.


### Syntax

OCaml expressions are constructed from constant literals (numbers,
booleans, etc.) and inbuilt operators (arithmetic and logical
operators, etc.) using lambda abstraction and function
application. Inbuilt binary operators use infix notation and follow
standard rules for operator precedence and associativity. Function
application has higher precedence than any other infix operator and is
left
associative. See
[here](https://caml.inria.fr/pub/docs/manual-ocaml/expr.html) for more
details about OCaml expression syntax. In the following, we discuss
the most important syntactic forms. More detailed OCaml tutorials can
be found [here](https://ocaml.org/learn/tutorials/). We also refer to
the [OCaml Manual](http://caml.inria.fr/pub/docs/manual-ocaml/) for a
comprehensive coverage of all features of the language and its
accompanying tools and standard library.

### Let bindings

OCaml programs are structured into modules, which can be nested. Every
source code file automatically defines a module. We will discuss the
module system later and for now assume that we only work with a single
module.

Top-level variable definitions in a module are introduces using `let x
= init` which binds `x` to the value obtained from evaluating the
expression `init`. The scope of the definition is the remainder of the
module that follows the definition with nested let-bindings or
subsequent `let` definitions of `x` within that scope yielding holes in
the scope (as in Scala). The scope of the binding for `x` introduced
by `let` excludes the definition expression `init`.

Example:

```ocaml
let x = 3

let y = x + 1
```

This code defines `x` to be `3` and `y` to be `4`. Note that OCaml allows
the same variable to be redeclared in the same scope:

```ocaml
let x = 3

(* code A *)
...

let x = x + 1

(* code B *)
...
```

This will bind `x` to `3` in code A (including the right-hand side
`x + 1` of the second let binding of `x`) and `x` to `4` in code `B`.

Local variable bindings are introduced using *inner* let expressions,
which take the form ```let x = init in body```. This is itself an
expression which binds `x` to the result of `init` in `body` and then
evaluates to the result of `body` with that binding of `x`. Example:

```ocaml
let y =
  let x = 3 in
  x + 1
```

This will define `y` to be `4`.


### Functions

Functions take a prominent role in OCaml. Like most functional
languages, OCaml allows functions to be defined *in-place* without
giving a name to the function. The expression 

```ocaml
fun x -> body
```

denotes a function that takes a formal parameter `x` and returns the
value computed by the expression `body`, which is the scope of
`x`. Such a function expression is referred to as a *lambda
abstraction*, *anonymous function*, or *closure*. Here is an example
of a lambda abstraction that denotes a function that increments an
integer value by `1`:

```ocaml
fun x -> x + 1
```

Function expressions can be applied to arguments using function
application:

```ocaml
let two = (fun x -> x + 1) 1
```

The expression `(fun x -> x + 1) 1` will apply the function denoted by
`fun x -> x + 1` to `1`, yielding `2`. Thus, `two` will be bound to `2`.

OCaml uses strict evaluation of function arguments and the argument
expressions are always passed by value. OCaml, however, supports
reference values which can be used to implement call-by-reference and
we will later see how to simulate call-by-name parameters using lambda
abstraction and higher-order functions. 

If we want to give a name to a function, we can simply combine lambda
abstraction with let definitions/bindings:

```ocaml
let plus_one = fun x -> x + 1

let two = plus_one 1
```

A `let`-bound name that is defined by a lambda abstraction as in the
previous example can be abbreviated using the notation

```ocaml
let plus_one x = x + 1
```

which is simply a syntactic short-hand for the more elaborate

```ocaml
let plus_one = fun x -> x + 1
```

#### Evaluation order

Note that, unlike Scala, OCaml does not guarantee that the operands of
operators (and arguments of multi-parameter functions) are evaluated
left-to-right. The compiler is allowed to choose the evaluation order
as it sees fit. If your code depends on a particular order, then you
must explicitly enforce it using let bindings. For example, if you
have an expression `e1 + e2` and your code depends on `e1` being
evaluated before `e2`, then write it as

```ocaml
let x1 = e1 in
x1 + e2
```

The exception to this rule are operators like logical conjunction `&&`
and disjunction `||` which have short-circuit semantics as in most
other languages.

#### Multi-parameter Functions

All functions in OCaml take a single parameter. So what to do if we
want to write a function that takes more than one parameter? Suppose
we want to define a function `plus` that simply adds to integer values
`x` and `y`. There are two ways to do this. The first way is to define
a function that takes both parameters `x` and `y` at once:

```ocaml
let plus (x, y) = x + y
```

While this definition makes it appear as if `plus` takes two
parameters, it actually takes just one parameter: a tuple consisting
of two integer values that, in the definition of `plus`, is implicitly
decomposed into its component values `x` and `y`. Thus, when we do

```ocaml
let three = plus (1, 2)
```

Then `plus` will actually be called with a single value which is a
pair constructed from the values `1` and `2`. The function `plus` then
deconstructs that pair again into its constituents `1` and `2` and
returns the result `3`.

While the explicit construction of tuples for multi-parameter
functions appears to cause a lot of overhead, the compiler will
usually be eliminate this overhead in its optimization phase at the
machine code level.

#### Currying, and Partial Function Application

The alternative (and idiomatic) way to define multi-parameter
functions in OCaml is to use a technique referred to as *currying*,
which goes back to the logician Haskell Curry (after which the Haskell
language is also named).

When using currying, we take advantage of the fact that functions are
first-class values in the language: if we want to encode a function
like `plus` that takes two parameters `x` and `y`, we simply write it
as a function that first takes the parameter `x` and then returns a
function that takes the second parameter `y` to calculate the actual
result:

```ocaml
let plus = fun x -> fun y -> x + y
```

We refer to such a function that uses nesting of lambda abstractions
to take multiple parameters as a *curried function*. Naturally, the
idea of currying generalizes to functions that take arbitrarily many
parameters.

The short-hand notation for definitions of single-parameter functions
also extends to curried functions. That is, the above definition can
be abbreviated to

```ocaml
let plus x y = x + y
```

When calling a curried function, we simply provide the arguments one
at a time:

```ocaml
let three = plus 1 2
```

This will bind `three` to `3`. Note that the expression `plus 1 2`
involves two function calls. First, it calls `plus` with `1` yielding
a new function which is then called with `2` in order to calculate the
result value `3`. 

Be careful with the syntax of curried function applications, which can
be confusing at the beginning. Always keep in mind that in OCaml
function application is left-associative. So the expression `plus 1 2`
is equivalent to `(plus 1) 2`.

One very useful feature of curried functions is that we do not have to
provide all the arguments at once. We can apply such a function
partially, by only passing some of its arguments, creating a new
function in the process:

```ocaml
let plus_one = plus 1

let three = plus_one 2
```

Here, `plus_one` is effectively bound to the function `fun y -> 1 + y`
which is obtained from `plus` by passing in `1` for `x`. This idea of
partial function application is very powerful in combination with
higher-order functions, which we will talk about in more detail next week.

### Testing Equality and Conditional Expressions

Equality between two values can be tested with `=` and
disequality with `<>`. 

```ocaml
# 1 = 1 ;;
- : bool = true

# 1 <> 1 ;;
- : bool = false

# 1 <> 2 ;;
- : bool = true
```

Other comparison operators are as usual. For example `x > y` tests
whether integer value `x` is greater than integer value `y`, etc.

Conditional expressions take the syntax `if b then t else e` where `b`
must be an expression that evaluates to a boolean and `t` and `e` must
be expressions that evaluate to values of the same type. For example,
here is a function that computes the maximum of two integer values:

```ocaml
let max x y = if x >= y then x else y
```

### Recursion

Recursive definitions are introduced using `let rec x = init`. A
recursive definition is like a non-recursive definition except that
the scope of `x` also includes the initialization expression `init`.

For example, here is how we can use this to define our all-time
favorite, the factorial function:

```ocaml
let rec fac = fun x ->
  if x = 0 then 1 else x * fac (x - 1)
```
or perhaps more readable
```ocaml
let rec fac x =
  if x = 0 
  then 1 
  else x * fac (x - 1)
```

The recursive form of `let` also extends to inner let bindings. For
example, here is how we can define `fac` using a tail-recursive helper
function `fac_tail` whose definition is nested within `fac`:

```ocaml
let fac x =
  let rec fac_tail x acc =
    if x = 0 then acc else fac_tail (x - 1) (x * acc)
  in
  fac_tail x 1
```

If multiple definitions have mutually recursive dependencies, they can be
defined with `let rec ... and ...`:

```ocaml
let rec f x = g x
and g x = f x
```

Local mutual recursive let bindings use `let rec ... and ... in body`
syntax similar to top-level let-bindings.

### Printing, Unit type

OCaml provides various utility functions for printing values to output
channels (including files). The most primitive ones are the functions
`print_endline` and `print_string` both of which take a string as
argument and print it on standard output (with and without a trailing
new line character, respectively).

The result value of each of these functions is `()` whose type is
`unit`. As in Scala, the type `unit` indicates that a function is only
called for the purpose of its side effect. We can thus think of
OCaml expressions of type `unit` as statements in the usual sense of
imperative programming languages.

```ocaml
# print_string "Hello World!"
Hello World!
- : unit = ()
```

Note that in OCaml programs (i.e. if you are not working with the
OCaml REPL), all expressions must occur below top-level let
definitions. The obligatory "Hello World" program in OCaml looks as
follows:

```ocaml
let _ = print_endline "Hello World!"
```

The underscore `_` is a wildcard pattern (more on that later). We use
it here to indicate that we only want to evaluate the right hand side
of the `let` definition but we do not actually care about the result
value and hence do not actually want to bind it to any variable.

Statements can be composed sequentially using semicolons:

```ocaml
let _ = 
  print_string "Hello ";
  print_endline "World!
```

You can also turn any expression into a statement by using the special
function `ignore` that evaluates its argument but ignores its result,
returning `()` instead:

```ocaml
let _ =
  ignore (1 + 2);
  print_endline "What is 1 + 2 again?"
```

### Types

So far it appears as if OCaml is an untyped language. For instance, in
Scala we need to annotate parameters in function definitions with
their types whereas in OCaml, we can write them without any type
annotations. Nevertheless, OCaml is still a statically typed
programming language, which means that it checks at compile-time that
certain kinds of program errors (e.g. trying to multiply an integer
with a string value or trying to call a value that is not a function)
cannot occur at run-time. However, unlike in Scala, the OCaml compiler
is able to infer all type information automatically from the program
source code.

We will talk more about types and automatic type inference in a few
weeks. However, we need to have at least a basic understanding of how
OCaml's type system works.

Each OCaml expression has an associated type which describes the
possible values that the expression may evaluate to at run-time and
how these values can then be used in other expressions. We distinguish
*primitive types* (e.g. integers, booleans, strings, etc) and
*compound types* which describe composite values that are constructed
from values of simpler types (e.g. tuples, functions, etc).

### Primitive Types

The most important primitive types of OCaml are

Type | Examples | Description
---|---|---
`int` | 1, 2, 42, ... | 31-bit signed int on 32-bit processors, or 63-bit signed int on 64-bit processors
`int32` | Int32.zero, ... | 32-bit signed integer
`int64` | Int64.zero, ... | 64-bit signed integer
`float` | 1.0 3.4, ... | IEEE double-precision floating point, equivalent to C's `double`
`bool` | true, false | booleans
`char` | 'a', 'b', 'x' | 8-bit character
`string` | "Hello" | strings
`unit` | () | the empty tuple (like Scala's `Unit` type)

For each type, we also include some examples of constant literals that
have that type.

### Compound Types

More complex compound types are obtained from simpler types via *type
constructors*. Each type constructor is accompanied by one or more
*value constructors* that construct values of the corresponding
compound type from values of its composite types. We discuss the most
important type and value constructors in the following.

#### Function types

Function types are constructed using the *arrow* type constructor
`->`. The type `t1 -> t2` represents functions that take a value of
type `t1` and return a value of type `t2`. We have already seen the
accompanying value constructor for functions, which is lambda
abstraction `fun x -> body`. For example, the expression

```ocaml
`fun x -> x + 1`
```

has type `int -> int`. The arrow operator of type expressions is
right-associative. In particular, the type `int -> int -> int` should
be interpreted as `int -> (int -> int)`, which is the type of the
curried function `plus` that we saw in an earlier example:

```ocaml
# let plus x y = x + y ;;
val plus : int -> int -> int
```

#### Tuples

OCaml has inbuilt types for tuples. Tuple types are constructed using
the *product* operator `*`. For example, the type `t1 * t2` denotes
pairs whose first component takes values of type `t1` and whose second
component takes values of type `t2`. The type `t1 * t2` can thus be
viewed as the Cartesian product of the sets of values denoted by types
`t1` and `t2`. Similarly, `t1 * t2 * t3` denotes triples of values
taken from `t1`, `t2`, and `t3`, etc. Note that the types `t1 * t2 *
t3`, `(t1 * t2) * t3`, and `t1 * (t2 * t3)` are not equivalent. The
first describes tribles of `t1`, `t2`, and `t3`, the second describes
pairs `t1 * t2` and `t3` values and the third describes pairs of `t1`
and `t2 * t3` values.

Tuple values are constructed using the notation `(e1, ..., en)` where
`e1` to `en` are again expressions. Examples:

```ocaml
# (1, 2) ;;
- : int * int = (1, 2)

# (true, "hello) ;;
- : bool * string = (true, "hello)

# (1, true, 3) ;;
- : int * bool * int) = (1, true, 3)

# (1, (true, 3)) ;;
- : int * (bool * int) = (1, (true, 3))

# let plus (x, y) = x + y ;;
val plus: int * int -> int
```

The type constructor `*` has higher precedence than `->`. So the type
`int * int -> int` denotes a function that takes a pair of integers
and returns again an integer (rather than a pair of an integer and a
function from integers to integers).

The components of a pair `(1, 2)` can be extracted using the
predefined functions `fst` and `snd` (these are similar to `car` and
`cdr` in Scheme):

```ocaml
# let p = (1, 2) ;;
val p : int * int = (1, 2)

# fst p ;;
- : int = 1

# snd p ;;
- : int = 2
```

For tuples of higher arity, use pattern matching to extract components
(see below). 

Note that the enclosing parenthesis around tuples can often be
omitted:

```ocaml
# let p = 1, 2 ;;
val p : int * int = (1, 2)
```

However, this feature of the syntax should be used carefully as it
can occasionally make code harder to read and cause confusion.

#### Lists

Lists are one of the most important data structures in functional
programming languages. A list is a sequence of values (e.g. `1,4,3`)
that can accessed linearly by traversing the list left to right rather
like a linked list that you probably studied in your data structure course.

However, unlike imperative linked lists, lists in functional
programming languages are immutable. That is, once a list has been
created, it can no longer be modified. As with other immutable data
structures, immutable lists have the advantage that their
representation in memory can be shared across different list
instances. For example, the two lists `1,4,3` and `5,2,4,3` can share
their common sublist `4,3`. This feature enables immutable lists to be
used for space-efficient, high-level implementations of algorithms if
the data structure is used correctly.

OCaml lists are *homogenous*, i.e., a single list can only store
elements that have the same common type. The type of a list that holds
elements of type `t` is denoted by `t list`. 

List values are constructed from the empty list, denoted `[]`, using
the *cons* constructor, denoted `::`. The cons constructor `::` is an
infix operator and right-associative. Examples

```ocaml
# 1 :: 2 :: 3 :: [] ;;
- : int list = [1; 2; 3]

# "Hello" :: "World" :: []"
- : string list = ["Hello"; "World"]

# "(1, "Hello") :: (2, "World") :: [] ;;
- : (int * string) list = [(1, "Hello"); (2, "World")]

# (1 :: []) :: (2 :: []) :: [] ;;
- : (int list) list = [[1]; [2]]
```

As the output of the pretty printer suggest, the notation `[e1;
...; en]` is syntactic sugar for the expression `e1 :: ... :: en :: []`.

**Caution**: The list elements are separated by semicolons and not
commas in the `[...]` syntax (which differs from other ML
dialects). In particular, be careful not to get confused when tuple
constructors with optional parenthesis appear inside list constructors:

```ocaml
# [1, 2, 3] ;;
- : (int * int * int) list = [(1, 2, 3)]
```

vs.

```ocaml
# [1; 2; 3] ;;
- : int list = [1; 2; 3]
```

The
[`List`](https://caml.inria.fr/pub/docs/manual-ocaml/libref/List.html)
module of the OCaml standard library provides common 
functions for manipulating lists. In particular, it
provides functions `hd` and `tl` that can be used to decompose a
non-empty list into its components first element (its *head*) and the
remainder of the list (its *tail*):

```ocaml
# let l = [1; 2; 3] ;;
- val l : int list = [1; 2; 3]

# List.hd l ;;
- : int = 1

# List.tl l ;;
- : int list = [2; 3]
```

Equality on lists is defined structurally:

```ocaml
# let l1 = [1; 2; 3] ;;
val l1 : int list = [1; 2; 3]

# let l2 = [2; 3] ;;
val l2 : int list = [2; 3]

# let l3 = 1 :: l2 ;;
val l3 : int list = [1; 2; 3]

# l1 = l3 ;;
-: bool = true
```

Here is a simple function that iterates over an `int list` to compute
the sum of its elements:

```ocaml
let rec sum_of_list l =
  if l = [] then 0 
  else List.hd l + sum_of_list (List.tl l)
```

Note, however, that the idiomatic way for decomposing lists when
implementing such functions is by
using [pattern matching](#pattern-matching).

There is also an in-built operator `@` which can be used to
concatenate two lists:

```ocaml
# [1; 2; 3] @ [4; 5] ;;
- : int list = [1; 2; 3; 4; 5]

# [1] @ [] ;;
- : int list = [1]

# [] @ [] ;;
- : 'a list = []
```



#### Parametric Polymorphism

One key feature of the type systems of languages in the ML family is
that they support *polymorphic types*. Polymorphic types are similar
to generics in languages like Java and Scala. However, they are
slightly different and we will discuss their relationship to generics
later.

More generally, *polymorphism* allows a single piece of code to work
with values of multiple types. The kind of polymorphism that languages
in the ML family support is referred to as *parametric polymorphism*
(or *let-polymorphism*). This can be viewed as allowing functions to
implicitly depend on type parameters. 

As an example, consider the identity function in OCaml:

```ocaml
let id x = x
```

Clearly, `id` does not depend on the specific type of the value passed
to `x` because the function simply returns `x` without doing anything
with it. In fact, OCaml allows `id` to be called with values of
different types:

```ocaml
# id 3 ;;
- : int = 3

# id "banana" ;;
- : string = "banana"
```

The type that the compiler infers for `id` is `'a -> 'a`. A type name
that is preceded with a quote symbol, like `'a`, is a *type
variable*. One typically reads type variables as if they were greek
letters, i.e., `'a` is pronounced "alpha", `'b` is pronounced "beta",
etc.

The type variables occurring in an OCaml type are implicitly
*universally quantified*. That is, the type `'a -> 'a` should be read
as describing functions that for every `'a` can take in a value of
type `'a` and return again a value of type `'a`. The function `id` is
implicitly parameterized by the type `'a` of its parameter `x`. 

The type of `id` tells us that for every call to `id`, the return
value is guaranteed to have the same type as the argument value that
we pass into `id`. Because type variables are implicitly quantified,
they can be renamed without changing the meaning of the type. For
instance, the types `'a -> 'a` and `'b -> 'b` are equivalent. On the
other hand, the types `'a -> 'b -> 'a` and `'a -> 'a -> 'a` are not
equivalent.  However, `'a -> 'b -> 'a` is compatible with the type `'a
-> 'a -> 'a`, but not the other way around - more on that later.

Parametric polymorphism allows us to write generic code while
retaining the benefits of static type checking and inference. 

For example, consider the following code snippet that uses the
function `id`:

```ocaml 
id 3 + 1 
``` 

The type checker can now infer that the addition operation will safely
execute at run-time. The reason is that it knows that `id` is called
with an `int` value (namely `3`) and the type of `id` then tells it
that the result value of `id` will again be of type `int`. Thus, the
addition operation will be applied to two `int` values and is
safe. Note that in order to do this reasoning, the type checker no
longer needs to inspect how `id` is exactly implemented. It only needs
to know `id`'s type, which it infers from the definition of `id` once
and for all.

For example, the following polymorphic function allows us to flip the
order of the arguments of an arbitrary curried function:

```ocaml
# let flip f x y = f y x ;;
val flip : ('a -> 'b -> 'c) -> 'b -> 'a -> 'c
```

That is `flip` takes a function of type `'a -> 'b -> 'c` and returns a
function of type `b -> 'a -> 'c`.

We will discuss later in more detail how the compiler infers this type
from the definition of `flip`.

#### Records

A *record* is a collection of named values called *fields* (think of
them as unordered tuples, whose components have names). Records are
similar to `struct` types in C and C++ and closely related to object
types in object-oriented languages.

Record types take the form `{f1: t1; ...; fn: tn}` where the `fi` are
field names and the `ti` are the types of the fields. The value
constructor of records takes a similar form: `{f1 =
e1; ...; fn = en}`. This expression creates a record value with fields
`f1` to `fn` initialized with the values that `e1` to `en` evaluate
to. Note that record types must be declared explicitly with a type
definition before they can be used. Example:

```ocaml
# type person = 
  { first_name: string;
    last_name: string
  } ;;

# let jane = 
    { first_name = "Jane"; 
      last_name = "Doe"
    } ;;
val jane : person = {first_name = "Jane"; last_name = "Doe"}
```

We can access a field of `f` of a record `x` by using the notation
`x.f`:

```ocaml
# jane.last_name ;;
- : string = "Doe"
```

Record fields are immutable by default (they can be made mutable by
prepending the field name in the record type definition with the
keyword `mutable`. When working with immutable records, instead of
"modifying" the value of a record is done by creating a copy of the
record and changing the value of the field to be modified to its new
value. The old record value thus remains unaffected. OCaml provides a
convenient syntax for copying record fields:

```ocaml
# let john = { jane with first_name = "John } ;;
val john : person = {first_name = "John"; last_name = "Doe"}

# john.first_name ;;
- : string = "John"

# jane.first_name ;;
- : string = "Jane"
```

### Pattern Matching

An important and very convenient feature supported by many functional
programming languages is *pattern matching*. In Class 3, we have
already discussed choice or multi-way select expressions that you will
find in most imperative programming languages. You can think of a
match expressions as a choice expression on steroids. 

A match expression enables you to branch not just on the specific
value of an expression but more generally on the *shape* of that
value, allowing you to simultaneously decompose complex compound
values into their constituents.

In OCaml, match expressions take the form:

```ocaml
match e with
| p1 -> e1
...
| pn -> en
```

This expression first evaluates `e` and then matches the obtained
value `v` against the *patterns* `p1` to `pn`. For the first *matching*
`pi -> ei` whose pattern `pi` matches `v`, the right-hand-side
expression `ei` is evaluated. The value obtained from `ei` is then the
result value of the entire match expression. If no pattern matches,
then a run-time exception will be thrown.

For now, we will focus on the following kinds of patterns `p`:

* a constant literal pattern `c`: here `c` must be a constant literal
  such as `1`, `1.0`, `"Hello"`, `[]`, etc. The pattern `c` matches a
  value `v` iff `v` is equal to `c`.

* a wildcard pattern `_`: matches any value

* a variable pattern `x`: matches any value and binds `x` to that
  value in the right-hand-side expression of the matching.

* a tuple pattern `(p1, ..., pn)`: matches a value `v` if `v` is a
  tuple `(v1, ..., vn)` where `v1` to `vn` are some values matched by
  the patterns `p1` to `pn`.

* a cons pattern `p1 :: p2`: matches values `v` that are lists of the
  form `v1 :: v2` where the head `v1` is matched by `p1` and the tail
  `v2` by `p2`.
  
* choice pattern `p1 | p2`: matches values `v` that are matched by
  pattern `p1` or pattern `p2`. Restriction: if a variable `x` occurs
  in `p1`, it must also occur in `p2` (and vice versa) and `x` must
  have the same type in both patterns.
  
* a variable binding pattern `(p1 as x)`: matches values `v` that are
  matched by `p1` and binds `x` to `v` in the right hand side of the
  matching.

There are some additional types of patterns related to algebraic data
types that we will study later.

Here is how we can define the function `sum_of_list` that we have seen
earlier using pattern matching:

```ocaml
let rec sum_of_list l = 
  match l with
  | [] -> 0
  | hd :: tl -> hd + sum_of_list tl
```

The two patterns in the match expression distinguish between the case
where the list `l` is empty (pattern `[]`) and the case where the list
is non-empty (pattern `hd :: tl`). In the second case, the list will
be decomposed into its head, which will be bound to the variable `hd`
and its tail, which will be bound to the variable `tl`. The right-hand
side of the matching for the non-empty list case then uses `hd` and
`tl` to implement the recursive case of `sum_of_list`. Arguably, this
version of `sum_of_list` is much more readable than the earlier
version.

Note that the names of variables in variable patterns such as `hd` and
`tl` can be chosen freely. They act like implicit variable
declarations. For instance, the following code is equivalent to the
one given above:

```ocaml
let rec sum_of_list l = 
  match l with
  | [] -> 0
  | x :: xs -> x + sum_of_list xs
```

If we define a function whose body is a match expression that matches
the parameter of the function like so:

```ocaml
fun x -> match x with
| p1 -> e1
...
| pn -> en
```

and the parameter `x` is not used in any of the right hand sides of
the matchings `e1` to `en`, then this function expression can be
abbreviated to

```ocaml
function
| p1 -> e1
...
| pn -> en
```

We can thus write the definition of `sum_of_list` even more compactly
like this:

```ocaml
let rec sum_of_list l = 
  match l with
  | [] -> 0
  | hd :: tl -> hd + sum_of_list tl
```

Note that we can also write functions that work on polymorphic lists. 
Here is an example of a function that uses pattern matching to reverse
a list of elements over some arbitrary element type `'a`:

```ocaml
let rec reverse = function
  | [] -> []
  | hd :: tl -> reverse tl @ [hd]

# reverse [1; 2; 3] ;;
- : int list = [3; 2; 1]
```

This implementation of reverse is not very efficient, though. It runs
in linear space and quadratic time in the size of the input list. This
is because the function is not tail-recursive and the list
concatenation operator `@` is linear in the size of its first argument.

Here is a more efficient tail-recursive implementation of `reverse`:

```ocaml
let reverse xs = 
  let rec reverse_helper rev_xs = function
  | hd :: tl -> reverse_helper (hd :: rev_xs) tl
  | [] -> rev_xs
  in reverse_helper [] xs
```

This version runs in constant stack space and linear time.

Here is another example: a by-hand implementation of the in-built
list concatenation operator `@`:

```ocaml
let rec concat xs ys =
  match xs with
  | x :: xs1 -> x :: concat xs1 ys
  | [] -> ys
```

*Exercise*: implement `concat` using tail-recursion.

#### Pattern Guards

**Caution**: A variable name occurring in a pattern will **always** be
interpreted as a variable pattern, which introduces a fresh binding
for that variable, shadowing any earlier binding of the same variable
in the scope of that pattern.

For example, suppose you want to use a match expression to determine
whether an `int` value `x`
against a globally defined constant value `c`:

```ocaml
let c = 42

let deep_thought x = match x with
| c -> "The answer to Life, the Universe, and Everything!"
| _ -> "This is not the answer. Calling the Vogons..."
```

Calling `deep_thought` with any `int` value will always cause the
function to take the first matching of the match expression because
the pattern `c` introduces a fresh binding for `c` that, in this case,
matches any value.

We can avoid this problem by using a different variable name for the
`c` in the pattern and then enforce the equality of the values matched
by this variable using a *pattern guard*:

```ocaml
let c = 42

let deep_thought x = match x with
| c1 when c1 = c -> "The answer to Life, the Universe, and Everything!"
| _ -> "This is not the answer. Calling the Vogons..."
```

The guard expression `c1 = c` is evaluated after the pattern `c1` has
been matched successfully. The right hand side
expression of the matching is then only evaluated if the guard
expression also evaluates to `true`. If the guard expression evaluates
to `false`, the next matching is tried. In general, any expression of
type `bool` can be used as a pattern guard.

Arguably, a cleaner implementation of `deep_thought` would use a
simple conditional expression to check equality between `x` and `c`
directly. So match expressions do not automatically lead to cleaner
and more readable code.

Let's look at a more reasonable example of pattern guards that also
demonstrates the use of patterns for *deep matching* of values.

Our goal is to write a function `remove_duplicates` that removes
consecutive duplicate elements in a given list:

```ocaml
# remove_duplicates [1; 1; 2; 1; 3; 3]
- : int list = [1; 2; 1; 3]
```

Here is a first short at implementing this function:

```ocaml
let rec remove_duplicates = function
  | hd :: hd :: tl -> remove_duplicates (hd :: tl)
  | hd :: tl -> hd :: remove_duplicates tl
  | [] -> []
```

The code nicely expresses the intend: if we see two consecutive
occurrences of the same element `hd` at the beginning of the list, we
drop the first one and recursively process the tail, keeping the
second one for now. Otherwise, the list either contains fewer than two
elements or the first two elements are different. These cases are
handled by the second and third pattern.

Unfortunately, this code does not compile because variables that are
used in variable patterns are only allowed to occur once within the
same pattern:
```
Error: Variable hd is bound several times in this matching
```

We can avoid this problem by using different variable names for the
two occurrences of `hd` in the pattern of the first matching and then
enforce the equality of the values matched by these variables using a
*pattern guard*:

```ocaml
let rec remove_duplicates = function
  | hd1 :: hd2 :: tl when hd1 = hd2 -> remove_duplicates (hd2 :: tl)
  | hd :: tl -> hd :: remove_duplicates tl
  | [] -> []
```

Again, the guard expression `hd1 = hd2` is evaluated after the pattern
`hd1 :: hd2 :: tl` has been matched successfully, checking whether the
two first elements are equal. If they are not, we proceed with the
next pattern `hd :: tl`.

This version compiles and works as expected:

```ocaml
# remove_duplicates [1; 1; 2; 1; 3; 3]
- : int list = [1; 2; 1; 3]
```

Here is a slightly optimized version of the above implementation that
uses a variable binding pattern to avoid the allocation of a new cons
node for the tail list passed to the recursive call in the right hand
side of the first matching:

```ocaml
let rec remove_duplicates = function
  | hd1 :: (hd2 :: _ as tl) when hd1 = hd2 -> remove_duplicates tl
  | hd :: tl -> hd :: remove_duplicates tl
  | [] -> []
```

The pattern `(hd2 :: _ as tl)` used in the first matching binds `tl`
to the value matched by the pattern `hd2 :: _`.

##### Checking Exhaustiveness

One nice feature of OCaml's static type checker is that it can help us
ensure that pattern matching expressions are exhaustive. For instance,
suppose we write something like:

```ocaml
match l with
| hd :: _ -> hd
```

Then the compiler will warn us that we have not considered all
possible cases of values that `l` can evaluate to. The compiler will
even provide examples of values that we have not considered in the
match expression: in this case `[]`. This feature is particularly
useful for match expressions that involve complex nested patterns.
