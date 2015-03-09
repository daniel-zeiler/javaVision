/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vision;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class Projection {
        public String name;
        public int[] gpsCoord = new int[2];
        public int[] dimensions = new int[2];
        public String URL;
        public int maxDist;
        public int[][] gpsArea;
        public int timer;        
        /**
         * 
         * @param name
         * @param gpsCoord
         * @param xDim
         * @param yDim
         * @param URL
         * @param maxDist
         */
    Projection(String name, int[] gpsCoord, int xDim, int yDim, String URL, int maxDist) {
            this.name = name;
            this.gpsCoord = gpsCoord;
            this.dimensions[0] = xDim;
            this.dimensions[1] = yDim;
            this.URL = URL;
            this.maxDist = maxDist;
            this.gpsArea = new int[Vision.calcSpace(this.maxDist)][2];
            this.gpsArea = calcArea(this.gpsArea, this.gpsCoord, this.maxDist);
            this.timer=0;
    }
    
    /**
     * 
     * @param inputCommand
     * @param variableCommand 
     */
    public void manipulationOfVariables(String inputCommand, Object... variableCommand){
        switch (inputCommand) {
            case "name":
                this.name=(String)variableCommand[0];
                break;
            case "URL":
                this.URL = (String)variableCommand[1];
                break;
            case "gpsCoord":
                this.gpsCoord[0]=(int)variableCommand[0];
                this.gpsCoord[1]=(int)variableCommand[1];
                this.gpsArea = calcArea(this.gpsArea, this.gpsCoord, this.maxDist);
                break;
            case "maxDist":
                this.maxDist = (int)variableCommand[0];
                int[][] newArea = new int[Vision.calcSpace(this.maxDist)][2];
                newArea = calcArea(newArea, this.gpsCoord, this.maxDist);
                this.gpsArea = newArea;
                break;
        }
    }

    /**
     * 
     * @param gpsArea
     * @param gpsCoord
     * @param maxDist
     * @return 
     */
    private int[][] calcArea(int[][] gpsArea, int[] gpsCoord, int maxDist) {
    
    int xDim = gpsCoord[0] - maxDist;
    int yDim = gpsCoord[1] - maxDist;
    int tracker = 0;
    for(int i = 0; i < 2 * (maxDist) + 1; i++){
        for(int j = 0; j < 2 * (maxDist) + 1; j++){
            gpsArea[tracker][0] = xDim + i;
            gpsArea[tracker][1] = yDim + j;
            tracker++;
        }
    }
    return gpsArea;
    }
   
    /**
     * 
     * @param proj
     * @return 
     */
    static Projection clone(Projection proj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SerializeToOutputStream((Serializable) proj, bos);
            byte[] bytes = bos.toByteArray();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));        
            return (Projection)ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Projection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;    
    }
    private static void SerializeToOutputStream(Serializable ser, OutputStream os) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
           oos.writeObject(ser);
           oos.flush();
        }
    }
}