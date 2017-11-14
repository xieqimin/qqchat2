import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class  LoginUI{//client
    private Client client;
    private TextField nameText=new TextField();
    private PasswordField passwText=new PasswordField();
    private Label label=new Label("");
    public void setLabel(String text) {
        this.label.setText(text);
    }
    public LoginUI(Client client){
        this.client=client;
    }
    public Stage getUI(){
        GridPane gridPane=new GridPane();
        //gridPane.setPadding(new Insets(11,12,15,11));
        gridPane.setVgap(5.5);
        gridPane.setHgap(6);
        gridPane.setAlignment(Pos.CENTER_LEFT);
        Label label1=new Label("账号");
        Label label2=new Label("密码");
        Button button=new Button("登陆");
        Button button1=new Button("注册账号");

        GridPane grid=new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField tname=new TextField();
        TextField tid=new TextField();
        PasswordField tpas=new PasswordField();
        Button button2=new Button("注册");
        Label lreg=new Label("");
        grid.add(new Label("账号"),0,0);
        grid.add(tid,1,0);
        grid.add(new Label("用户名"),0,1);
        grid.add(tname,1,1);
        grid.add(new Label("密码"),0,2);
        grid.add(tpas,1,2);
        grid.add(button2,1,3);
        grid.add(lreg,0,4);
        Scene sce=new Scene(grid);
        Stage sta=new Stage();
        sta.setMinHeight(350);
        sta.setMinWidth(450);
        sta.setScene(sce);

        button1.setOnAction(e->{
            sta.show();
        });
        button2.setOnAction(e->{
            boolean b =client.Register(tid.getText(),tname.getText(),tpas.getText());
            //检查账号密码是否符合规范
            if(b){
                lreg.setText("注册成功");
                sta.close();
            }
            else{
                lreg.setText("该账号已被注册");
            }
        });
        button.setPrefSize(150,40);
        button.setStyle("-fx-text-fill: rgb(49, 89, 23);-fx-background:#09a3dc;" +
                "-fx-color: #09a3dc;" + "-fx-border-radius: 5;" + "-fx-padding: 3 6 6 6;");
        nameText.setPrefSize(260,40);
        passwText.setPrefSize(260,40);
        gridPane.add(label1,0,0);
        gridPane.add(nameText,1,0);
        gridPane.add(label2,0,1);
        gridPane.add(passwText,1,1);
        gridPane.add(button,1,2);
        gridPane.add(label,1,3);
        gridPane.add(button1,0,3);
        Scene scene=new Scene(gridPane);
        Stage stage=new Stage();
        button.setOnAction(event ->{
            String id=nameText.getText();
            String password=passwText.getText();
            try {
                String st=client.checkpass(id,password);
                setLabel(st);
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.setMinWidth(300);
        stage.setMinHeight(300);
        stage.setScene(scene);
        return stage;
    }
}