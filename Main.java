package org.openstreetmap.gui.jmapviewer;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.util.*;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main{
	static double longitude;
	static double latitude;
	JMapViewer map;
    public Main(){
        
    }
    public void updatePoses(JMapViewer map) throws InterruptedException{
        this.map = map;
        Thread.sleep(5000);
        System.out.println("uppdaterar");
        map.addMapMarker(new MapMarkerDot(getLatitude(), getLongitude()));
        updatePoses(map);
    }
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(4800,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                
                (new Thread(new SerialReader(in))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }

    public static class SerialReader implements Runnable {
        InputStream in;
        public SerialReader (InputStream in){
            this.in = in;
        }
        /**
         * Fredrik Gustafsson
         */
        public void run (){
            byte[] buffer = new byte[1024];
            int len = -1;
            StringBuffer line2 = new StringBuffer();
            StringBuffer checkThisLine = new StringBuffer();
            boolean dollar = false;
            boolean record = false;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                	String line = new String(buffer, 0, len);
                	if (line.equals("$")){
                		record = false;
                		dollar = true;
                	}
                	if (dollar == true && line2.length() <= 6){
                		line2.append(line);
                	}
                	if (record == true && dollar == false){
                		checkThisLine.append(line);
                	}
                	if (dollar == true && record == false && checkThisLine.length() > 1){
                		String checkString = checkThisLine.toString();
                		String[] strings = checkString.split(",");
                		String latitudeString = strings[2];
                		String longitudeString = strings[4];
                		
                		if (latitudeString.length() > 4 && longitudeString.length() > 4){
                    		String latitudeShort = latitudeString.substring(2, latitudeString.length());
                    		System.out.println(latitudeShort);
                    		double latitudeDouble = Double.parseDouble(latitudeShort);
                    		latitudeDouble = latitudeDouble / 60;
                    		String latitudeBig = latitudeString.substring(0,2);
                    		double latitudeBigInt = Double.parseDouble(latitudeBig);
                    		latitudeDouble = latitudeBigInt + latitudeDouble;
                    		
                    		String longitudeShort = longitudeString.substring(3, longitudeString.length());
                    		double longitudeDouble = Double.parseDouble(longitudeShort);
                    		longitudeDouble = longitudeDouble / 60;
                    		String longitudeBig = longitudeString.substring(0,3);
                    		double longitudeBigInt = Double.parseDouble(longitudeBig);
                    		longitudeDouble = longitudeBigInt + longitudeDouble;
                    		
                    		longitude = longitudeDouble;
                    		latitude = latitudeDouble;
                    		System.out.println(strings[2] + ", " + strings[4]);
                		}
                		Main main = new Main();
                		System.out.println("Latitude: " + main.getLatitude() + ", Longitude: " + main.getLongitude());
                		checkThisLine.delete(0, checkThisLine.length());
                	}
                	if (line2.length() == 6 && record == false){
                	    dollar = false;
                		String thisLine = line2.toString();
                		String gpgga = "$GPGGA";
                		if (thisLine.equals(gpgga)){
                			record = true;
                		}
                		
                		line2.delete(0, line2.length());
                	}
                	if (line2.length() > 6)
                	    line2.delete(0, line2.length());
                }
            }
            catch ( IOException e ){
                e.printStackTrace();
            }            
        }
    }
    
    public static void main ( String[] args )
    {
        CommPortIdentifier portId;
        Enumeration en = CommPortIdentifier.getPortIdentifiers();
        Vector listData = new Vector(8);
        String port = new String();

        // Walk through the list of port identifiers and, if it
        // is a serial port, add its name to the list.
        while (en.hasMoreElements()) {
            portId = (CommPortIdentifier) en.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                port = portId.getName();
            }
        }
        try
        {
            (new Main()).connect(port);
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public double getLongitude(){
    	return longitude;
    }
    public double getLatitude(){
    	return latitude;
    }
}