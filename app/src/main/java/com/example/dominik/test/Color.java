package com.example.dominik.test;

import java.io.Serializable;

		public enum Color implements Serializable {
	BLUE, GREEN, YELLOW, RED, SILVER, VORTEX;

			public String toString(Color color) {
				String Color = "";
				switch(color) {
					case BLUE: Color = "BLUE";
						break;
					case GREEN: Color = "GREEN";
						break;
					case YELLOW: Color = "YELLOW";
						break;
					case RED: Color = "RED";
						break;
					case SILVER: Color = "SILVER";
						break;
					case VORTEX: Color = "VORTEX";
						break;
					default:
						break;
				}
				return Color;


			}
}



