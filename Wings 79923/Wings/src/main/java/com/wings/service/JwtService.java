package com.wings.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

//@Component
//public class JwtService { //OR JwtUtil
//
//    public static final String SECRET = "5367566859703373367639792F423F4528482B4D625165546857605A71347"; //greater than 256 bits (*8)
//    public static final long JWT_TOKEN_VALIDITY = 900000; // 15 minutes
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    private Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date()); //Is expiry date BEFORE the current date?
//    }
//
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    public String generateToken(String userName) {
//        Map<String, Object> claims = new HashMap<>();
//        return createToken(claims, userName);
//    }
//
//    private String createToken(Map<String, Object> claims, String userName) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(userName)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    private Key getSignKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}


// -----XXXXXXXX--------OR---------XXXXXXXXXXX


//@Component
//public class JwtService { //OR JwtUtil
//
//    public static final String SECRET = "5367566859703373367639792F423F4528482B4D625165546857605A71347"; //greater than 256 bits (*8)
//    public static final long EXPIRATION = 900000; // 15 minutes
//
//    //Decoding/Parsing
//    public Claims extractAllClaims(String token) {
//    	return Jwts
//    			.parser()
//    			.setSigningKey(SECRET)
//    			.parseClaimsJws(token)
//    			.getBody();
//    }
//    
//    public String extractUsername(String token) {
//    	Claims claims = extractAllClaims(token);
//    	return claims.getSubject();
//    }
//    
//    public Date extractExpiry(String token) {
//    	Claims claims = extractAllClaims(token);
//    	return claims.getExpiration();
//    }
//    
//    //Encoding/Generation
//    public String generateToken(String username) {
//    	return Jwts
//    			.builder()
//    			.signWith(SignatureAlgorithm.HS256, SECRET)
//    			.addClaims(new HashMap<>())
//    			.setSubject(username)
//    			.setIssuedAt(new Date(System.currentTimeMillis()))
//    			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
//    			.compact();
//    }
//    
//    //Validation
//    public boolean validateToken(String token, UserDetails user) {
//    	String username = extractUsername(token);
//    	Date expiry = extractExpiry(token);
//    	return username.equals(user.getUsername()) && expiry.after(new Date(System.currentTimeMillis()));
//    }
//}

@Component
public class JwtService{
	
	public final static String SecretKey = "IamSecretKeyIamSecretKeyIamSecretKeyIamSecretKeyIamSecretKeyIamSecretKey";
	
	public final static Long EXP = 24*60*60*1000l;
	
	public Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.setSigningKey(SecretKey)
				.parseClaimsJws(token)
				.getBody();
	}
	
	public String extractUsername(String token) {
		Claims claims = extractAllClaims(token);
		return claims.getSubject();
	}
	
	public Date extractExpiration(String token) {
		Claims claims = extractAllClaims(token);
		return claims.getExpiration();
	}
	
	public String generateToken(String username) {
		return Jwts
				.builder()
				.signWith(SignatureAlgorithm.HS256, SecretKey)
				.addClaims(new HashMap<>())
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXP))
				.compact();
	}
	
	public boolean validateToken(String token, UserDetails user) {
		String username = extractUsername(token);
		Date expiration = extractExpiration(token);
		
		return username.equals(user.getUsername()) && expiration.after(new Date(System.currentTimeMillis()));
	}
}



