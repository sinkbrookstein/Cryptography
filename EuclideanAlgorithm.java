/*
 * Useful applications of Euclidean Algorithm
 * Author: Sydney Brookstein
 * Tpcs: Cryptography, University of Denver
 * May 2019
 */

public class EuclideanAlgorithm {
	
	//iteratively finds greatest common divisor of n, m
	public static int iterativeGCD(int n, int m) {
		while(m != 0) {
			int temp = m;
			m = n%m;
			n = temp;
		}
		return n;
	}
	
	//iteratively finds m inverse (mod n) using Euclidean algorithm
	public static int inverse(int n, int m) {
		int originalN = n;
		int prevprev = 0;
		int prev = 1;
		int quotient = -n/m;
		int remainder = n%m;
		int inverse = 1;
		while(remainder != 0) {
			inverse = prevprev + quotient*inverse;
			n = m;
			m = remainder;
			quotient = -n/m;
			remainder = n%m;
			prevprev = prev;
			prev = inverse;
		}
		if(m != 1) {
			//this means gcd != 1, so m has no inverse
			return 0;
		}
		//if number is negative, return it's corresponding positive number
		if(inverse < 0) {
			return originalN + inverse;
		}
		return inverse;
	}
	
	//finds and prints numbers with a multiplicative inverse (mod n)
	//returns phi(n), the total number of numbers that have a multiplicative inverse (mod n)
	public static int multInverses(int n) {
		int count = 0;
		for(int i = 1; i <= n; i++) {
			int inverse = inverse(n, i);
			if(inverse != 0) {
				//print out inverse pairs
				System.out.println("(" + i + ", " + inverse + ")");
				count++;
			}
		}
		return count;
	}
	
	//solves congruence x = i1 (mod m1), x = i2 (mod m2) using Chinese remainder theorem
	public static int solveCongruence(int i1, int m1, int i2, int m2) {
		int inverse1 = inverse(m2, m1);
		int inverse2 = inverse(m1, m2);
		int ans = (i2*inverse1*m1 + i1*inverse2*m2)%(m1*m2);
		return ans;
	}

	public static void main(String[] args) {
		

	}

}
