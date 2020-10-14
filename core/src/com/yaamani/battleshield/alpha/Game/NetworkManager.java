package com.yaamani.battleshield.alpha.Game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.MainMenuScreen;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * | Left controller stick angle : float(4bytes) | Right controller stick angle : float(4bytes) | Active shields num : byte |
 *
 * if angle >= 1000 = null
 */
public class NetworkManager implements Disposable {

    public static final String TAG = NetworkManager.class.getSimpleName();

    public static final int PORT_NUMBER = 6969;
    public static final String HOST_IP = "192.168.1.198";

    private MainMenuScreen mainMenuScreen;
    private GameplayScreen gameplayScreen;

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

    private boolean leftStickAngleReadyToBeConsumed;
    private boolean rightStickAngleReadyToBeConsumed;
    private boolean activeShieldsNumReadyToBeConsumed;

    private Float leftStickAngle;
    private Float rightStickAngle;
    private byte activeShieldsNum;

    private boolean leftStickAnglePrepared;
    private boolean rightStickAnglePrepared;
    private boolean activeShieldsNumPrepared;

    private Runnable transmissionRunnable;
    private Runnable receivingRunnable;

    public NetworkManager(MainMenuScreen mainMenuScreen, GameplayScreen gameplayScreen) {

        this.mainMenuScreen = mainMenuScreen;
        this.gameplayScreen = gameplayScreen;

        serverSocketHints = new ServerSocketHints();
        socketHints = new SocketHints();

        initializeServerConnectionRunnable();
        initializeClientConnectionRunnable();
        initializeTransmissionRunnable();
        initializeReceivingRunnable();
    }

    @Override
    public void dispose() {
        if (serverSocket != null)
            serverSocket.dispose();
        if (desktopSocket != null)
            desktopSocket.dispose();
        if (mobileSocket != null)
            mobileSocket.dispose();
    }

    private void createAndStartTransmissionThread() {
        new Thread(transmissionRunnable).start();
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

    public void prepareStickAngleForTransmissionIfIamMobile(Float stickAngle, Constants.Direction direction) {
        //if (!connectionEstablished) return;
        if (Gdx.app.getType() != Application.ApplicationType.Android & Gdx.app.getType() != Application.ApplicationType.iOS) return;

        if (direction == Constants.Direction.LEFT) {

            if (stickAngle != null)
                leftStickAngle = stickAngle;
            else
                leftStickAngle = 1010f; // Anything greater than 1000

            leftStickAnglePrepared = true;

        } else { // RIGHT

            if (stickAngle != null)
                rightStickAngle = stickAngle;
            else
                rightStickAngle = 1010f; // Anything greater than 1000

            rightStickAnglePrepared = true;

        }
    }

    public void prepareActiveShieldsNumForTransmissionIfIamMobile(byte activeShieldsNum) {
        //if (!connectionEstablished) return;
        if (Gdx.app.getType() != Application.ApplicationType.Android & Gdx.app.getType() != Application.ApplicationType.iOS) return;

        this.activeShieldsNum = activeShieldsNum;
        activeShieldsNumPrepared = true;
    }

    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    public Float consumeLeftStickAngle() {
         leftStickAngleReadyToBeConsumed = false;
         return leftStickAngle;
    }

    public Float consumeRightStickAngle() {
        rightStickAngleReadyToBeConsumed = false;
        return rightStickAngle;
    }

    public byte consumeActiveShieldsNum() {
        activeShieldsNumReadyToBeConsumed = false;
        return activeShieldsNum;
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

                    createAndStartTransmissionThread();
                }
            }
        };
    }

    private void initializeTransmissionRunnable() {
        transmissionRunnable = new Runnable() {
            @Override
            public void run() {
                while (connectionEstablished) {
                    if (leftStickAnglePrepared & rightStickAnglePrepared & activeShieldsNumPrepared) {

                        byte[] leftStickAngleBytes = ByteBuffer.allocate(4).putFloat(leftStickAngle).array();
                        byte[] rightStickAngleBytes = ByteBuffer.allocate(4).putFloat(rightStickAngle).array();

                        byte[] all = {
                                leftStickAngleBytes[0],
                                leftStickAngleBytes[1],
                                leftStickAngleBytes[2],
                                leftStickAngleBytes[3],
                                rightStickAngleBytes[0],
                                rightStickAngleBytes[1],
                                rightStickAngleBytes[2],
                                rightStickAngleBytes[3],
                                activeShieldsNum
                        };

                        try {
                            out.write(all);
                        } catch (IOException e) {
                            //e.printStackTrace();
                            Gdx.app.error(TAG, e.getMessage());
                        }

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


                        leftStickAngle = ByteBuffer.wrap(leftStickAngleBytes).getFloat();
                        if (leftStickAngle >= 1000f) leftStickAngle = null;

                        rightStickAngle = ByteBuffer.wrap(rightStickAngleBytes).getFloat();
                        if (rightStickAngle >= 1000f) rightStickAngle = null;

                        activeShieldsNum = activeShieldsNumberBytes;


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
}
