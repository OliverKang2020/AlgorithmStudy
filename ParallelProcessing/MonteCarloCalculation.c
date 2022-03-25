/*!
 * @file
 * @brief
 *
 * Copyright (c) Junho Kang
 */

#include "MonteCarloCalculation.h"
#include <stdlib.h>
#include <stdio.h>
#include <time.h>

static MonteCarloCalculation_t *CalInstance;

static double generateRandomValue(int max, int min)
{
   return ((max - min) * (rand() / (double) RAND_MAX)) + min;
}

static void *ThreadRoutine(void *context)
{
   long threadCount = (long)context;
   double sum = 0;

   int rangeLimit = CalInstance->_private.config->numberOfRandomPoints / CalInstance->_private.config->numberOfThreads;
   int min = rangeLimit * threadCount;
   int max = (threadCount + 1 == CalInstance->_private.config->numberOfThreads)?
      CalInstance->_private.config->numberOfRandomPoints : (threadCount + 1) * rangeLimit;

   for(int randomIndex = min; randomIndex < max; randomIndex++)
   {
      double random = generateRandomValue(
         CalInstance->_private.config->topBoundaryValue,
         CalInstance->_private.config->bottomBoundaryValue);
      sum += CalInstance->_private.config->func(random);
   }

   pthread_mutex_lock(&CalInstance->_private.mutex_lock);
   printf("\nthread count %ld, minRandom: %d, maxRandom: %d\n", threadCount, min, max);
   printf("thread count %ld, sum: %lf, accum: %lf\n", threadCount, sum, CalInstance->_private.integral);
   CalInstance->_private.integral += sum;
   pthread_mutex_unlock(&CalInstance->_private.mutex_lock);
   pthread_exit(NULL);
}

void MonteCarloCalculation_Init(
   MonteCarloCalculation_t *instance,
   const MonteCarloCalculationConfiguration_t *config)
{
   instance->_private.config = config;
}

double MonteCarloCalculation_Calculate(MonteCarloCalculation_t *instance)
{
   CalInstance = instance;
   instance->_private.integral = 0;
   if(instance->_private.config->numberOfThreads == 0)
   {
      for(int randomIndex = 0; randomIndex < instance->_private.config->numberOfRandomPoints; randomIndex++)
      {
         double random = generateRandomValue(
            instance->_private.config->topBoundaryValue,
            instance->_private.config->bottomBoundaryValue);
         instance->_private.integral += instance->_private.config->func(random);
      }
   }
   else
   {
      pthread_t thread[instance->_private.config->numberOfThreads];
      srand((unsigned)time(NULL));
      pthread_mutex_init(&instance->_private.mutex_lock, NULL);

      for(long index = 0; index < instance->_private.config->numberOfThreads; index++)
      {
         int thr_id = pthread_create(&thread[index], NULL, ThreadRoutine, (void *)index);
         if(thr_id < 0)
         {
            char *str;
            sprintf(str, "pthread %ld create error", index);
            perror(str);
            exit(EXIT_FAILURE);
         }
      }

      for(int index = 0; index < instance->_private.config->numberOfThreads; index++)
      {
         pthread_join(thread[index], NULL);
      }
   }

   instance->_private.integral *= (
      (double)(instance->_private.config->topBoundaryValue - instance->_private.config->bottomBoundaryValue) 
      / instance->_private.config->numberOfRandomPoints);
   return instance->_private.integral;
}
