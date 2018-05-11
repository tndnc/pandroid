package com.tndnc.equity.googleSheets;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.tndnc.equity.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsWriteUtil {
    private static Sheets sheetsService;
    private static String SPREADSHEET_ID = DB.sid;

    private static String[] failedUserInfo;
    private static List<String[]> failedEvaluation = new ArrayList<>();
    private static SharedPreferences prefs;

    public static void setup(Context ctx) throws GeneralSecurityException, IOException {
        InputStream in = ctx.getResources().openRawResource(R.raw.credentials);
        Credential credential = GoogleCredential.fromStream(in).createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        sheetsService = SheetsServiceUtil.getSheetsService(credential);
        prefs = ctx.getSharedPreferences("user_profile", Context.MODE_PRIVATE);
    }

    private void checkFailures() {
        if (failedUserInfo != null) {
            if (prefs.getBoolean("user_profile_saved", false)) {
                new ModifyUserProfileAsyncTask().execute(failedUserInfo);
            } else {
                new WriteUserInfoAsyncTask().execute(failedUserInfo);
            }
            failedUserInfo = null;
        }
        if (failedEvaluation.size() > 0) {  
            for (String[] data : failedEvaluation) {
                new WriteUserEvaluationAsyncTask().execute(data);
            }
            failedEvaluation.clear();
        }
    }

    /**
     * Write user info, args must be in this order:
     * user_id, age, formation
     */
    public void writeUserInfo(String... data) {
        new WriteUserInfoAsyncTask().execute(data);
    }

    /**
     * Write user evaluation after resolution, args must be in this order:
     * user_id, time, nb_moves, diff_eval, level_name
     */
    public void writeUserEvaluation(String... data) {
        this.checkFailures();
        new WriteUserEvaluationAsyncTask().execute(data);
    }
    
    /**
     * Modify user Profile
     */
    public void ModifyUserProfile(String... data) {
        this.checkFailures();
        new ModifyUserProfileAsyncTask().execute(data);
    }

    private static class WriteUserInfoAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... data) {

            ValueRange body = new ValueRange()
                    .setValues(Arrays.<List<Object>>asList(
                            new List[]{Arrays.asList(data)}
                    ));
            try {
            	AppendValuesResponse result = sheetsService.spreadsheets().values()
                        .append(SPREADSHEET_ID, "User_Info_2!A1", body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
            	Object userPos = result.getUpdates().get("updatedRange");
                prefs.edit().putString("userPos", (String) userPos).apply();
            } catch (IOException e) {
                Log.w("GoogleSheet", "Error during user info write; rescheduling");
                failedUserInfo = data;
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
                        .append(SPREADSHEET_ID, "User_Evaluation_2!A1", body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
            } catch (IOException e) {
                Log.w("GoogleSheet", "Error during user eval write; rescheduling");
                failedEvaluation.add(data);
                e.printStackTrace();
            }
            return null;
        }
    }
    
    private static class ModifyUserProfileAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... data) {
            
            ValueRange body = new ValueRange()
                    .setValues(Arrays.<List<Object>>asList(
                            new List[]{Arrays.asList(data)}
                    ));
            try {
                sheetsService.spreadsheets().values()
                        .update(SPREADSHEET_ID, prefs.getString("userPos",""), body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
            } catch (IOException e) {
                Log.w("GoogleSheet", "Error during user rewrite; rescheduling");
                failedUserInfo = data;
                e.printStackTrace();
            }
            return null;
        }
    }
}
