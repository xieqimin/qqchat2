import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Friend{
    //待完善
    private Label label=new Label();
    private Client client;
    private ReceiveMessage receiveMessage;
    private ArrayList<NewLable> friends=new ArrayList<>();
    public void addFriend(String ffname,String ffid){
        NewLable newLable=new NewLable(client,ffname,ffid);
        friends.add(newLable);
    }
    public Friend(Client client,String name){
        this.client=client;
        label.setText(name);
    }
    public void setReceiveMessage(ReceiveMessage receiveMessage) {
        this.receiveMessage=receiveMessage;
        new Thread(receiveMessage).start();
    }
    public void ThreadStop(){
        receiveMessage.setStop(false);
    }

    public Stage getUI(){
        VBox vBox=new VBox();
        for(int i=0;i<friends.size();i++){
            vBox.getChildren().add(friends.get(i));
        }
        ScrollPane scrollPane=new ScrollPane();
        Button addfBut=new Button("加好友");
        scrollPane.setContent(vBox);
        BorderPane borderPane=new BorderPane();
        borderPane.setCenter(scrollPane);
        //addfui
        GridPane gridPane=new GridPane();
        TextField textField=new TextField();
        Button button=new Button("添加");
        Label label1=new Label("");
        gridPane.add(new Label("好友ID"),0,0);
        gridPane.add(textField,1,0);
        gridPane.add(button,1,1);
        gridPane.add(label1,0,1);
        button.setOnAction(event ->{
            String fid=textField.getText();
            if(client.addFriend(fid)){
                label1.setText("添加成功");
            }else {
                label1.setText("没有此id");
            }

        });
        Scene scene11=new Scene(gridPane);
        Stage stage11=new Stage();
        stage11.setScene(scene11);
        stage11.setMinWidth(300);
        stage11.setMinHeight(180);
        //addfui
        borderPane.setTop(label);
        addfBut.setOnAction(e->{
            stage11.show();
        });
        borderPane.setBottom(addfBut);
        Scene scene=new Scene(borderPane);
        Stage stage=new Stage();
        stage.setScene(scene);
        stage.setOnCloseRequest(e->{
            client.closeAllIDchatUI();
            ThreadStop();
        });
        return stage;
    }
    class NewLable extends Label{
        private String fID;
        //private Client client;
        public NewLable(Client client, String fname,String fID){
            //this.client=client
            this.setText(fname);
            this.fID=fID;
            this.setPrefSize(230,70);
            this.setOnMouseClicked(e->{
                ChatUI chatUI=new ChatUI(client,fname,fID);
                Stage stag=chatUI.getUI();
                client.setIDchatUI(fID,chatUI);
                stag.show();
                try {
                    String s = client.sMessage(fID, client.getMyid());
                    if(!s.equals("no message on")) {
                        chatUI.addMessage(s);
                    }
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            });
        }
    }
}
