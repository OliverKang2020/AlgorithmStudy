
graph = dict()

graph['A'] = ['B', 'C']
graph['B'] = ['A', 'D']
graph['C'] = ['A', 'G', 'H', 'I']
graph['D'] = ['B', 'E', 'F']
graph['E'] = ['D']
graph['F'] = ['D']
graph['G'] = ['C']
graph['H'] = ['C']
graph['I'] = ['C', 'J']
graph['J'] = ['I']


# Stack
def dfs_stack(start_v):
    visited, stack = list(), list(start_v)
    while stack:
        v = stack.pop()
        if v not in visited:
            visited.append(v)
            for w in graph[v]:  # stack.extend(_graph[node])
                stack.append(w)
    return visited


print(dfs_stack('A'))


# Recursion
def dfs_recursion(node, visited=None):
    if visited is None:
        visited = list()
    visited.append(node)
    for w in graph[node]:
        if w not in visited:
            visited = dfs_recursion(w, visited)
    return visited


print(dfs_recursion('A'))
