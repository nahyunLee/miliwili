package com.app.miliwili.utils;

import com.app.miliwili.config.BaseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.app.miliwili.config.BaseResponseStatus.*;


@Service
public class GoogleService {


    public String userIdFromGoogle(String token) throws BaseException{

        String userId="";
        BufferedReader in  = null;
        InputStream is = null;
        InputStreamReader isr = null;
       JsonParser jsonParser = new JsonParser();
        JsonParser parser = new JsonParser();

        try{
            String reqURL = "https://oauth2.googleapis.com/tokeninfo?id_token="+token;
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try {
                is = conn.getInputStream();
                isr = new InputStreamReader(is, "UTF-8");
                in = new BufferedReader(isr);
            }catch (Exception e){
                throw new BaseException(INVALID_TOKEN);
            }

             int responseCode = conn.getResponseCode();
             JsonObject jsonObj = (JsonObject)jsonParser.parse(in);

             userId = (jsonObj.get("sub").toString());
            if(responseCode!=200){
                throw new BaseException(INVALID_TOKEN);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return userId;
    }

}