/*!
 * @file
 * @brief
 *
 * Copyright (c) Junho Kang
 */

#include <pthread.h>

typedef struct
{
   int numberOfRandomPoints;
   int numberOfThreads;
   int topBoundaryValue;
   int bottomBoundaryValue;
   double (*func)(double x);
} MonteCarloCalculationConfiguration_t;

typedef struct
{
   struct
   {
      const MonteCarloCalculationConfiguration_t *config;
      double integral;
      pthread_mutex_t mutex_lock;
   } _private;
} MonteCarloCalculation_t;

void MonteCarloCalculation_Init(
   MonteCarloCalculation_t *instance,
   const MonteCarloCalculationConfiguration_t *config);

double MonteCarloCalculation_Calculate(MonteCarloCalculation_t *instance);
