/*!
 * @file
 * @brief Implements single linked list.
 *
 */

#include <stdio.h>
#include <stdlib.h>

enum
{
   FirstNodeData = 10,
   SecondNodeData = 20
};

typedef struct Node_t
{
   struct Node_t *next;
   int data;
} Node_t;

void PushFront(Node_t *node, int data)
{
   Node_t *newNode = malloc(sizeof(Node_t));

   newNode->next = node->next;
   newNode->data = data;

   node->next = newNode;
}

int main()
{
   Node_t *head = malloc(sizeof(Node_t));
   head->next = NULL;

   PushFront(head, FirstNodeData);
   PushFront(head, SecondNodeData);

   // Node_t *node1 = malloc(sizeof(Node_t));
   // head->next = node1;
   // node1->data = FirstNodeData;

   // Node_t *node2 = malloc(sizeof(Node_t));
   // node1->next = node2;
   // node2->data = SecondNodeData;
   // node2->next = NULL;

   Node_t *currentNode = head->next;
   while(currentNode != NULL)
   {
      printf("current node data: %d\n", currentNode->data);
      currentNode = currentNode->next;
   };

   currentNode = head->next;
   while(currentNode != NULL)
   {
      free(currentNode);
      currentNode = currentNode->next;
   };
   free(head);

   return 0;
}
