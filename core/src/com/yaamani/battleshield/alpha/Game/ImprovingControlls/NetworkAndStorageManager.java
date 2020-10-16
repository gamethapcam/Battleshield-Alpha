package com.yaamani.battleshield.alpha.Game.ImprovingControlls;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.MainMenuScreen;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * | Left controller stick angle : float(4bytes) | Right controller stick angle : float(4bytes) | Active shields num : byte | Bullets per attack : byte |
 *
 * if angle >= 1000 = null
 */
public class NetworkAndStorageManager implements Disposable {

    public static final String TAG = NetworkAndStorageManager.class.getSimpleName();

    private MainMenuScreen mainMenuScreen;
    private GameplayScreen gameplayScreen;

    // ------------------------ Network stuff ------------------------

    public static final int PORT_NUMBER = 6969;
    public static final String HOST_IP = "192.168.1.198";

    private ServerSocketHints serverSocketHints;
    private ServerSocket serverSocket;

    private SocketHints socketHints;
    private Socket desktopSocket;
    private Socket mobileSocket;

    private Runnable serverConnectionRunnable;
    private Runnable clientConnectionRunnable;

    private boolean connectionEstablished;

    private InputStream in;
    private OutputStream out;

    private Runnable receivingRunnable;



    // ------------------------ Storage stuff ------------------------

    public static final int NUM_OF_ENTRIES_TO_SAVE_EACH_SAVE_CYCLE = 1/*3*//*min*/ * 60 /*sec*/ * 60/*fps*/;

    private boolean saveControllerValuesModeEnabled;
    private int numOfSavedEntries;

    private FileHandle fileHandle;
    //private FileWriter fileWriter;



    // ------------------------ Data stuff ------------------------

    private Runnable valuesPreparationRunnable;

    private boolean leftStickAngleReadyToBeConsumed;
    private boolean rightStickAngleReadyToBeConsumed;
    private boolean activeShieldsNumReadyToBeConsumed;

    private Float currentLeftStickAngle;
    private Float currentRightStickAngle;
    private byte currentActiveShieldsNum;

    private boolean leftStickAnglePrepared;
    private boolean rightStickAnglePrepared;
    private boolean activeShieldsNumPrepared;

    private Array<Float> allLeftStickAngles;
    private Array<Float> allRightStickAngles;
    private Array<Byte> allActiveShieldsNum;


    public NetworkAndStorageManager(MainMenuScreen mainMenuScreen, GameplayScreen gameplayScreen) {

        this.mainMenuScreen = mainMenuScreen;
        this.gameplayScreen = gameplayScreen;

        serverSocketHints = new ServerSocketHints();
        socketHints = new SocketHints();

        initializeServerConnectionRunnable();
        initializeClientConnectionRunnable();
        initializeValuesPreparationRunnable();
        initializeReceivingRunnable();

        int initialCapacity = 30/*min*/ * 60/*sec*/ * 60/*fps*/;
        allLeftStickAngles = new Array<>(true, initialCapacity, Float.class);
        allRightStickAngles = new Array<>(true, initialCapacity, Float.class);
        allActiveShieldsNum = new Array<>(true, initialCapacity, Byte.class);
    }

    @Override
    public void dispose() {
        if (serverSocket != null)
            serverSocket.dispose();
        if (desktopSocket != null)
            desktopSocket.dispose();
        if (mobileSocket != null)
            mobileSocket.dispose();
        /*if (fileWriter != null) {
            try {
                new WriteEntriesRunnable(numOfSavedEntries, allLeftStickAngles.size-numOfSavedEntries).run();
                fileWriter.close();
                Gdx.app.log(TAG, "fileWrite should be closed now.");
            } catch (IOException e) {
                Gdx.app.error(TAG, e.getMessage());
                e.printStackTrace();
            }
        }*/
        if (saveControllerValuesModeEnabled)
            new WriteEntriesRunnable(numOfSavedEntries, allLeftStickAngles.size-numOfSavedEntries).run();
    }

    private void createAndStartValuesPreperationThread() {
        new Thread(valuesPreparationRunnable).start();
    }

    private void createAndStartReceivingThread() {
        new Thread(receivingRunnable).start();
    }

