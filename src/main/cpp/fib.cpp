//  g++ fib.cpp -o fib
#include <iostream>

using namespace std;

int fib(int n) {
    if(n < 2) return 1;
    return fib(n-1) + fib(n-2);
}

int main(int argc, const char * argv[]) {

    int num = atoi(argv[1]);
    cout << fib(num) << std::endl;
  return 0;
}


