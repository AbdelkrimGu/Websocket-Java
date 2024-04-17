package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Controller extends Component {
    // variables
    static ArrayList<String> sFiles = new ArrayList<>();
    @FXML
    AnchorPane rootPane;

    @FXML
    Button desactiver;
    @FXML
    Button activer;




    static ServerSocket serverSocket;
    static String result = "";


    //


    public void directory(){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Source Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                    +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    +  chooser.getSelectedFile());
            result = String.valueOf(chooser.getSelectedFile());
        }
        else {
            System.out.println("No Selection ");
        }
        if(result != null){
            // récuperer les fichiers du dossier séléctionné

            File testDirectory = new File(result);
            File[] files = testDirectory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isFile();
                }
            });
            for (int i=0;i < files.length ; i++){
                sFiles.add(files[i].getName());
            }
            AnchorPane pane = null;
            try {
                pane = FXMLLoader.load(getClass().getResource("activation.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            rootPane.getChildren().setAll(pane);

        }


    }

    public void connexion() {
        System.out.println("8");
        activer.setVisible(false);
        System.out.println("9");
        desactiver.setVisible(true);
        System.out.println("10");
        try{

            serverSocket = new ServerSocket(1234);


            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
                    Object object = objectInput.readObject();
                    String choice = (String) object;
                    if (choice.equals("1")){
                        // envoie de la liste des fichiers disponible dans le répertoire
                        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(sFiles);
                    }
                    else {
                        // recevoir le nom de fichier voulu
                        //objectInput = new ObjectInputStream(socket.getInputStream());
                        object = objectInput.readObject();
                        String target = (String) object;

                        // envoie du fichier voulu
                        System.out.println(result+"\\"+target);

                        File file = new File(result+"\\"+target);
                        // Get the size of the file
                        long length = file.length();
                        byte[] bytes = new byte[16 * 1024];
                        InputStream in = new FileInputStream(file);
                        OutputStream out = socket.getOutputStream();

                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                        objectInput.close();
                        out.flush();
                        out.close();
                    }
                    socket.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                    //System.out.println(result+"/5.png");

                }


            }




        }
        catch (Exception e){
            Label erreur = new Label("erreur d'activation");
            erreur.setLayoutX(15);
            erreur.setLayoutY(15);
            erreur.setPrefSize(200,25);
            rootPane.getChildren().add(2,erreur);

        }

    }
    public void arreter() throws IOException {
        serverSocket.close();
        AnchorPane pane = null;
        try {
            pane = FXMLLoader.load(getClass().getResource("sample.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.getChildren().setAll(pane);

    }
}
