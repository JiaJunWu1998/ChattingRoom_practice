package client;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client1 extends Frame implements ActionListener {


    private TextArea textArea = new TextArea(30,50);
    private JLabel label = new JLabel("ip地址");
    private JLabel labe2 = new JLabel("端口号");
    private JLabel labe3 = new JLabel("昵称");
    private TextField nickname = new TextField(20);
    private TextField ip = new TextField("127.0.0.1",20);
    private TextField port = new TextField("8888",10);
    private TextField message = new TextField(40);
    private JButton connbutton = new JButton("连接");
    private JButton sendbutton = new JButton("发送");
    private Panel p1 = new Panel();
    private Panel p2 = new Panel();
    private Socket socket;
    private void launchFrame(){
        this.setTitle("客户端");
        this.setLocation(400,100);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //将控件添加到窗口上
        p1.add(label);
        p1.add(ip);
        p1.add(labe2);
        p1.add(port);
        p1.add(labe3);
        p1.add(nickname);
        p1.add(connbutton);

        p2.add(message);
        p2.add(sendbutton);

        add(p1,BorderLayout.NORTH);
        add(textArea);
        add(p2,BorderLayout.SOUTH);

        pack();
        this.setVisible(true);

        //给button添加事件监听
        connbutton.addActionListener(this);
        sendbutton.addActionListener(this);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        //与服务器建立连接
        if(e.getSource() == connbutton){
            connServer();
            new Thread(new Recivce()).start();
        }
        //发送数据
        else if(e.getSource() == sendbutton){
            sendMessage();
        }
    }

    private void sendMessage(){
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
            printWriter.println(nickname.getText()+ "  " + sdf.format(new Date())+"\n" + message.getText());
            textArea.append("我说："+ message.getText() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connServer(){
        try {
            if (socket == null) {
                //socket实例化，并把IP地址与端口号传入，需要注意的是port需要转化成整型
                socket = new Socket(ip.getText(), Integer.parseInt(port.getText()));
                if (socket != null) {
                    textArea.append("已经与服务器建立连接\n");
                }
                //此处需要注意的是 发送数据的时候需要刷新流，否则数据发送不过去
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                printWriter.println(nickname.getText() + "已进入聊天室");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Recivce implements Runnable{

        @Override
        public void run() {
            recivceMessage();
        }
        private void recivceMessage(){
            try {
                InputStreamReader input = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(input);
                String msg;
                while((msg = reader.readLine()) != null){
                    textArea.append(msg + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client1().launchFrame();
    }


}
