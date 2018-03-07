package com.codecool.greencommitment.client;

import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Client {
    private int port;
    private String host;

    private String clientId;
    private Socket socket;
    private List<Sensor> sensors;
    private int dataSendInterval = 5;

    private volatile boolean isTransferring;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private BufferedReader inReader;
    private PrintWriter outWriter;

    // Constructor(s)
    public Client(int port, String host) throws IOException {
        socket = new Socket(host, port);
        sensors = new ArrayList<>();
        sensors.add(new TemperatureSensor());
        sensors.add(new AirPressureSensor());
        sensors.add(new WindSpeedSensor());
        this.port = port;
        this.host = host;
    }

    public String start() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        inReader = new BufferedReader(new InputStreamReader(inputStream));
        outWriter = new PrintWriter(outputStream, true);

        if (handleLogin()) {
            return "Logged in to server!";
        } else {
            return"Login unsuccessful!";
        }
    }

    // Method(s)

    // Getters and setters
    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setDataSendInterval(int dataSendInterval) {
        this.dataSendInterval = dataSendInterval;
    }

    public boolean isTransferring() {
        return isTransferring;
    }

    public void setIsTransferring(boolean transferring) {
        isTransferring = transferring;
    }

    //Login handling starts here
    private boolean handleLogin() throws IOException {
        String pathToId = System.getProperty("user.home") + "/clientid";
        File idFile = new File(pathToId);
        String ok;
        if (idFile.exists()) {
            readId(pathToId);
            ok = sendId();
        } else if (!(idFile.exists())) {
            writeId(pathToId, getId());
            ok = "ok";
        } else {
            ok = "no";
        }
        return ok.equals("ok");
    }

    private void readId(String pathToId) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathToId));
        this.clientId = br.readLine();
        br.close();
    }

    private void writeId(String pathToId, String id) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(pathToId));
        bw.write(id);
        bw.flush();
        bw.close();
    }

    private String sendId() throws IOException {
        outWriter.println(clientId);
        return inReader.readLine();
    }

    private String getId() throws IOException {
        String clientId = "0";
        outWriter.println(clientId);
        clientId = inReader.readLine();
        return clientId;
    }
    // Login handling closes here

    protected String logOut(){
        String logOut = "logout";
        outWriter.println(logOut);
        return "Logged out!";
    }

    //Data transfer starts here
    public Thread dataTransfer = new Thread(){
        public void run() {
            while (true) {
                if (isTransferring) {
                    try {
                        sendData();
                        TimeUnit.SECONDS.sleep(dataSendInterval);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    } catch (IOException | NullPointerException ioe) {
                        System.out.println("Couldn't send data to server! Something wrong on that side! Exiting!");
                        System.exit(1);
                    } catch (ConcurrentModificationException cme) {
                        System.out.println("Try again please!");
                    }
                }
            }
        }
    };

    private String sendData() throws IOException, ConcurrentModificationException, NullPointerException {
        for (Sensor s:sensors){
            if (s.isStarted()){
                Document doc = s.readData();
                outWriter.println("measurement");
                if (inReader.readLine().equals("ok")) {
                    outputStream.writeObject(doc);
                    if (inReader.readLine().equals("error")){
                        return "Server data handling error. Please restart the client!";
                    }
                }
            }
        }
        return "ok";
    }

    public String getChartFromServer(String type) throws IOException, ClassNotFoundException {
        BufferedImage picture;
        String transferOk, fileName;
        int sensorId = 0;

        switch (type){
            case "temp":
                sensorId = 453;
                fileName = "tempsensorchart";
                break;
            case "air":
                sensorId = 1587;
                fileName = "airsensorchart";
                break;
            case "wind":
                sensorId = 1785;
                fileName = "windsensorchart";
                break;
            default:
                return "No such sensor!";
        }

        outWriter.println("request " + sensorId);
        if (inReader.readLine().equals("ok")) {
            picture = (BufferedImage) inputStream.readObject();
            if (inReader.readLine().equals("ok")){
                 return saveImageToDisk(picture, fileName);
            } else {
                return "Error receiving picture! Please try again a bit later!";
            }
        } else {
            return "Error receiving picture! Please try again a bit later!";
        }
    }

    private String saveImageToDisk(BufferedImage image, String fileName) throws IOException {
        File imageFile = new File(System.getProperty("user.home") + "/" + fileName);
        ImageIO.write(image,"jpg", imageFile);
        return imageFile.getAbsolutePath();
    }
}

