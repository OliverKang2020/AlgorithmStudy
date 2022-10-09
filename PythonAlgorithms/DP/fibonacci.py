
def fibo(x):
    if x == 1 or x == 2:
        return 1
    return fibo(x-1) + fibo(x-2)


d = [0] * 100


def fibo_memo(x):
    if x == 1 or x == 2:
        return 1
    if d[x] == 0:
        d[x] = fibo_memo(x-1) + fibo_memo(x-2)
    return d[x]


print(fibo_memo(99))


def fibo_dp_bottom_up(n):
    _d = [0] * 100
    _d[1] = 1
    _d[2] = 1
    for i in range(3, n+1):
        _d[i] = _d[i-1] + _d[i-2]
    return _d[n]


print(fibo_dp_bottom_up(99))