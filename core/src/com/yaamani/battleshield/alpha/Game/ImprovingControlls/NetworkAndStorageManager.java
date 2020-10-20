package com.yaamani.battleshield.alpha.Game.ImprovingControlls;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.MainMenuScreen;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Timer;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * | Left controller stick angle : float(4bytes) | Right controller stick angle : float(4bytes) | Active shields num : byte | Bullets per attack : byte |
 *
 * if angle >= 1000 = null
 */
public class NetworkAndStorageManager implements Disposable, Resizable {

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

    //public static final int NUM_OF_ENTRIES_TO_SAVE_EACH_SAVE_CYCLE = (int) (0.25f/*1*//*min*/ * 60 /*sec*/ * 60/*fps*/);

    private boolean saveControllerValuesModeEnabled;
    private int numOfSavedEntries;
    private Timer writeToStorageTimer;
    private boolean currentlyWritingToStorage;

    private FileHandle fileHandle;
    //private FileWriter fileWriter;

    private boolean loadControllerValuesModeEnabled;


    // ------------------------ Data stuff ------------------------

    private Runnable valuesPreparationRunnable;

    private boolean leftStickAngleReadyToBeConsumed;
    private boolean rightStickAngleReadyToBeConsumed;
    private boolean activeShieldsNumReadyToBeConsumed;
    private boolean bulletsPerAttackReadyToBeConsumed;

    private Float currentLeftStickAngle;
    private Float currentRightStickAngle;
    private byte currentActiveShieldsNum;
    private byte currentBulletsPerAttack;

    private boolean leftStickAnglePrepared;
    private boolean rightStickAnglePrepared;
    private boolean activeShieldsNumPrepared;
    private boolean bulletsPerAttackPrepared;

    private Array<Float> allLeftStickAngles;
    private Array<Float> allRightStickAngles;
    private Array<Byte> allActiveShieldsNums;
    private Array<Byte> allBulletsPerAttacks;

    private Array<Float> allLeftStickVelocities;
    private Array<Float> allRightStickVelocities;



    private DataMonitoring dataMonitoring;


    public NetworkAndStorageManager(MainMenuScreen mainMenuScreen, GameplayScreen gameplayScreen, MyBitmapFont myBitmapFont) {

        this.mainMenuScreen = mainMenuScreen;
        this.gameplayScreen = gameplayScreen;

        serverSocketHints = new ServerSocketHints();
        socketHints = new SocketHints();

        initializeServerConnectionRunnable();
        initializeClientConnectionRunnable();
        initializeValuesPreparationRunnable();
        initializeReceivingRunnable();


        writeToStorageTimer = new Timer(15/*sec*/*1000/*ms*/) {
            @Override
            public void onFinish() {
                super.onFinish();
                saveTheMostRecentEntries();
                writeToStorageTimer.start();
            }
        };


        dataMonitoring = new DataMonitoring(mainMenuScreen.getStage().getViewport(), this, myBitmapFont);
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
        /*if (saveControllerValuesModeEnabled)
            new WriteEntriesRunnable(numOfSavedEntries, allLeftStickAngles.size-numOfSavedEntries).run();*/
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        dataMonitoring.resize(width, height, worldWidth, worldHeight);
    }

