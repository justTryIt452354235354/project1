package org.cmu.cc.q2;

import java.math.BigInteger;

public class PDC {
	private static BigInteger Y;
	private static String C;
	private static BigInteger X = new BigInteger("12389084059184098308123098579283204880956800909293831223134798257496372124879237412193918239183928140");
	private static BigInteger Z;
	private static int K;
	private static String I;
	
	
	public boolean isValidMessage(String message) {
		int length = message.length();
		int i = 1;
		while (length > 0) {
			length = length - i;
			i = i + 1;
		}
		if (length == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public String decryption(String key, String message) {
		Y = new BigInteger(key);
		this.C = message;
		KeyGen();
		generateK();		
		reverseRotate(C);
		String response = reverseCaesarify();
		return response;
	}
	
	private static void KeyGen() {
		//System.out.println("X: " + X);
		//System.out.println("Y: " + Y);
		
		// generate Z
		String strX = X.toString();
		String strY = Y.toString();
		
		int times = strX.length() - strY.length() + 1;
		int xLength = strX.length();
		int yLength = strY.length();
		String tmpY = strY;
		String tmpSum = "";
		for (int i = 0; i < times; i++) {
			String tmpX = strX.substring(i, i + yLength);
			char[] x = tmpX.toCharArray();
			char[] y = tmpY.toCharArray();
			for (int j = 0; j < x.length; j++) {
				int a = x[j] - 48 + y[j] - 48;
				if (a >= 10) 
					a = a - 10;
				tmpSum += a;
			}
			tmpY = tmpSum;
			tmpSum = "";
		}
		Z = new BigInteger(tmpY);
		//System.out.println("Z: " + Z);
	}
	
	private static void generateK() {
		BigInteger tmpK = Z.remainder(new BigInteger("25")).add(new BigInteger("1"));
		K = tmpK.intValue();
		//System.out.println("K: " + K);
	}
	
	private static void reverseRotate(String s) {
		// get side
		int length = s.length();
		char[] tmp = s.toCharArray();
		int side = 1;
		while (length > 0) {
			length = length - side;
			if (length != 0) {
				side = side + 1;
			}
		}
		//System.out.println("side:" + side);
		
		// fill in a triangle
		char[][] triangle = new char[side][side];
		fill(triangle, 0, 0, side, tmp, 0);
		
		System.out.println("Triangle:");
		//printTriangle(triangle);
		
		// reverse rotate a triangle
		char[][] ch = new char[side][side];
		for (int i = 0; i < side; i++) {
			int xStart = side - i - 1;
			int yStart = side - i - 1;
			for (int j = 0; j < i + 1; j++) {
				ch[i][j] = triangle[xStart][yStart];
				xStart++;
			}
		}
		
		System.out.println("Rotate Triangle:");
		//printTriangle(ch);
		
		String result = "";
		for (int i = 0; i < side; i++) {
			for (int j = 0; j < i + 1; j++) {
				result += ch[i][j];
			}
		}
		I = result;
		//System.out.println("I: " + I);
	}
	
	private static void fill(char[][] triangle, int xStart, int yStart, int offset, char[] origin, int index) {
		if (offset <= 0)
			return;
		int x = xStart;
		int y = yStart;
		int addIndex = index;	
		for (int i = 0; i < offset - 1; i++) {
			triangle[x][y] = origin[addIndex];
			addIndex++;
			x++;
		}
		
		y = yStart;
		for (int j = 0; j < offset - 1; j++) {
			triangle[x][y] = origin[addIndex];
			addIndex++;
			y++;
		}
	
		for (int k = 0; k < offset - 1; k++) {
			triangle[x][y] = origin[addIndex];
			x--;
			y--;
			addIndex++;
		}		
		
		fill(triangle, xStart + 2, yStart + 1, offset - 3, origin, addIndex);
	}
	
	
	private static void printTriangle(char[][] triangle) {
		for (int i = 0; i < triangle.length; i++) {
			for (int j = 0; j < i+1; j++) {
				System.out.print(triangle[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	private static String reverseCaesarify() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < I.length(); i++) {
			if (Character.isUpperCase(I.charAt(i))) {
				char ch = (char)(((int)I.charAt(i) - K - 65) % 26 + 65);
				sb.append(ch);
			} else {
				char ch = (char)(((int)I.charAt(i) - K - 97) % 26 + 97);
				sb.append(ch);
			}
		}
		//System.out.println("result:" + sb.toString());
		return sb.toString();
	}
}
