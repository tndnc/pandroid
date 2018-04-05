import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GoogleSheetsIntegrationTest {
    private static Sheets sheetsService;
    private static String SPREADSHEET_ID = "1zLIlFkoa4GM70x5zydp6L0Rgjk8vzqXAM0FQ936YRhY";
    
 
    @BeforeClass
    public static void setup() throws GeneralSecurityException, IOException {
        sheetsService = SheetsServiceUtil.getSheetsService();
    }
    
    @Test
    public void whenWriteSheet_thenReadSheetOk() throws Exception {
        ValueRange body = new ValueRange()
          .setValues(Arrays.asList(
            Arrays.asList("SUKKAAAAA January"), 
            Arrays.asList("books", "40"), 
            Arrays.asList("pens", "10"),
            Arrays.asList("Expenses February"), 
            Arrays.asList("clothes", "20"),
            Arrays.asList("shoes", "5")));
        AppendValuesResponse result = sheetsService.spreadsheets().values()
          .append(SPREADSHEET_ID, "A1", body)
          .setValueInputOption("RAW")
          .execute();
    }
}
