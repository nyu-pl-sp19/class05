

def foo(x: Int, p: Int => Unit): Unit = {
  def bar(y: Int): Unit = {
    println(x)
    println(y)
  }
  if (x > 1) p(x) else foo(2, bar)
  
}

def f(x: Int): Unit = {
  println("hello")
}

foo(1, f)