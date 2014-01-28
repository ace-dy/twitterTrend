package com.gsu.TwitterTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterInRussia {
	public static void main(String[] args) {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			ResponseList<Location> locations;
			ArrayList<String> list = new ArrayList<String>();
			locations = twitter.getAvailableTrends();
			for (Location location : locations) {
				if (location.getName().equals("Russia")) {
					Trends trends = twitter.getLocationTrends(location
							.getWoeid());
					Trend trend[] = trends.getTrends();
					for (int i = 0; i < trend.length; i++) {
						// System.out.println(trend[i].getName());
						list.add(i, trend[i].getName());
					}
					insertData(list);
				}
			}
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get trends: " + te.getMessage());
			System.exit(-1);
		}
	}

	public static void insertData(ArrayList<String> trend) {
		try {
			// Load the driver
			Class.forName("com.nuodb.jdbc.Driver");
			// Create the connection
			Connection conn = DriverManager.getConnection(
					"jdbc:com.nuodb://localhost/dykim", "nuodb", "nuodb123");
			// Create a PreparedStatement class to execute the SQL statement
			PreparedStatement stmt = conn
					.prepareStatement("insert into user.twit_russia (trend_name, trend_ord) values (?, ?)");
			for (int i = 0; i < trend.size(); i++) {
				try {
					stmt.setString(1, trend.get(i));
					stmt.setInt(2, i);
					stmt.addBatch();
					stmt.executeBatch();
				} catch (Exception exception) {
					System.out.println("Skipping insert...");
				}
			}
			conn.commit();

			System.out.println("Insert completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
