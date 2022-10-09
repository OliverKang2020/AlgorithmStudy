
from collections import deque

n, m, start = map(int, input().split())

print(n)
print(m)
print(start)

graph = [[] for _ in range(n+1)]

for _ in range(m):
    a, b = map(int, input().split())
    graph[a].append(b)
    graph[b].append(a)

for _i in range(len(graph)):
    graph[_i].sort()


def dfs(node, visited=None):
    if visited is None:
        visited = list()  # alternative: visited[node] = True
    print(node, end=' ')
    visited.append(node)
    for w in graph[node]:
        if w not in visited:  # alternative: if not visited[i]:
            dfs(w, visited)


# bfs
# from collections import deque
# queue = deque([node])

def bfs(node):
    queue = deque([node])
    visited = list()

    while queue:
        v = queue.popleft()
        if v not in visited:
            print(v, end=' ')
            visited.append(v)
            for w in graph[v]:
                queue.append(w)


dfs(start)
print()
bfs(start)
