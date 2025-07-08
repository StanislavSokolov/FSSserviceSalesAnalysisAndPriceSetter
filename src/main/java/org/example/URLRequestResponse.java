package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class URLRequestResponse {

    public static String getDateCurrent() {
        Date date = new Date();
        String str = date.toString();
        String[] subStr;
        String delimeter = " "; // Разделитель
        subStr = str.split(delimeter); // Разделения строки str с помощью метода split()
        String day = subStr[2];
        String month = subStr[1];
        String year = subStr[5];
        String month1 = month;
        if (month.equals("Jan")) month1 = "01";
        else if (month.equals("Feb")) month1 = "02";
        else if (month.equals("Mar")) month1 = "03";
        else if (month.equals("Apr")) month1 = "04";
        else if (month.equals("May")) month1 = "05";
        else if (month.equals("Jun")) month1 = "06";
        else if (month.equals("Jul")) month1 = "07";
        else if (month.equals("Aug")) month1 = "08";
        else if (month.equals("Sep")) month1 = "09";
        else if (month.equals("Oct")) month1 = "10";
        else if (month.equals("Nov")) month1 = "11";
        else month1 = "12";
        return year + "-" + month1 + "-" + day;
    }

    public static String getDate(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, i);
        String str = calendar.getTime().toString();
        String[] subStr;
        String delimeter = " "; // Разделитель
        subStr = str.split(delimeter); // Разделения строки str с помощью метода split()
        String day = subStr[2];
        String month = subStr[1];
        String year = subStr[5];
        String month1 = month;
        if (month.equals("Jan")) month1 = "01";
        else if (month.equals("Feb")) month1 = "02";
        else if (month.equals("Mar")) month1 = "03";
        else if (month.equals("Apr")) month1 = "04";
        else if (month.equals("May")) month1 = "05";
        else if (month.equals("Jun")) month1 = "06";
        else if (month.equals("Jul")) month1 = "07";
        else if (month.equals("Aug")) month1 = "08";
        else if (month.equals("Sep")) month1 = "09";
        else if (month.equals("Oct")) month1 = "10";
        else if (month.equals("Nov")) month1 = "11";
        else month1 = "12";
        return year + "-" + month1 + "-" + day;
    }

    public static String getResponseFromURLandBodyRequest(URL url, String token, String supplierArticle) throws IOException {

        String reqBody = "";

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization", token);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
//        reqBody = "{\"settings\":{\"cursor\"}[\"" + supplierArticle + "\"],\"allowedCategoriesOnly\":true}";
//        reqBody = "{\"settings\":{\"sort\":{\"ascending\": false}, \"filter\": {\"textSearch\": \"\", \"allowedCategoriesOnly\": true, \"tagIDs\": [ ], \"objectIDs\": [ ], \"brands\": [ ], \"imtID\": " + supplierArticle + " , \"withPhoto\": -1}, \"cursor\": {\"updatedAt\": \"\", \"nmID\": 0, \"limit\": 11} } }";
        reqBody = "{\"settings\":{\"cursor\": {\"limit\": 100},\"filter\":{\"withPhoto\": -1}}}";
        writer.write(reqBody);
        writer.close();

        System.out.println(reqBody);

        return getResponse(httpURLConnection);
    }

//    public static String getResponseFromURLandBodyRequest(URL url, String token, String parametr0, String parametr1, String parametr2) throws IOException {
//
//        String reqBody = "";
//
//        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//        httpURLConnection.setRequestProperty("accept", "application/json");
//        httpURLConnection.setRequestProperty("Authorization", token);
//        httpURLConnection.setDoOutput(true);
//        OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
//        reqBody = "{\"vendorCodes\":[\"" + supplierArticle + "\"],\"allowedCategoriesOnly\":true}";
//        writer.write(reqBody);
//        writer.close();
//
//        System.out.println(reqBody);
//
//        return getResponse(httpURLConnection);
//    }

    public static String getResponseFromURL(URL url, String token) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization", token);
        httpURLConnection.setDoOutput(true);

        return getResponse(httpURLConnection);
    }

    private static String getResponse(HttpURLConnection httpURLConnection) throws IOException {
        try {
            InputStream in = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return  null;
            }
        } finally {
            httpURLConnection.disconnect();
        }
    }


    public static String getResponseFromURL(URL url, String token, String client, String method, String product_id, String price) throws IOException, URISyntaxException {

        String methodNumber = method;
        String reqBody = "";

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Client-Id", client);
        httpURLConnection.setRequestProperty("Api-Key", token);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
        if (methodNumber.equals("list")) reqBody = "{\"filter\":{\"visibility\": \"ALL\"},\"last_id\": \"\", \"limit\": 100}";
        if (methodNumber.equals("info")) reqBody = "{\"offer_id\": \"\",\"product_id\": " + product_id + ", \"sku\": 0}";
        if (methodNumber.equals("fbo/list")) reqBody = "{\"dir\": \"ASC\", \"filter\": {\"since\": \"" + getDateCurrent() + "T00:00:00.000Z\"}, \"limit\": 5, \"offset\": 0, \"translit\": true, \"with\": {\"analytics_data\": true, \"financial_data\": true}}";
        if (methodNumber.equals("import/prices")) reqBody = "{\"prices\": [{\"auto_action_enabled\": \"UNKNOWN\",\"min_price\": \"100\", \"offer_id\": \"\", \"old_price\": \"0\", \"price\": \"" + price + "\", \"product_id\": " + product_id + "}]}";
        writer.write(reqBody);
        writer.close();

        System.out.println(reqBody);

        return getResponse(httpURLConnection);
    }
}