    public void connect() {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop)
            new Thread(serverConnectionRunnable).start();
        else if (Gdx.app.getType() == Application.ApplicationType.Android | Gdx.app.getType() == Application.ApplicationType.iOS)
            new Thread(clientConnectionRunnable).start();
    }

    public void prepareStickAngleForTransmissionAndStorageIfIamMobile(Float stickAngle, Constants.Direction direction) {
        //if (!connectionEstablished) return;
        if (Gdx.app.getType() != Application.ApplicationType.Android & Gdx.app.getType() != Application.ApplicationType.iOS) return;

        if (direction == Constants.Direction.LEFT) {

            if (stickAngle != null)
                currentLeftStickAngle = stickAngle;
            else
                currentLeftStickAngle = 1234f; // Anything greater than 1000

            leftStickAnglePrepared = true;

        } else { // RIGHT

            if (stickAngle != null)
                currentRightStickAngle = stickAngle;
            else
                currentRightStickAngle = 1234f; // Anything greater than 1000

            rightStickAnglePrepared = true;

        }
    }

    public void prepareActiveShieldsNumForTransmissionAndStorageIfIamMobile(byte activeShieldsNum) {
        //if (!connectionEstablished) return;
        if (Gdx.app.getType() != Application.ApplicationType.Android & Gdx.app.getType() != Application.ApplicationType.iOS) return;

        this.currentActiveShieldsNum = activeShieldsNum;
        activeShieldsNumPrepared = true;
    }

    private void transmit() {
        byte[] leftStickAngleBytes = ByteBuffer.allocate(4).putFloat(currentLeftStickAngle).array();
        byte[] rightStickAngleBytes = ByteBuffer.allocate(4).putFloat(currentRightStickAngle).array();

        byte[] all = {
                leftStickAngleBytes[0],
                leftStickAngleBytes[1],
                leftStickAngleBytes[2],
                leftStickAngleBytes[3],
                rightStickAngleBytes[0],
                rightStickAngleBytes[1],
                rightStickAngleBytes[2],
                rightStickAngleBytes[3],
                currentActiveShieldsNum
        };

        try {
            out.write(all);
        } catch (IOException e) {
            Gdx.app.error(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void newEntry() {
        allLeftStickAngles.add(currentLeftStickAngle);
        allRightStickAngles.add(currentRightStickAngle);
        allActiveShieldsNum.add(currentActiveShieldsNum);

        if (saveControllerValuesModeEnabled & (allLeftStickAngles.size - numOfSavedEntries >= NUM_OF_ENTRIES_TO_SAVE_EACH_SAVE_CYCLE))
            saveTheMostRecentEntries(numOfSavedEntries, NUM_OF_ENTRIES_TO_SAVE_EACH_SAVE_CYCLE);
    }

    public void saveTheMostRecentEntries(int start, int len) {
        new Thread(new WriteEntriesRunnable(start, len)).start();
    }

    public void saveTheMostRecentEntries() {
        new Thread(new WriteEntriesRunnable(numOfSavedEntries, allLeftStickAngles.size-numOfSavedEntries)).start();
    }

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public boolean isSaveControllerValuesModeEnabled() {
        return saveControllerValuesModeEnabled;
    }

    public void setSaveControllerValuesModeEnabled(boolean saveControllerValuesModeEnabled) {
        this.saveControllerValuesModeEnabled = saveControllerValuesModeEnabled;

        //try {
            FileHandle parentDirectory = Gdx.files.external("Battleshield");
            if (!parentDirectory.exists())
                parentDirectory.mkdirs();
            fileHandle = Gdx.files.external("Battleshield/" + System.currentTimeMillis() + ".csv");
            //fileWriter = new FileWriter(fileHandle.file());

            //fileWriter.write("Left, " + " Right, " + " ShieldsNum" + '\n');
            //fileWriter.close();

            createAndStartValuesPreperationThread();

        /*} catch (IOException e) {
            Gdx.app.error(TAG, e.getMessage());
            e.printStackTrace();
        }*/
    }

    public Float consumeLeftStickAngle() {
         leftStickAngleReadyToBeConsumed = false;
         return currentLeftStickAngle;
    }

    public Float consumeRightStickAngle() {
        rightStickAngleReadyToBeConsumed = false;
        return currentRightStickAngle;
    }

    public byte consumeActiveShieldsNum() {
        activeShieldsNumReadyToBeConsumed = false;
        return currentActiveShieldsNum;
    }

    public boolean isLeftStickAngleReadyToBeConsumed() {
        return leftStickAngleReadyToBeConsumed;
    }

    public boolean isRightStickAngleReadyToBeConsumed() {
        return rightStickAngleReadyToBeConsumed;
    }

    public boolean isActiveShieldsNumReadyToBeConsumed() {
        return activeShieldsNumReadyToBeConsumed;
    }

    private void initializeServerConnectionRunnable() {
        serverConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                Gdx.app.log(TAG, "------------------------------");

                if (serverSocket != null) {
                    serverSocket.dispose();
                }

                boolean serverStarted = false;

                ServerSocketHints serverSocketHints = new ServerSocketHints();

                try {

                    serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, PORT_NUMBER, serverSocketHints);
                    serverStarted = true;

                    Gdx.app.log(TAG, "Server started !!");

                } catch (GdxRuntimeException e) {

                    Gdx.app.error(TAG, e.getMessage());
                    e.printStackTrace();

                    mainMenuScreen.connectionFailed();
                }





                if (desktopSocket != null)
                    dispose();

                SocketHints socketHints = new SocketHints();

                if (serverStarted) {
                    try {

                        Gdx.app.log(TAG, "Waiting for a client ....");

                        desktopSocket = serverSocket.accept(socketHints);

                        Gdx.app.log(TAG, "Client connected !!");

                        connectionEstablished = true;

                    } catch (GdxRuntimeException e) {

                        Gdx.app.error(TAG, e.getMessage());
                        e.printStackTrace();

                        mainMenuScreen.connectionFailed();
                    }
                }




                if (connectionEstablished) {

                    mainMenuScreen.connectionEstablished();

                    in = desktopSocket.getInputStream();

                    createAndStartReceivingThread();
                }
            }
        };
    }

    private void initializeClientConnectionRunnable() {
        clientConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                Gdx.app.log(TAG, "-------------------");

                if (mobileSocket != null)
                    mobileSocket.dispose();


                SocketHints socketHints = new SocketHints();

                try {

                    mobileSocket = Gdx.net.newClientSocket(Net.Protocol.TCP, HOST_IP, PORT_NUMBER, socketHints);

                    Gdx.app.log(TAG, "Connected !!");

                    connectionEstablished = true;

                } catch (GdxRuntimeException e) {

                    Gdx.app.error(TAG, e.getMessage());
                    e.printStackTrace();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    mainMenuScreen.connectionFailed();
                }





                if (connectionEstablished) {

                    mainMenuScreen.connectionEstablished();

                    out = mobileSocket.getOutputStream();

                    createAndStartValuesPreperationThread();
                }
            }
        };
    }

    private void initializeValuesPreparationRunnable() {
        valuesPreparationRunnable = new Runnable() {
            @Override
            public void run() {
                while (connectionEstablished | saveControllerValuesModeEnabled) {
                    if (leftStickAnglePrepared & rightStickAnglePrepared & activeShieldsNumPrepared) {

                        newEntry();

                        if (connectionEstablished)
                            transmit();

                        leftStickAnglePrepared = false;
                        rightStickAnglePrepared = false;
                        activeShieldsNumPrepared = false;
                    }
                }
            }
        };
    }

    private void initializeReceivingRunnable() {
        receivingRunnable = new Runnable() {
            @Override
            public void run() {
                while (connectionEstablished) {

                    try {

                        byte[] leftStickAngleBytes = {(byte) in.read(), (byte) in.read(), (byte) in.read(), (byte) in.read()};
                        byte[] rightStickAngleBytes = {(byte) in.read(), (byte) in.read(), (byte) in.read(), (byte) in.read()};
                        byte activeShieldsNumberBytes = (byte) in.read();


                        currentLeftStickAngle = ByteBuffer.wrap(leftStickAngleBytes).getFloat();
                        if (currentLeftStickAngle >= 1000f) currentLeftStickAngle = null;

                        currentRightStickAngle = ByteBuffer.wrap(rightStickAngleBytes).getFloat();
                        if (currentRightStickAngle >= 1000f) currentRightStickAngle = null;

                        currentActiveShieldsNum = activeShieldsNumberBytes;

                        newEntry();

                        leftStickAngleReadyToBeConsumed = true;
                        rightStickAngleReadyToBeConsumed = true;
                        activeShieldsNumReadyToBeConsumed = true;



                    } catch (IOException e) {
                        //e.printStackTrace();
                    }

                }
            }
        };
    }











    public class WriteEntriesRunnable implements Runnable {

        private int start;
        private int len;

        public WriteEntriesRunnable(int start, int len) {
            super();
            this.start = start;
            this.len = len;
        }

        @Override
        public void run() {

            FileWriter fileWriter;

            try {

                fileWriter = new FileWriter(fileHandle.file());


                for (int i = start; i < start + len; i++) {
                    float leftStick = allLeftStickAngles.get(i);
                    float rightStick = allRightStickAngles.get(i);
                    float activeShieldsNum = allActiveShieldsNum.get(i);

                    try {
                        //Gdx.app.log(TAG, "Writing the remaining values.");
                        fileWriter.write(leftStick + ", " + rightStick + ", " + activeShieldsNum + '\n');
                    } catch (IOException e) {
                        Gdx.app.error(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

                numOfSavedEntries += start+len;


                fileWriter.close();


            } catch (IOException e) {
                Gdx.app.error(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
