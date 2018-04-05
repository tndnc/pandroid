import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainTest {
	public static void main(String [] args){
		GoogleSheetsWriteUtil g = new GoogleSheetsWriteUtil();
		try {
			GoogleSheetsWriteUtil.setup();
		} catch (GeneralSecurityException | IOException e1) {
		}
		try {
			g.writeUserEvaluation("34", 100, 666, 9);
		} catch (Exception e) {
		}
	}

}
