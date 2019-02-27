#include <stdio.h>

// non-tail recursive factorial
int fac(int n) {
  if (n == 0) return 1;
  else return n * fac(n - 1);
}

// tail-recursive factorial
int fac_tr_loop(int n, int acc) {
  if (n == 0) return acc;
  else return fac_tr_loop(n - 1, n * acc);
}

int fac_tr(int n) {
  return fac_tr_loop(n, 1);
}

int main (int argc, char* argv[]) {

  printf("fac(5) = %d\n", fac_tr(5));

  printf("fac(5) = %d\n", fac(5));
  
  return 0;
}
