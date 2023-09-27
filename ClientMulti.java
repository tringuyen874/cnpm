import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientMulti {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientMulti(Socket socket, String clientUsername) {
        try {
            this.socket = socket;
            this.clientUsername = clientUsername;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));   
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(clientUsername);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = sc.nextLine();
                bufferedWriter.write(clientUsername + ": " + messageToSend);
                bufferedWriter.newLine(); 
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String messageFromGroupChat;
                while(socket.isConnected()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch (Exception e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }

        }).start();
    }

    protected void closeEverything(Socket socket2, BufferedReader bufferedReader2, BufferedWriter bufferedWriter2) {
        try {
            if (bufferedReader2 != null) {
                bufferedReader2.close();
            }

            if (bufferedWriter2 != null) {
                bufferedWriter2.close();
            }

            if (socket2 != null) {
                socket2.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = sc.nextLine();
        Socket socket = new Socket("localhost" ,2468);
        ClientMulti client = new ClientMulti(socket, username);
        client.listenForMessages();
        client.sendMessage();
    }

    

    
}
