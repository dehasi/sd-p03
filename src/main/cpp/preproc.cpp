// g++ -E preproc.cpp
#define N 129

int main(int argc, const char * argv[]) {
    #if 1
        int a = N;
    #endif

    #if 0
        int B  = N;
    #endif
  return 0;
}