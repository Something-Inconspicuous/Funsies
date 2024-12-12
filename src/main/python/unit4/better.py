def triangular_num(n: int) -> int:
    if n == 0: return 0
    return n + triangular_num(n - 1)

def pow(base: float, exp: int) -> float:
    if exp == 0: return 1.0
    if exp == 1: return base
    return base * pow(base, exp - 1)

def num_chars(arr: list[str], a = 0, b: int | None = None) -> int:
    if b == None: b = len(arr)
    if a >= b: return 0
    return len(arr[a]) + num_chars(arr, a + 1, b)

def nth_sqaure(n: int) -> int:
    if n == 0: return 0
    if n == 1: return 1

    # but, like... why?
    return (n * 2 - 1) + nth_sqaure(n - 1)
    

def fib(n: int) -> int:
    if n <= 1: return n
    if n == 2: return 1
    return fib(n - 1) + fib(n - 2)

print("=====tri========") # 6
print(triangular_num(3))      

print("=====pow========") # 81
print(pow(3, 4))   

print("===num_cahrs====") # 8
print(num_chars(["aaa", "bbbb", "c"]))

print("===nth_square===") # 16
print(nth_sqaure(4)) 

print("======fib======") # 987
print(fib(16))