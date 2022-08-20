package com.jeff87218.MasterDuelDeckExport.Writer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.jeff87218.MasterDuelDeckExport.YgoObject.YgoObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardsWriter {
    static Map<Integer, YgoObject> cardMap;

    static {
        try {
            cardMap = new Gson().fromJson(new JsonReader(new FileReader("ygocards.json")), new TypeToken<HashMap<Integer, YgoObject>>(){}.getType());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void writeCSV(String fileName,List<Integer> mainDeck,List<Integer> extraDeck) throws IOException {

        FileWriter fileWriter = new FileWriter(fileName+".csv");
        fileWriter.append("\"MainDeck\",\"ExtraDeck\"\n");
        for(int i=0;i<mainDeck.size();i++){
            if(i<extraDeck.size()) {
                fileWriter.append("\"").append(cardMap.get(mainDeck.get(i)).getEnName()).append("\"");
                fileWriter.append(",");
                fileWriter.append("\"").append(cardMap.get(extraDeck.get(i)).getEnName()).append("\"");
                fileWriter.append("\n");
            }else {
                fileWriter.append("\"").append(cardMap.get(mainDeck.get(i)).getEnName()).append("\"");
                fileWriter.append("\n");
            }
        }
        fileWriter.flush();
        fileWriter.close();
        Desktop.getDesktop().edit(new File(fileName + ".csv"));
    }

    public void Clipboard(List<Integer> mainDeck,List<Integer> extraDeck) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("MainDeck:\n");
        for(int i=0;i<mainDeck.size();i++){
            sb.append("\"").append(cardMap.get(mainDeck.get(i)).getEnName()).append("\"\n");
        }
        sb.append("\nExtraDeck:\n");
        for(int i=0;i<extraDeck.size();i++){
            sb.append("\"").append(cardMap.get(extraDeck.get(i)).getEnName()).append("\"\n");

        }
        StringSelection selection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

    }


}
