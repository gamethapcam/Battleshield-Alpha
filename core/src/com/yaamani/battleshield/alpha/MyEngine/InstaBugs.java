package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class InstaBugs {

    private static final String TAG = InstaBugs.class.getSimpleName();

    public static final InstaBugs INSTANCE = new InstaBugs();

    private InstaBugs() {
    }



    public void initialize() {

        Thread.UncaughtExceptionHandler previousHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

                throwable.printStackTrace();

                StringBuilder s = new StringBuilder();
                InstaBugs.this.buildThrowableString(s, throwable);
                InstaBugs.this.buildAllStackTracesString(s);
                //Gdx.app.error(TAG, "\n" + s.toString());
                writeToStorage(s);
                previousHandler.uncaughtException(thread, throwable);

            }
        });
    }

    private void buildThrowableString(StringBuilder s, Throwable throwable) {
        s.append(throwable.toString()).append("\n");

        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements)
            s.append("\t|").append(stackTraceElement.toString()).append("\n");

        s.append("\n");
    }

    private void buildAllStackTracesString(StringBuilder s) {
        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {

            s.append(entry.getKey().toString()).append("\n");

            if (entry.getValue().length == 0) s.append("\t|\n");

            for (StackTraceElement stackTraceElement : entry.getValue())
                s.append("\t|").append(stackTraceElement.toString()).append("\n");

            s.append("\n");
        }
    }

    private void writeToStorage(StringBuilder s) {
        FileHandle parentDirectory = Gdx.files.local("Battleshield");
        if (!parentDirectory.exists())
            parentDirectory.mkdirs();
            //Gdx.app.log(TAG, "" + parentDirectory.file().mkdirs());

        FileHandle fileHandle = Gdx.files.local("Battleshield/InstaBugs" + System.currentTimeMillis() + ".txt");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileWriter fileWriter = new FileWriter(fileHandle.file(), true);
                    fileWriter.write(s.toString());
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
