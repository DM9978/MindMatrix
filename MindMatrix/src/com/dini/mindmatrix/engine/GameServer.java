package com.dini.mindmatrix.engine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;

/**
 * Game that interfaces to external Servers to retrieve games.
 * A game consists of an image and an integer that denotes the solution of this game.
 *
 * @author Marc Conrad
 *
 */
public class GameServer {

	private int currentApiIndex = 0;

	private static final String[] API_URLS = {
			"https://marcconrad.com/uob/banana/api.php?out=csv&base64=yes",
			"https://marcconrad.com/uob/tomato/api.php?out=csv&base64=yes",
			"https://marcconrad.com/uob/smile/api.php?out=csv&base64=yes"
	};

	/**
	 * Basic utility method to read string from URL.
	 */
	private static String readUrl(String urlString)  {
		try {
			URL url = new URL(urlString);
			InputStream inputStream = url.openStream();

			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result.toString("UTF-8");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the next game from the current API in the cycle.
	 * @return a random game or null if a game cannot be found.
	 */
	public Game getRandomGame() {
		String currentApi = API_URLS[currentApiIndex];
		String dataraw = readUrl(currentApi);
		String[] data = dataraw.split(",");

		byte[] decodeImg = Base64.getDecoder().decode(data[0]);
		ByteArrayInputStream quest = new ByteArrayInputStream(decodeImg);

		int solution = Integer.parseInt(data[1]);

		BufferedImage img = null;
		try {
			img = ImageIO.read(quest);
			Game game = new Game(img, solution);

			currentApiIndex = (currentApiIndex + 1) % API_URLS.length;

			return game;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