    public void render() {

        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
            writeToStorageTimer.update(Gdx.graphics.getDeltaTime());

        if (loadControllerValuesModeEnabled)
            dataMonitoring.render();
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

    public void prepareBulletsPerAttackForTransmissionAndStorageIfIamMobile(byte bulletsPerAttack) {
        //if (!connectionEstablished) return;
        if (Gdx.app.getType() != Application.ApplicationType.Android & Gdx.app.getType() != Application.ApplicationType.iOS) return;

        this.currentBulletsPerAttack = bulletsPerAttack;
        bulletsPerAttackPrepared = true;
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
                currentActiveShieldsNum,
                currentBulletsPerAttack
        };

        try {
            out.write(all);
        } catch (IOException e) {
            Gdx.app.error(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void newEntry() {

        if (allLeftStickAngles == null) {
            int initialCapacity = 30/*min*/ * 60/*sec*/ * 60/*fps*/;

            allLeftStickAngles = new Array<>(true, initialCapacity, Float.class);
            allRightStickAngles = new Array<>(true, initialCapacity, Float.class);
            allActiveShieldsNums = new Array<>(true, initialCapacity, Byte.class);
            allBulletsPerAttacks = new Array<>(true, initialCapacity, Byte.class);

            allLeftStickVelocities = new Array<>(true, initialCapacity, Float.class);
            allRightStickVelocities = new Array<>(true, initialCapacity, Float.class);
        }

        allLeftStickAngles.add(currentLeftStickAngle);
        allRightStickAngles.add(currentRightStickAngle);
        allActiveShieldsNums.add(currentActiveShieldsNum);
        allBulletsPerAttacks.add(currentBulletsPerAttack);

        /*if (saveControllerValuesModeEnabled & (allLeftStickAngles.size - numOfSavedEntries >= NUM_OF_ENTRIES_TO_SAVE_EACH_SAVE_CYCLE) & !isCurrentlyWritingToStorage())
            saveTheMostRecentEntries(numOfSavedEntries, NUM_OF_ENTRIES_TO_SAVE_EACH_SAVE_CYCLE);*/




        if (allLeftStickAngles.size == 1) {
            allLeftStickVelocities.add(0f);
        }
        else {
            Float previousLeftStickAngle = allLeftStickAngles.get(allLeftStickAngles.size-2);
            if (previousLeftStickAngle == null | currentLeftStickAngle == null)
                allLeftStickVelocities.add(0f);
            else {

                float previous = MyMath.deg_0_to_360(previousLeftStickAngle*MathUtils.radDeg) * MathUtils.degRad;
                float current = MyMath.deg_0_to_360(currentLeftStickAngle*MathUtils.radDeg) * MathUtils.degRad;

                allLeftStickVelocities.add((current - previous) / (1f / 60f)); // Rad/sec
            }
        }

        if (allRightStickAngles.size == 1) {
            allRightStickVelocities.add(0f);
        }
        else {
            Float previousRightStickAngle = allRightStickAngles.get(allRightStickAngles.size-2);
            if (previousRightStickAngle == null | currentRightStickAngle == null)
                allRightStickVelocities.add(0f);
            else
                allRightStickVelocities.add((currentRightStickAngle-previousRightStickAngle)/(1f/60f)); // Rad/sec
        }

    }

    /*public void saveTheMostRecentEntries(int start, int len) {
        new Thread(new WriteEntriesRunnable(start, len)).start();
    }*/

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

    public boolean isLoadControllerValuesModeEnabled() {
        return loadControllerValuesModeEnabled;
    }

    public void enableLoadControllerValuesMode() {
        this.loadControllerValuesModeEnabled = true;


        try {

            Gdx.app.log(TAG, "Please enter the absolute path : ");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            String filePath = reader.readLine();
            FileHandle fh = Gdx.files.absolute(filePath);

            Gdx.app.log(TAG, "Loading...");


            Scanner fileScanner = new Scanner(fh.file());
            while (fileScanner.hasNextLine()) {

                String line = fileScanner.nextLine();
                String[] individualValues = line.split(", ");

                //Gdx.app.log(TAG, Arrays.toString(individualValues));

                currentLeftStickAngle = Float.parseFloat(individualValues[0]);
                if (currentLeftStickAngle >= 1000f) currentLeftStickAngle = null;
                //Gdx.app.log(TAG, ""+currentLeftStickAngle);

                currentRightStickAngle = Float.parseFloat(individualValues[1]);
                if (currentRightStickAngle >= 1000f) currentRightStickAngle = null;

                currentActiveShieldsNum = Byte.parseByte(individualValues[2]);

                currentBulletsPerAttack = Byte.parseByte(individualValues[3]);


                newEntry();
            }

            fileScanner.close();

            /*Gdx.app.log(TAG, "allLeftStickAngles = " + allLeftStickAngles.toString());
            Gdx.app.log(TAG, "allRightStickAngles = " + allRightStickAngles.toString());
            Gdx.app.log(TAG, "allActiveShieldsNums = " + allActiveShieldsNums.toString());
            Gdx.app.log(TAG, "allBulletsPerAttacks = " + allBulletsPerAttacks.toString());*/

            if (allLeftStickVelocities.size < DataMonitoring.PLOTTED_POINTS_PER_FRAME)
                Gdx.app.error(TAG, "Too short :(");
            else
                Gdx.app.log(TAG, "Done.");

            Viewport vp = mainMenuScreen.getStage().getViewport();
            dataMonitoring.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), vp.getWorldWidth(), vp.getWorldHeight());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setCurrentLeftStickAngle(Float currentLeftStickAngle) {
        this.currentLeftStickAngle = currentLeftStickAngle;
        leftStickAngleReadyToBeConsumed = true;
    }

    void setCurrentRightStickAngle(Float currentRightStickAngle) {
        this.currentRightStickAngle = currentRightStickAngle;
        rightStickAngleReadyToBeConsumed = true;
    }

    void setCurrentActiveShieldsNum(byte currentActiveShieldsNum) {
        this.currentActiveShieldsNum = currentActiveShieldsNum;
        activeShieldsNumReadyToBeConsumed = true;
    }

    void setCurrentBulletsPerAttack(byte currentBulletsPerAttack) {
        this.currentBulletsPerAttack = currentBulletsPerAttack;
        bulletsPerAttackReadyToBeConsumed = true;
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

    public byte consumeBulletsPerAttack() {
        bulletsPerAttackReadyToBeConsumed = false;
        return currentBulletsPerAttack;
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

    public boolean isBulletsPerAttackReadyToBeConsumed() {
        return bulletsPerAttackReadyToBeConsumed;
    }

    public Array<Float> getAllLeftStickAngles() {
        return allLeftStickAngles;
    }

    public Array<Float> getAllRightStickAngles() {
        return allRightStickAngles;
    }

    public Array<Byte> getAllActiveShieldsNums() {
        return allActiveShieldsNums;
    }

    public Array<Byte> getAllBulletsPerAttacks() {
        return allBulletsPerAttacks;
    }

    public Array<Float> getAllLeftStickVelocities() {
        return allLeftStickVelocities;
    }

    public Array<Float> getAllRightStickVelocities() {
        return allRightStickVelocities;
    }

    public Timer getWriteToStorageTimer() {
        return writeToStorageTimer;
    }

    public boolean isCurrentlyWritingToStorage() {
        return currentlyWritingToStorage;
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
                    if (leftStickAnglePrepared & rightStickAnglePrepared & activeShieldsNumPrepared & bulletsPerAttackPrepared) {

                        newEntry();

                        if (connectionEstablished)
                            transmit();

                        leftStickAnglePrepared = false;
                        rightStickAnglePrepared = false;
                        activeShieldsNumPrepared = false;
                        bulletsPerAttackPrepared = false;
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
                        byte bulletsPerAttackBytes = (byte) in.read();


                        Float receivedLeftStickAngle = ByteBuffer.wrap(leftStickAngleBytes).getFloat();
                        if (receivedLeftStickAngle >= 1000f) receivedLeftStickAngle = null;
                        setCurrentLeftStickAngle(receivedLeftStickAngle);

                        Float receivedRightStickAngle = ByteBuffer.wrap(rightStickAngleBytes).getFloat();
                        if (receivedRightStickAngle >= 1000f) receivedRightStickAngle = null;
                        setCurrentRightStickAngle(receivedRightStickAngle);

                        setCurrentActiveShieldsNum(activeShieldsNumberBytes);

                        setCurrentBulletsPerAttack(bulletsPerAttackBytes);



                        newEntry();

                    } catch (IOException e) {
                        e.printStackTrace();
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

            Gdx.app.log(TAG, "Saving (start = " + start + ", len = " + len + ").");

            this.start = start;
            this.len = len;


        }

        @Override
        public void run() {

            currentlyWritingToStorage = true;
            Gdx.app.log(TAG, "Writing to storage ....");

            FileWriter fileWriter;

            try {

                fileWriter = new FileWriter(fileHandle.file(), true);


                for (int i = start; i < start + len; i++) {
                    float leftStick = allLeftStickAngles.get(i);
                    float rightStick = allRightStickAngles.get(i);
                    byte activeShieldsNum = allActiveShieldsNums.get(i);
                    byte bulletsPerAttack = allBulletsPerAttacks.get(i);


                    try {
                        //Gdx.app.log(TAG, "Writing the remaining values.");
                        fileWriter.write(leftStick + ", " + rightStick + ", " + activeShieldsNum + ", " + bulletsPerAttack + '\n');
                    } catch (IOException e) {
                        Gdx.app.error(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

                numOfSavedEntries += /*start+*/len;

                fileWriter.close();



            } catch (IOException e) {
                Gdx.app.error(TAG, e.getMessage());
                e.printStackTrace();
            }

            currentlyWritingToStorage = false;
            Gdx.app.log(TAG, "Writing to storage is now done.");

        }
    }
}
