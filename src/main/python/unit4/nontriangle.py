'''
Python Turtle Documentation
https://docs.python.org/3/library/turtle.html
'''
import turtle

# Sierpinski Triangle from Runestone Academy
# https://runestone.academy/ns/books/published/ADS23/Recursion/SierpinskiTriangle.html

# modify the code to produce a NON-TRIANGULAR complex fractal that diminishes in size - output must be unique (you may discuss but not duplicate code)

def draw_triangle(points, color, my_turtle):
		my_turtle.fillcolor(color)
		my_turtle.up()
		my_turtle.goto(points[0][0], points[0][1])
		my_turtle.down()
		my_turtle.begin_fill()
		my_turtle.goto(points[1][0], points[1][1])
		my_turtle.goto(points[2][0], points[2][1])
		my_turtle.goto(points[0][0], points[0][1])
		my_turtle.end_fill()

def draw_polygon(self: turtle.Turtle, num_sides: int, side_len: float, color = None):
	theta: float = 360.0 / num_sides
	old_fc = self.fillcolor()
	if(color != None):
		self.begin_fill()
		self.fillcolor(color)
	for i in range(num_sides):
		self.forward(side_len)
		self.left(theta)
	if(color != None):
		self.end_fill()
		self.fillcolor(old_fc)

turtle.Turtle.draw_polygon = draw_polygon

def draw_hexagon(self: turtle.Turtle, side_len: float, color = None):
	draw_polygon(self, 6, side_len, color)


turtle.Turtle.draw_hexagon = draw_hexagon


def get_mid(p1, p2):
		return ((p1[0] + p2[0]) / 2, (p1[1] + p2[1]) / 2)


def sierpinski(points, degree, my_turtle):
		colormap = ["blue", "red", "green", "white", "yellow", "violet", "orange"]
		draw_triangle(points, colormap[degree], my_turtle)
		if degree > 0:
				sierpinski(
						[points[0], get_mid(points[0], points[1]), get_mid(points[0], points[2])],
						degree - 1,
						my_turtle,
				)
				sierpinski(
						[points[1], get_mid(points[0], points[1]), get_mid(points[1], points[2])],
						degree - 1,
						my_turtle,
				)
				sierpinski(
						[points[2], get_mid(points[2], points[1]), get_mid(points[0], points[2])],
						degree - 1,
						my_turtle,
				)

def rose(turtle: turtle.Turtle, r: float, theta: float, depth = 0):
    colormap = ["blue", "red", "green", "white", "yellow", "violet", "orange"]
    turtle.fillcolor(colormap[depth % len(colormap)])
    turtle.begin_fill()
    turtle.draw_hexagon(r)
    turtle.end_fill()
    turtle.left(theta)
    if(r > 5):
        rose(turtle, r - 1, theta + 2, depth + 1)
		
def draw_poly(self: turtle.Turtle, points):
	self.penup()
	self.goto(points[0])
	self.begin_fill()
	self.pendown()
	for point in points:
		self.goto(point)
	self.end_fill()
	
turtle.Turtle.poly = draw_poly

def box(t, points, depth):
    colormap = ["blue", "red", "green", "black", "yellow", "violet", "orange"]
    t.color(colormap[depth % len(colormap)])
    t.poly(points)
    if depth > 0:
        box(t, 
            [
				points[0], 
			    get_mid(points[0], points[1]), 
				get_mid(points[0], points[2]),
				get_mid(points[0], points[3])
				
            ], depth - 1
            )
        box(t, 
            [
				points[2], 
			    get_mid(points[2], points[3]), 
				get_mid(points[2], points[0]),
				get_mid(points[2], points[1])
				
            ], depth - 1
            )

def main():
    my_turtle = turtle.Turtle()
    my_win = turtle.Screen()
    my_turtle.speed(0)
    # my_points = [[-180, -150], [0, 150], [180, -150]]
    points = [[-150, -150], [150, -150], [150, 150], [-150, 150]]
    # sierpinski(my_points, 5, my_turtle)
    box(my_turtle, points, 9)
    my_win.exitonclick()

main()
