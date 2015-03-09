/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vision;

import java.io.IOException;
import static java.lang.Math.acos;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class User {
    public int[] gpsCoord = new int[2];
    public int cardinalDirection;
    private final double visionField;
    public int hash;
    public ArrayList<Projection> list;

    User(int[] initialUserPos, double userFieldOfVision, int initialUserDirection) {
        this.cardinalDirection = initialUserDirection;
        this.visionField = userFieldOfVision;
        this.gpsCoord = initialUserPos;
        this.hash = generateHash(this.gpsCoord);
        this.list = new ArrayList<>();
    }
        


    /**
     * Updates information about the users current position
     * @param inGPS This assigns the current gps coordinate
     * @param inDirection This assigns the current direction
     */
    public void updateUser(Scanner inGPS, Scanner inDirection) {
        this.gpsCoord = getGpsCoord(inGPS);
        this.cardinalDirection = getCardinalDirection(inDirection);
        this.hash = generateHash(this.gpsCoord);
    }
    /**
     * Generates a hash of the current gps coordinate
     * @param tempCoord 
     * @return 
     */
    private int generateHash(int[] gpsCoord) {
        short a = (short) gpsCoord[0];
        short b = (short) gpsCoord[1];
        long sum = (long) a + b;
        return (int) (sum * (sum + 1) / 2) + a;        
    }
    /**
     * Returns the current gps coordinate.
     * @param inGPS
     * @return 
     */
    private int[] getGpsCoord(Scanner inGPS) {
        int[] Coord = new int[2];
        Coord[0] = Integer.parseInt(inGPS.nextLine());
        Coord[1] = Integer.parseInt(inGPS.nextLine());
        return Coord;
    }
    /**
     * Returns the current direction.
     * @param inDirection
     * @return 
     */
    private int getCardinalDirection(Scanner inDirection) {
        return Integer.parseInt(inDirection.nextLine());
    }
    /**
     * Determines whether a Projection is seen or not.
     * @param gpsCoordProj
     * @return 
     */
    public boolean checkVision(int[] gpsCoordProj) {
        double minbound = this.visionField * 0.5;
        double maxbound = 360 - (this.visionField * 0.5);
        //arbitrary point along visual axis
        double[] p1 = {
            this.gpsCoord[0] + cos(this.cardinalDirection),
            this.gpsCoord[1] + sin(this.cardinalDirection)
        };
        //point of user
        double[] p3 = {
            this.gpsCoord[0],
            this.gpsCoord[1]
        };
        //point of projection
        double[] p2 = {
            gpsCoordProj[0],
            gpsCoordProj[1]
        };
        System.out.println("this is arbitrary point p1: " + Arrays.toString(p1));
        System.out.println("this is projection point p2: " + Arrays.toString(p2));
        System.out.println("this is user point p3: " + Arrays.toString(p3));

        double vectormag1 = Math.sqrt(Math.pow(p1[0] - p2[0],2) + Math.pow(p1[1] - p2[1],2));
        double vectormag2 = Math.sqrt(Math.pow(p1[0] - p3[0],2) + Math.pow(p1[1] - p3[1],2));
        System.out.println("this is vectormag1: " + vectormag1);
        System.out.println("this is vectormag2: " + vectormag2);
        double dotprod = getAngle(p1, p2, p3);//(
                    //vectormag1 * vectormag1 + 
                    //vectormag2 * vectormag2 -
                   // )
                //);
        System.out.println("this is the dotprod: " + dotprod);
        double theta = Math.toDegrees(getAngle(p1,p2,p3));
                Math.toDegrees(
                Math.acos(
                    ((dotprod) / 
                    ((vectormag1 * vectormag2)))
            ));
        
        System.out.println("this is the angel: " + theta);
        System.out.println("this is the minbound: " + minbound);
        System.out.println("this is the maxbound: " + maxbound);
        System.out.println("Are we within the bounds? " + (theta<minbound||theta>maxbound));
        return (theta < minbound || theta > maxbound);
    }
    
    public double findAngle(double[] p3, double[] p2, double[] p1){
        double p1p2 = Math.sqrt(Math.pow(p1[0] - p2[0],2) + Math.pow(p1[1] - p2[1],2));
        double p1p3 = Math.sqrt(Math.pow(p1[0] - p3[0],2) + Math.pow(p1[1] - p3[1],2));
        double p2p3 = Math.sqrt(Math.pow(p2[0] - p3[0],2) + Math.pow(p2[1] - p3[1],2));
        return Math.acos(((p1p2*p1p2)+(p1p3*p1p3)-(p2p3*p2p3)/(2*p1p2*p1p3)));
    }
    /**
     * 
     * @param proj
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void inVision(Projection proj) throws IOException, ClassNotFoundException{
        double distance = ceil(getDistance( proj.gpsCoord[0],proj.gpsCoord[1],this.gpsCoord[0],this.gpsCoord[1]));
        //currently cannot correctly clone projection, instantiate new projection workaround.
        //Projection show = Projection.clone(proj);
        int newX = (int) ceil(
                (proj.dimensions[0])*
                (proj.maxDist/distance)
        );
        int newY = (int) ceil( 
                (proj.dimensions[1])*
                (proj.maxDist/distance)
        );
        Projection show = new Projection(proj.name,proj.gpsCoord,newX,newY,proj.URL,proj.maxDist);
        this.list.add(show);
    }
    
    /**
     * 
     * @param pointOneX
     * @param pointOneY
     * @param pointTwoX
     * @param pointTwoY
     * @return 
     */
    private double getDistance(double pointOneX, double pointOneY, double pointTwoX, double pointTwoY){
        return sqrt((
                pow((pointOneX - pointTwoX),2)+
                pow((pointOneY - pointTwoY),2)
                ));
    }
    /**
     * 
     */
    public void displayProjections() {
        if(!this.list.isEmpty()){
            System.out.println("wooh");
            System.out.println("The Name of this projection is: " + this.list.get(0).name);
            System.out.println("The dimensions of this projection are: " + Arrays.toString(this.list.get(0).dimensions));
            System.out.println();
        }else{
            System.out.println("no luck");
        }
    }

    private double getAngle(double[] p1, double[] p2, double[] p3) {
                double angle1 = Math.atan2(p1[1]-p3[1],p1[0]-p3[0]);
                double angle2 = Math.atan2(p2[1]-p3[1],p2[0]-p3[0]);
                return angle1-angle2;
    }
}
