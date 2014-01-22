package org.genshin.emojidexandroid;

import android.content.Context;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nazuki on 2014/01/21.
 */
public class FavoritesData
{
    public static final int TRUE = 0;
    public static final int FALSE = 1;
    public static final int FAILED = 2;

    public static ArrayList<List<Integer>> load(Context context)
    {
        String str = "";
        StringBuilder builder = new StringBuilder();
        ArrayList<List<Integer>> data = new ArrayList<List<Integer>>();

        // read json data
        try
        {
            InputStream in = context.openFileInput("favorites.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((str = reader.readLine()) != null)
                builder.append(str);
            str = new String(builder);
            reader.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return data;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return  data;
        }

        if (str.equals(""))
            return  data;

        // convert array data
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try
        {
            JsonParser parser = factory.createJsonParser(str);
            rootNode = mapper.readValue(parser, JsonNode.class);
        }
        catch (IOException e) { e.printStackTrace();}
        for (int i = 0; i < rootNode.size(); i++)
        {
            List<Integer> list = new ArrayList<Integer>();
            for (int j = 0; j < rootNode.get(i).size(); j++)
                list.add(rootNode.get(i).get(j).intValue());
            data.add(list);
        }

        return data;
    }

    public static int save(Context context, int[] keyCodes)
    {
        // current list
        ArrayList<List<Integer>> data = load(context);

        // duplication check
        boolean check = false;
        for (List<Integer> list : data)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i) != keyCodes[i])
                    break;
                if (i == list.size() - 1)
                    check = true;
            }
        }
        if (check)
            return FALSE;

        // save data
        JSONArray array = new JSONArray();
        for (List<Integer> list : data)
        {
            JSONArray tmp = new JSONArray();
            for (int code : list)
                tmp.put(code);
            array.put(tmp);
        }

        JSONArray tmp = new JSONArray();
        for (int keyCode : keyCodes)
            tmp.put(keyCode);
        array.put(tmp);

        try
        {
            OutputStream out = context.openFileOutput("favorites.json", Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.append(array.toString());
            writer.close();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return FAILED;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return FAILED;
        }

        return TRUE;
    }
}
