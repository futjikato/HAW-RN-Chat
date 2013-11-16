package de.haw.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chatserver
 */
public class Server {

    private ServerSocket serverSocket;

    private ThreadPoolExecutor threadPoolExecutor;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(50);
        threadPoolExecutor = new ThreadPoolExecutor(5, 20, 5, TimeUnit.SECONDS, queue);
    }

    public void start() {
        Thread currentThread = Thread.currentThread();

        while(!currentThread.isInterrupted()) {
            try {
                // accept socket
                Socket socket = serverSocket.accept();

                // initialize new worker
                try {
                    Worker worker = new Worker(socket);

                    // execute with thread from threadpool
                    threadPoolExecutor.execute(worker);
                } catch (IOException e) {
                    e.printStackTrace();

                    // close socket if reader or writer could not be initialized
                    if(!socket.isClosed()) {
                        socket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                currentThread.interrupt();
            }
        }
    }

}
