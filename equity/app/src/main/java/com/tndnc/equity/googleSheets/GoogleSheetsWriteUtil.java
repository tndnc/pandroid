package com.tndnc.equity.googleSheets;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.tndnc.equity.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GoogleSheetsWriteUtil {
    private static Sheets sheetsService;
    private static String SPREADSHEET_ID = "1zLIlFkoa4GM70x5zydp6L0Rgjk8vzqXAM0FQ936YRhY";

    public static void setup(Context ctx) throws GeneralSecurityException, IOException {
        InputStream in = ctx.getResources().openRawResource(R.raw.credentials);
        Credential credential = GoogleCredential.fromStream(in).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        sheetsService = SheetsServiceUtil.getSheetsService(credential);
    }
    
    public void writeUserInfo(String id_user, int age, String formation) throws Exception {
        new WriteUserInfoAsyncTask().execute(id_user, String.valueOf(age), formation);
    }
    
    public void writeUserEvaluation(String id_user, int time, int nbClick, int evaluation, String levelName) throws Exception {
        new WriteUserEvaluationAsyncTask().execute(id_user, String.valueOf(time), String.valueOf(nbClick), String.valueOf(evaluation), levelName);
    }

    private static class WriteUserInfoAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... data) {

            Date date = new Date();

            ValueRange body = new ValueRange()
                    .setValues(Arrays.<List<Object>>asList(
                            new List[]{Arrays.asList(data, date.toString())}
                    ));
            try {
                sheetsService.spreadsheets().values()
                        .append(SPREADSHEET_ID, "User_Info!A1", body)
                        .setValueInputOption("RAW")
                        .execute();
            } catch (IOException e) {
                Log.w("GoogleSheet", "Error during user info write");
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class WriteUserEvaluationAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... data) {

            ValueRange body = new ValueRange()
                    .setValues(Arrays.<List<Object>>asList(
                            new List[]{Arrays.asList(data)}
                    ));
            try {
                sheetsService.spreadsheets().values()
                        .append(SPREADSHEET_ID, "User_Evaluation!A1", body)
                        .setValueInputOption("RAW")
                        .execute();
            } catch (IOException e) {
                Log.w("GoogleSheet", "Error during user eval write");
                e.printStackTrace();
            }
            return null;
        }
    }
}
