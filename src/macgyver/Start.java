package macgyver;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.io.*;

import javax.swing.*;

import org.xml.sax.SAXException;

public class Start {
    OsmParser parser;
    Point center;
    Statement statement;
    public Start() throws SAXException, IOException{
        parser = new OsmParser("/home/gustehn/Skrivbord/liten.osm");
        for (int i = 0; i < OsmParser.vertices.size(); i++){
            if (OsmParser.vertices.get(i).adjacencies.size() == 0){
                OsmParser.vertices.remove(i);
            }
        }
        try{
            try{
                String url = "jdbc:mysql://kodsyntes.se:9797/spel"; // --""--
                Connection connection = DriverManager.getConnection(url, "spel", "apa56bepa57");
                statement = connection.createStatement();
            }catch(Exception esc){
                JOptionPane.showMessageDialog(null, "Please check your JDBC- and MYSQL-setup");
                System.exit(0);
            }
            for (int i = 0; i < OsmParser.vertices.size(); i++){
                String insertBorrowers = "INSERT INTO nodes VALUES(" + "'" + OsmParser.vertices.get(i).name + "', " + "'" + OsmParser.vertices.get(i).lon + "', " + "'" + OsmParser.vertices.get(i).lat + "')";
                statement.executeUpdate(insertBorrowers);
                for (int j = 0; j < OsmParser.vertices.get(i).adjacencies.size(); j++){
                    if (j == 0){
                        String insertBorrowerz = "INSERT INTO ways (name, adj1) VALUES(" + "'" + OsmParser.vertices.get(i).name + "', " + "'" + OsmParser.vertices.get(i).adjacencies.get(j).target.name + "')";
                        statement.executeUpdate(insertBorrowerz);
                    }else{
                        String insertBorrowerz = "UPDATE ways SET adj" + (j+1) + " = " + "'" + OsmParser.vertices.get(i).adjacencies.get(j).target.name + "' WHERE name = '" + OsmParser.vertices.get(i).name +"'";
                        statement.executeUpdate(insertBorrowerz);
                    }
                }
            }
//            String giveme = "SELECT name FROM nodes WHERE lon = '11.934293746948242' AND lat = '57.70769500732422'";
//            ResultSet rs = statement.executeQuery(giveme);
//            String nameR = "";
//            while (rs.next()){
//                nameR = rs.getString("name");
//            }
//            System.out.println(nameR);
//            ArrayList<Long> shortID = new ArrayList<Long>();
//            long nameL;
//            for (int i = 0; i < 19; i++){
//                String giveme2 = "SELECT adj" + (i+1) +  "FROM ways WHERE name = '" + nameR + "'";
//                rs = statement.executeQuery(giveme2);
//                while (rs.next()){
//                    nameR = rs.getString("adj" + (i+1));
//                    nameL = Long.parseLong(nameR);
//                    shortID.add(nameL);
//                }
//            }
//            for (int i = 0; i < shortID.size(); i++){
//                String giveme3 = "SELECT lon, lat FROM nodes WHERE name = " + shortID.get(i);
//                rs = statement.executeQuery(giveme);
//                double lon;
//                double lat;
//                String lonS = "";
//                String latS = "";
//                while (rs.next()){
//                    lonS = rs.getString("lon");
//                    double
//                }
//            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public int LonToX(double aLongitude) {
        int x = convertLat(Double.toString(aLongitude));
        int max = 115645;
        int min = 115534;
        int length = max - min;
        x = x - min;
        double procent = (double)x / length;
//        double xD = procent * getWidth();
//        x = (int)xD;
//        x -= (getWidth() / 2);
        
        return x;
    }
    public int LatToY(double aLatitude) {
        int y = convertLat(Double.toString(aLatitude));
        int max = 574159;
        int min = 574239;
        y = y - min;
        double length = max - min;
        double procent = (double)y / length;
//        double yD = procent * getWidth();
//        y = (int)yD;
        return y;
    }
    public static void main(String[] args) throws SAXException, IOException{
        Start frame = new Start();
    }
    public int convertLat(String lat){
        if (lat.substring(0,1).equals("-")){
            lat = lat.substring(1, lat.length());
        }
        String degString = lat.substring(0, 2);
        String latRemainder  = ("0." + lat.substring(3, lat.length()));
        double remDouble = Double.parseDouble(latRemainder);
        remDouble = remDouble * 60;
        String remString = Double.toString(remDouble);
        String remStringNew = remString.substring(0, 2);
        String minValue = remStringNew;
        if (minValue.length() == 1){
            minValue = "0" + minValue;
        }
        String secString = ("0." + remString.substring(3, remString.length()));
        double secDouble = Double.parseDouble(secString);
        secDouble = secDouble * 60;
        secDouble = Math.round(secDouble);
        String secString2 = Double.toString(secDouble);
        if (secString2.length() == 3){
            if (Integer.parseInt(secString2.substring(0, 1)) >= 5){
                secString2 = secString2 + "0";
            }
            else{
                secString2 = "0" + secString2;
            }
        }
        double degDouble = Double.parseDouble(degString);
        double minDouble = Double.parseDouble(minValue);
        
        String degString2 = degString + minValue + secString2;
        degString2 = degString2.trim();
        double degDouble2 = Double.parseDouble(degString2);
        return((int)degDouble2);
        
    }
}
