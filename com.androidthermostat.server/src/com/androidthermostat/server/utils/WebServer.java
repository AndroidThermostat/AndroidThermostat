package com.androidthermostat.server.utils;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.androidthermostat.server.data.Conditions;
import com.androidthermostat.server.data.Schedules;
import com.androidthermostat.server.data.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class WebServer {

    private static Context context;
    
    public void init(Context context)
    {
    	WebServer.context = context;
    	try{
	    	Thread t = new RequestListenerThread(8080);
	    	
	        t.setDaemon(false);
	        t.start();
    	} catch (IOException ex) {
    		Utils.debugText = "Error starting web server - " + ex.toString();
    		
    	}
    }

    static class HttpFileHandler implements HttpRequestHandler  {

        //private final String docRoot;

        public HttpFileHandler() {
            super();
            //this.docRoot = docRoot;
        }

        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported");
            }
            String target = request.getRequestLine().getUri();

                        
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                String json = EntityUtils.toString(entity);
                if (target.startsWith("/api/"))
	            {
	            	handleApiPost(target, json, response);
	            }
            } else {
	            if (target.startsWith("/api/"))
	            {
	            	handleApiGet(target, response);
	            } else {
	            	response.setStatusCode(HttpStatus.SC_OK);
	                StringEntity body = new StringEntity("<html><body><h1>Hello World</h1>" + target + "</body></html>");
	                body.setContentType("text/html");
	                response.setEntity(body);            	
	            }
            }
            
        }

        
        private void handleApiGet(String target, HttpResponse response) throws HttpException, IOException
        {
        	response.setStatusCode(HttpStatus.SC_OK);
            
        	Gson gson = new Gson();
        	
        	String json="";
        	if (target.equals("/api/settings")) {
        		json = gson.toJson(Settings.getCurrent());
        	} else if (target.equals("/api/schedules")) {
        		json = gson.toJson(Schedules.getCurrent());
        	} else if (target.equals("/api/conditions")) {
        		gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        		json = gson.toJson(Conditions.getCurrent());
        	}
        	
        	StringEntity body = new StringEntity(json);
            body.setContentType("application/json");
            response.setEntity(body); 
        }
        
        private void handleApiPost(String target, String json, HttpResponse response) throws HttpException, IOException
        {
        	response.setStatusCode(HttpStatus.SC_OK);
        	
        	if (target.equals("/api/settings")) {
        		Settings.load(json);
        		Settings.getCurrent().save(context);
        	} else if (target.equals("/api/conditions")) {
        		//Conditions.getCurrent().load(json);
        		//catch change conditions remotely
        	} else if (target.equals("/api/schedules")) {
        		Schedules.load(json);
        		Schedules.getCurrent().save(context);
        	}
        	StringEntity body = new StringEntity("[]");
            body.setContentType("application/json");
            response.setEntity(body); 
        }
        
        
    }
    
    
    

    static class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams params;
        private final HttpService httpService;

        public RequestListenerThread(int port) throws IOException {
        	
        	BasicHttpParams tempParams = new BasicHttpParams();
            this.serversocket = new ServerSocket(port);
            this.params = (HttpParams) tempParams;
            
            this.params
                .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // Set up the HTTP protocol processor
            HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] {
                    new ResponseDate(),
                    new ResponseServer(),
                    new ResponseContent(),
                    new ResponseConnControl()
            });

            // Set up request handlers
            HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
            registry.register("*", new HttpFileHandler());

            // Set up the HTTP service
                        
            this.httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());

            httpService.setParams(params);
            httpService.setHandlerResolver(registry);
            
                    
        }

        @Override
        public void run() {
            System.out.println("Listening on port " + this.serversocket.getLocalPort());
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    System.out.println("Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    break;
                } catch (IOException e) {
                    System.err.println("I/O error initialising connection thread: "
                            + e.getMessage());
                    break;
                }
            }
        }
    }

    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(
                final HttpService httpservice,
                final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        @Override
        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {}
            }
        }

    }

}