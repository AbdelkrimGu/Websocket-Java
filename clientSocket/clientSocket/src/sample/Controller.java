package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Controller extends Component {
    // variables
    @FXML
    AnchorPane rootPane;

    @FXML
    TextField adresseip;

    @FXML
    Button retour;


    static ArrayList<String> filesList = null ;
    static String adresse_ip = "";
    static String result = "";
    static ScrollPane scrollPane = null;



    public void parcour() throws IOException {

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Source Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(true);
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




    }
    public void connexion()  {
        adresse_ip = adresseip.getText();
        if (result.equals("") || adresse_ip.equals("") ){
            Label erreur = new Label();
            erreur.setText("Réessayez SVP");
            erreur.setLayoutX(305);
            erreur.setLayoutY(397);
            erreur.setPrefSize(100,25);
            rootPane.getChildren().add(5,erreur);
        }
        else {
            try {
                Socket socket = new Socket(adresse_ip, 1234);
                AnchorPane pane = FXMLLoader.load(getClass().getResource("telechargement.fxml"));
                rootPane.getChildren().setAll(pane);
                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                objectOutput.writeObject("1");


                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
                Object object = objectInput.readObject();
                filesList = (ArrayList<String>) object;


                socket.close();
                objectOutput.close();
                objectInput.close();
                scrollPane = new ScrollPane();
                scrollPane.setVisible(true);
                rootPane.getChildren().add(1,scrollPane);
                scrollPane.setLayoutX(0);
                scrollPane.setLayoutY(85);
                scrollPane.setPrefHeight(315);
                //scrollPane.setPannable(true);

                int panewidth = 500;

                VBox vb = new VBox();
                vb.setLayoutX(15);
                vb.setLayoutY(14);
                vb.setPrefWidth(500);
                vb.setStyle("-fx-background-color:  white ;");
                vb.setPrefHeight(filesList.size() * panewidth + panewidth);
                vb.setSpacing(22);


                if(filesList.size() <= 3){
                    scrollPane.setPrefHeight(500);
                }
                else {
                    scrollPane.setPrefHeight(500 + (filesList.size()-3) * panewidth);
                }

                scrollPane.setContent(vb);
                Pane[] Panes = new Pane[filesList.size()];

                for (int i =0; i < filesList.size(); i++) {
                    Panes[i] = new Pane();
                    Panes[i].setPrefSize(500, 70);
                    Panes[i].setLayoutX(0);
                    Panes[i].setLayoutY(0);
                    Panes[i].setStyle("-fx-background-color:  #EEEDEF ; -fx-background-radius: 15;");

                    Label nomfichier = new Label(filesList.get(i));
                    nomfichier.setLayoutX(15);
                    nomfichier.setLayoutY(15);
                    nomfichier.setPrefSize(150,30);
                    Panes[i].getChildren().add(0,nomfichier);



                    Hyperlink releve = new Hyperlink();
                    releve.setText("télecharger");
                    releve.setLayoutX(400);
                    releve.setLayoutY(25);
                    releve.onActionProperty();
                    int finalI = i;
                    releve.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                Socket socket = new Socket(adresse_ip, 1234);
                                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                                objectOutput.writeObject("2");
                                System.out.println(filesList.get(finalI));
                                objectOutput.writeObject(filesList.get(finalI));


                                InputStream in = null;
                                OutputStream out = null;

                                try {
                                    in = socket.getInputStream();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    System.out.println("Can't get socket input stream. ");
                                }

                                try {
                                    out = new FileOutputStream(result +"/"+ filesList.get(finalI));
                                } catch (FileNotFoundException ex) {
                                    ex.printStackTrace();
                                    System.out.println("File not found. ");
                                }

                                byte[] bytes = new byte[16*1024];

                                int count;
                                while ((count = in.read(bytes)) > 0) {
                                    out.write(bytes, 0, count);
                                }

                                objectOutput.close();
                                out.close();
                                in.close();
                                socket.close();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });

                    Panes[i].getChildren().add(1,releve);
                    vb.getChildren().add(i,Panes[i]);
                }

            }
            catch (Exception e){
                e.printStackTrace();
                Label erreur = new Label();
                erreur.setText("adresse ip de serveur éronné réessayer");
                erreur.setLayoutX(305);
                erreur.setLayoutY(397);
                erreur.setPrefSize(200,25);
                rootPane.getChildren().add(erreur);
            }
        }
    }
    public void retour() throws IOException {
        rootPane.getChildren().clear();
        scrollPane.setVisible(false);
        AnchorPane pane = FXMLLoader.load(getClass().getResource("sample.fxml"));
        rootPane.getChildren().setAll(pane);
    }
}
