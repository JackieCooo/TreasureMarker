package com.jackie.treasuremarker.ui.card;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class CardViewModel extends ViewModel {
    private MutableLiveData<LinkedList<CardInfo>> info;
    private Application application;
    private final static String DATABASE_PATH = "card_database.csv";
    private final static String DATABASE_HEADER = "title,address,type,date";
    private final static String TAG = "CardViewModel";

    public CardViewModel() {
        info = new MutableLiveData<>();
        info.setValue(new LinkedList<>());
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public MutableLiveData<LinkedList<CardInfo>> getInfo() {
        return info;
    }

    public void append(CardInfo tar) {
        assert info.getValue() != null;
        info.getValue().add(tar);
    }

    public void load() {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        Context context = this.application.getApplicationContext();

        String[] fileList = context.fileList();
        Log.i(TAG, "File list length: " + fileList.length);
        for (String i : fileList) {
            Log.i(TAG, i);
        }

        if (Arrays.stream(fileList).noneMatch(s -> s.equals(DATABASE_PATH))) {
            Log.i(TAG, "Create database file");
            try {
                fos = context.openFileOutput(DATABASE_PATH, Context.MODE_APPEND);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                try {
                    bw.write(DATABASE_HEADER);
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert fos != null;
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Log.i(TAG, "Loading data...");
            fis = context.openFileInput(DATABASE_PATH);
            Scanner scanner = new Scanner(fis, "UTF-8");
            String line;

            scanner.nextLine();
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                Log.i(TAG, "Read line: " + line);
                String[] raw = line.split(",");
                CardInfo cardInfo = new CardInfo();
                cardInfo.setTitle(raw[0]);
                cardInfo.setAddress(raw[1]);
                switch (raw[2]) {
                    case "ALL":
                        cardInfo.setType(CategoryType.ALL);
                        break;
                    case "FOODS":
                        cardInfo.setType(CategoryType.FOODS);
                        break;
                    case "TRAVEL":
                        cardInfo.setType(CategoryType.TRAVEL);
                        break;
                    case "SPORTS":
                        cardInfo.setType(CategoryType.SPORTS);
                        break;
                    case "PHOTOGRAPHY":
                        cardInfo.setType(CategoryType.PHOTOGRAPHY);
                        break;
                    case "ENTERTAINMENT":
                        cardInfo.setType(CategoryType.ENTERTAINMENT);
                        break;
                    default:
                        cardInfo.setType(null);
                }
                if (AlarmDate.validate(raw[3])) {
                    cardInfo.setDate(new AlarmDate(raw[3]));
                }
                else {
                    cardInfo.setDate(null);
                }

                append(cardInfo);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            assert fis != null;
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        Log.i(TAG, "Saving data...");
        FileOutputStream fos = null;
        Context context = this.application.getApplicationContext();

        try {
            fos = context.openFileOutput(DATABASE_PATH, Context.MODE_PRIVATE);
            assert info.getValue() != null;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            try {
                bw.write(DATABASE_HEADER);
                bw.newLine();
                for (CardInfo i : info.getValue()) {
                    Log.i(TAG, "Write line: " + i);
                    bw.write(i.toString());
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            assert fos != null;
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCleared() {
        save();
        super.onCleared();
    }
}
