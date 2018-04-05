import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GoogleSheetsWriteUtil {
    private static Sheets sheetsService;
    private static String SPREADSHEET_ID = "1zLIlFkoa4GM70x5zydp6L0Rgjk8vzqXAM0FQ936YRhY";
    
 

    public static void setup() throws GeneralSecurityException, IOException {
        sheetsService = SheetsServiceUtil.getSheetsService();
    }
    
    public void writeUserInfo(String id_user, int age, String formation) throws Exception {
        ValueRange body = new ValueRange()
          .setValues(Arrays.asList(
            Arrays.asList(id_user, age, formation)));
        AppendValuesResponse result = sheetsService.spreadsheets().values()
          .append(SPREADSHEET_ID, "A1", body)
          .setValueInputOption("RAW")
          .execute();
    }
    
    public void writeUserEvaluation(String id_user, int time, int nbClick, int evaluation) throws Exception {
        ValueRange body = new ValueRange()
          .setValues(Arrays.asList(
            Arrays.asList(id_user, time, nbClick, evaluation)));
        AppendValuesResponse result = sheetsService.spreadsheets().values()
          .append(SPREADSHEET_ID, "A1", body)
          .setValueInputOption("RAW")
          .execute();
    }
}
