# dfs - stack ? or recursive way?


"""
       1
    /  |  \
   2   5   9
   |   /\  |
   3  6  8 10
  /   |
 4    7
"""

graph = {
    1: [2,5,9],
    2: [3],
    3: [4],
    4: [],
    5: [6,8],
    6: [7],
    7: [],
    8: [],
    9: [10],
    10: []
}


def recursive_dfs(v, visited=None):
    if visited is None:
        visited = list()
    visited.append(v)  # 시작 정점 방문
    for w in graph[v]:
        if w not in visited:  # 방문 하지 않았으면
            visited = recursive_dfs(w, visited)
    return visited


def iterative_dfs(start_v):
    visited = []
    stack = [start_v]
    while stack:
        v = stack.pop()
        if v not in visited:
            visited.append(v)
            for w in graph[v]:
                stack.append(w)
    return visited


print("recursive_dfs: ", recursive_dfs(1))
print("iterative_dfs: ", iterative_dfs(1))

# 스택은 마지막에 스택에 담은 정점부터 꺼내져 방문되기 때문에
# 재귀 방식과 결과가 다름.
"""
recursive_dfs:  [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
iterative_dfs:  [1, 9, 10, 5, 8, 6, 7, 2, 3, 4]
"""