
# divisor

n, k = map(int, input().split())

result = list()
for _n in range(n):
    if n % (_n+1) == 0:
        result.append(_n+1)

if len(result) >= k:
    print(result[k-1])
else:
    print(0)

