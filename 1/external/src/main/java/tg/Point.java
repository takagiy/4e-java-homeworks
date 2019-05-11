package tg;

//Turtle Graphics Library for the book "Java for Everyone, 3rd Edition".
//Copyright (C) 2000, 2006, 2016, Hideki Tsuiki, Kyoto University


public class Point{
	public double x;
	public double y;
	public Point(double x, double y){
		this.x = x; this.y = y;
	}
	public String toString(){ 
		return String.format("(%3.1f, %3.1f)", x, y);
	}
}
