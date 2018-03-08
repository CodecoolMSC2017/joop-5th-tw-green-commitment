package com.codecool.greencommitment.client;

import com.codecool.greencommitment.common.UdpDiscovery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    private int dataSendInterval = 4;

    private volatile boolean isTransferring;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private BufferedReader inReader;
    private PrintWriter outWriter;
    private UdpDiscovery discovery;

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

    public Client() throws IOException {
        discovery = new UdpDiscovery();
        String[] serverData = discovery.runClient();
        this.port = Integer.valueOf(serverData[1]);
        this.host = serverData[0];

        socket = new Socket(host, port);
        sensors = new ArrayList<>();
        sensors.add(new TemperatureSensor());
        sensors.add(new AirPressureSensor());
        sensors.add(new WindSpeedSensor());
    }

    public String start() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        inReader = new BufferedReader(new InputStreamReader(inputStream));
        outWriter = new PrintWriter(outputStream, true);

        if (handleLogin()) {
            return "Logged in to server!";
        } else {
            return "Login unsuccessful!";
        }
    }

    // Method(s)

    // Getters and setters
    public String getClientId() {
        return clientId;
    }

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

    public String getId() throws IOException {
        String clientId = "0";
        outWriter.println(clientId);
        clientId = inReader.readLine();
        return clientId;
    }
    // Login handling closes here

    public String logOut() {
        String logOut = "logout";
        outWriter.println(logOut);
        return "Logged out!";
    }

    //Data transfer starts here
    public Thread dataTransfer = new Thread() {
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
                    } catch (ClassNotFoundException e) {
                        System.out.println("Casting error! Something is really screwed here! Exiting!");
                        System.exit(1);
                    }
                }
            }
        }
    };

    private String sendData() throws IOException, ConcurrentModificationException, NullPointerException, ClassNotFoundException, InterruptedException {
        for (Sensor s : sensors) {
            if (s.isStarted()) {
                Document doc = s.readData();
                Element measurement = (Element) doc.getElementsByTagName("measurement").item(0);
                String id = measurement.getAttribute("id");
                outWriter.println("measurement");
                if (inReader.readLine().equals("ok")) {
                    outputStream.writeObject(doc);
                    String ok = inReader.readLine();
                    if (ok.equals("error")) {
                        return "Server data handling error. Please restart the client!";
                    } else if (ok.equals("ok")) {
                        getChartFromServer(s.name, id);
                    }
                }
            }
        }
        return "ok";
    }

    public String getChartFromServer(String name, String id) throws IOException, ClassNotFoundException {
        BufferedImage picture;
        byte[] imageInBytes;
        String fileName;

        switch (name) {
            case "Temperature sensor":
                fileName = "tempsensorchart";
                break;
            case "Air pressure sensor":
                fileName = "airsensorchart";
                break;
            case "Windspeed sensor":
                fileName = "windsensorchart";
                break;
            default:
                return "No such sensor!";
        }
        if (inReader.readLine().equals("ok")) {
            imageInBytes = (byte[]) inputStream.readObject();
            picture = ImageIO.read(new ByteArrayInputStream(imageInBytes));
            if (inReader.readLine().equals("ok")) {
                return saveImageToDisk(picture, fileName, id);
            } else {
                return "Error receiving picture! Please try again a bit later!";
            }
        } else {
            return "Could not receive picture!";
        }
    }

    private String saveImageToDisk(BufferedImage image, String fileName, String id) throws IOException {
        File imageFile = new File(System.getProperty("user.home") + "/" + id + "LineChart.jpeg");
        ImageIO.write(image, "jpg", imageFile);
        return imageFile.getAbsolutePath();
    }
}

