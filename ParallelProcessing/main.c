#include "MonteCarloCalculation.h"
#include <time.h>
#include <math.h>
#include <stdio.h>

double func1(double x)
{
   return 4.0 / (double)(1 + x * x);
}

double func2(double x)
{
   return sqrt(x + sqrt(x));
}

static const MonteCarloCalculationConfiguration_t config1 = 
{
   .numberOfRandomPoints = 1000000000,
   .numberOfThreads = 6,
   .topBoundaryValue = 4,
   .bottomBoundaryValue = 0,
   .func = func1
};

static const MonteCarloCalculationConfiguration_t config2 = 
{
   .numberOfRandomPoints = 1000000000,
   .numberOfThreads = 10,
   .topBoundaryValue = 2,
   .bottomBoundaryValue = 0,
   .func = func2
};

int main()
{
   MonteCarloCalculation_t instance;
   MonteCarloCalculation_Init(&instance, &config1); // switch config1 or config2 here.

   struct timespec begin, end;
   double elapsed;
   clock_gettime(CLOCK_MONOTONIC, &begin);

   double result = MonteCarloCalculation_Calculate(&instance);
   clock_gettime(CLOCK_MONOTONIC, &end);

   elapsed = end.tv_sec - begin.tv_sec;
   elapsed += (end.tv_nsec - begin.tv_nsec) / 1000000000.0;

   printf("\n\nnumber of threads: %d \n", config1.numberOfThreads); // switch config1 or config2 here.
   printf("result: %lf \n", result);
   printf("elapsed time: %lf \n", elapsed);

   return 0;
}
