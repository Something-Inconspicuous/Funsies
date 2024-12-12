def main():
    print("====countDown====")
    countDown(14)
    print("======evens======")
    evens(13, 100)
    print("====multsOfX=====")
    rec_mults_of_x(13, 14, 104)
    print("======double=====")
    arr = [1, 2, 4, 5, 16]
    double(arr)
    print(arr)


def countDown(n):
    if(n > 0):
        print(n)
        countDown(n - 1)
def countUp(n, start = 0):
    if(start < n):
        print(start)
        countDown(start + 1)

def double(arr, i = 0):
    if i < len(arr):
        arr[i] *= 2
        double(arr, i + 1)

def evens(a, b):
    mults_of_x(2, a, b)

def mults_of_x(x, a, b):
    a -= 1
    b -= 1
    a -= a % x - x
    while a < b:
        print(a)
        a += x

def rec_mults_of_x(x, a, b):
    a -= 1
    b -= 1
    a -= a % x - x
    m_rec_mults_of_x(x, a, b)
    
def m_rec_mults_of_x(x, a, b):
    if(a < b):
        print(a)
        m_rec_mults_of_x(x, a + x, b)

main()
