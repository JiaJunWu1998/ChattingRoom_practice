package jim.practice.server;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class server1 extends Frame {
    private TextArea textArea = new TextArea(20,40);
    private Socket socket;
    private Hashtable hashtable = new Hashtable();

    private void launchFrame(){
        this.setTitle("服务器");
        this.setLocation(200,300);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.add(textArea);
        this.setVisible(true);
        pack();
        startSerevr();

    }

    private void revicerMessage(){
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

    private void startSerevr(){
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            //使用多线程解决多个客户端连接服务器问题
            textArea.append("客户端已连接\n");
            while(true) {
                socket = serverSocket.accept();
                ConnService thread = new ConnService(socket);
                new Thread(thread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ConnService implements Runnable{
        private Socket socket;
        private String name;

        public ConnService(Socket socket) {
            this.socket = socket;
            try {
                InputStreamReader input = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(input);
                this.name = reader.readLine();
                hashtable.put(name,socket);
                broadcast(name + "进入了聊天室\n",socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                InputStreamReader input = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(input);
                String context;
                while(true){
                    if((context = reader.readLine()) != null){
                        broadcast(context,socket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //转发客户端的消息
    private void broadcast(String content ,Socket socket){
        Enumeration keys1 = hashtable.keys();
        textArea.append(content+ "\n");
        while (keys1.hasMoreElements()) {
            String keys_name = (String)keys1.nextElement();
            Socket socket1 = (Socket)hashtable.get(keys_name);
            try {
                if(socket1 != socket) {
                    PrintWriter printWriter = new PrintWriter(socket1.getOutputStream(), true);
                    printWriter.println(content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new server1().launchFrame();
    }
}
