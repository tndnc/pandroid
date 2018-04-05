package com.tndnc.equity.googleSheets;

import android.net.Uri;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.SheetsScopes;

public class GoogleAuthorizeUtil {
    public static Credential authorize() throws IOException, GeneralSecurityException {
        //TODO alex je pige pas ca marche pas ici 
        Uri path = Uri.parse("android.resource://res/raw/googleSheetsData.json");
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(path.toString()))
        	    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        return credential;
    }

}