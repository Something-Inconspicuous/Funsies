"""
Python Turtle Documentation
https://docs.python.org/3/library/turtle.html
"""
import turtle
import random


# spiral shape from Runestone Academy
# https://runestone.academy/ns/books/published/ADS23/Recursion/VisualizingRecursion.html

# modify the code to produce a more complex spiral shape - output must be unique (you may discuss but not duplicate code)

def draw_spiral(my_turtle, line_len, theta_deg):
	if line_len > 0:
		my_turtle.forward(line_len)
		my_turtle.right(theta_deg)
		# now changes the angle each time
		draw_spiral(my_turtle, line_len - 5, theta_deg + 1)

# my_turtle = turtle.Turtle()
# my_win = turtle.Screen()
# draw_spiral(my_turtle, 100, 45)
# my_win.exitonclick()


def lerp_col(col1: tuple, col2: tuple, t: float) -> tuple:
	return (lerp(col1[0], col2[0], t), lerp(col1[1], col2[1], t), lerp(col1[2], col2[2], t))

def lerp(a: float, b: float, t: float) -> float:
	return (b - a) * t + a


# fractal tree from Runestone Academy
# https://runestone.academy/ns/books/published/ADS23/Recursion/VisualizingRecursion.html

# modify the fractal tree - make all of the modifications below

# 1) Modify the thickness of the branches so that as the branch_len gets smaller, the line gets thinner.

# 2) Modify the color of the branches so that as the branch_len gets very short it is colored like a leaf.

# 3) Modify the angle used in turning the turtle so that at each branch point the angle is selected at random in some range. For example choose the angle between 15 and 45 degrees. Play around to see what looks good.

# 4) Modify the branch_len recursively so that instead of always subtracting the same amount you subtract a random amount in some range.

def tree(branch_len, t):
	if branch_len > 5:
		# shortening width
		thickness = branch_len / 10
		t.width(thickness)

		# leaf color gradient
		color_grad = min(1.0, branch_len / 50.0)
		WOOD_COL = (0.59, 0.29, 0.00)
		LEAF_COL = (0.12, 0.70, 0.00)
		t.color(lerp_col(LEAF_COL, WOOD_COL, color_grad))

		# angle random
		ltheta = random.randrange(10, 40)
		rtheta = random.randrange(10, 40)

		# length random
		ldx = random.randrange(5, 15)
		rdx = random.randrange(5, 15)

		t.forward(branch_len)
		t.right(ltheta)
		tree(branch_len - ldx, t)
		t.left(ltheta)
		t.left(rtheta)
		tree(branch_len - rdx, t)
		t.right(rtheta)
		t.penup()
		t.backward(branch_len)
		t.pendown()

def main():
	t = turtle.Turtle()
	t.speed(0)
	my_win = turtle.Screen()
	t.left(90)
	t.up()
	t.backward(100)
	t.down()
	t.color("brown")
	tree(75, t)
	my_win.exitonclick()

# uncomment next line to see output
# remember to comment out spiral shape above
main()

