import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.io.IOException;

public class Ex2Client {

	public static void main(String[] args) {
		try (Socket socket = new Socket("codebank.xyz", 38102)) {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			CRC32 crc = new CRC32();
			byte upperByte;
			byte lowerByte;
			byte wholeByte;
			byte[] result = new byte[100];
			long crcValue;
			int serverResponse;

			System.out.println("Connected to Server.");
			System.out.println("Received bytes: ");

			// read 100 bytes from server
			for (int i = 0; i < 100; i++) {
				upperByte = (byte) is.read();
				lowerByte = (byte) is.read();
				upperByte = (byte) (upperByte << 4);
				wholeByte = (byte) (upperByte + lowerByte);
				result[i] = wholeByte;

				if (i != 0 && i % 10 == 0) {
					System.out.println("");
				}
				System.out.printf("%02X", result[i]);

			}

			// get a crc value
			System.out.print("\n----------------------");
			crc.update(result);
			crcValue = crc.getValue();
			System.out.printf("\nGenerated CRC32:%02X", crcValue);

			for(int i=3; i >= 0; i--) {
				os.write((int)crcValue >> (8*i));
			}
			
			// get comfirmation of result from server
			System.out.print("\n-----------------------");
			serverResponse = is.read();
			if (serverResponse == 1) {
				System.out.println("\nResponse Good.");
			} else if (serverResponse == 0){
				System.out.println("\nResponse BAD");
			}

			// disconnect from server
			System.out.println("Disconnected from server.");
			socket.close();
			
		} catch (IOException e) {
			System.out.println("Error connecting server!");
			e.printStackTrace();
		}
	}

}
