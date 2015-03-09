package vision;
/**
 * imported packages from Google's Guava API due to ease of implementation of multimaps
 */
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Arrays;

/**
 * The purpose of this algorithm is to take spatial data from a "heads up display" and
 * project onto the surface information pertaining to objects in the 3d world such as markers,
 * advertisement, notes, or other spatially sensitive information.
 * @author Daniel Zeiler
 */
public class Vision {
    private static boolean testOn(int timer) {
        while(timer<10000){
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param userAlpha
     * @param projectionList
     * @param projectionMap 
     */
    public static void checkProj(User userAlpha, ArrayList<Projection> projectionList, Multimap<Integer, Integer>projectionMap){
        int index = 0;
        for (Projection Projection : projectionList) {
            index++;
            if(getDistance(userAlpha.gpsCoord,Projection.gpsCoord) > Projection.maxDist){
                if(Projection.timer > 100){
                    removeHashes(projectionMap, Projection, index);
                    //I will need to reconfigure the projectionList indexes that correspond to the hashmap key=>projection structure.
                    //This can be done at 'start up', however, over an extended period memory consumption would continue.
                    //projectionList.remove(Projection);
                }else{
                    Projection.timer++;
                }
            }else{
                Projection.timer = 0;
            }
        }
    }
    
    /**
     * 
     * @param userGpsCoord
     * @param projGpsCoord
     * @return 
     */
    private static int getDistance(int[] userGpsCoord, int[] projGpsCoord) {
        return (int) sqrt(
                pow((userGpsCoord[0] - projGpsCoord[0]),2) +
                pow((userGpsCoord[1] - projGpsCoord[1]),2)
        );
    }
    
    /**
     * 
     * @param hash
     * @param finalMap
     * @return 
     */
    private static boolean checkHash(int hash, com.google.common.collect.Multimap<Integer, Integer> finalMap) {
        return finalMap.containsKey(hash);
    }
    
    /**
     * 
     * @param Projection
     * @param index
     * @param projectionMap 
     */
    public static void addHashes(Projection Projection, int index, Multimap<Integer, Integer> projectionMap) {
        int[] tempCoord = new int[2];
        for(int i = 0; i < calcSpace(Projection.maxDist); i++) {
            tempCoord[0]=Projection.gpsArea[i][0];
            tempCoord[1]=Projection.gpsArea[i][1];
            projectionMap.put(generateHash(tempCoord), index);
        } 
    }
    
    /**
     * 
     * @param projectionMap
     * @param projection
     * @param index 
     */
    private static void removeHashes(Multimap<Integer, Integer> projectionMap, Projection projection, int index) {
        int[] tempCoord = new int[2];
        for(int i = 0; i < calcSpace(projection.maxDist); i++) {
            tempCoord[0]=projection.gpsArea[i][0];
            tempCoord[1]=projection.gpsArea[i][1];
            projectionMap.remove(generateHash(tempCoord), index);
        }
    }
    
    /**
     * 
     * @param maxDist
     * @return 
     */
    public static int calcSpace(int maxDist) {
        return  ((2 * (maxDist) + 1) * (2 * (maxDist) + 1));
    }
    
    /**
     * 
     * @param tempCoord
     * @return 
     */
    private static int generateHash(int[] tempCoord) {
        short a = (short) tempCoord[0];
        short b = (short) tempCoord[1];
        long sum = (long) a + b;
        return (int) (sum * (sum + 1) / 2) + a;        
    }
    
    public static void generateProjections(Projection[] projectionArray, Multimap<Integer, Integer> projectionMap,ArrayList<Projection> projectionList){
        int index=0;
        for(Projection projection: projectionArray){
            projectionList.add(projection);
            addHashes(projection,index,projectionMap);
            index++;
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException{
        java.io.File file = new java.io.File("userGps.txt");
        Scanner inGPS = new Scanner(file);
        Scanner inDirection = new Scanner(new File("userDirection.txt"));
        //Scanner inProjections = new Scanner(new File("projections.txt"));
        int[] display1GPS = {2, 2};
        int[] display2GPS = {90, 33};
        ArrayList<Projection> projectionList = new ArrayList<>();
        Multimap<Integer, Integer> projectionMap = ArrayListMultimap.create();
        Projection[] projectionArray = {
            new Projection("display1", display1GPS, 100, 100, "www.helloworld.com", 2),
            new Projection("display2", display2GPS, 100, 100, "www.goodbyeworld.com", 5)
        };
        generateProjections(projectionArray, projectionMap, projectionList);
        int[] initialUserPos = {0, 0};
        double userFieldOfVision = 120;
        int initialUserDirection = 90;
        User userAlpha = new User(initialUserPos, userFieldOfVision, initialUserDirection);
        int timer=0;
        while(testOn(timer)){
            userAlpha.list.clear();
            timer++;
            checkProj(userAlpha, projectionList, projectionMap);
            userAlpha.updateUser(inGPS, inDirection);
            System.out.println(Arrays.toString(userAlpha.gpsCoord) + "   " + userAlpha.cardinalDirection);
            if(checkHash(userAlpha.hash, projectionMap)){
                Collection<Integer> realizedProjections = projectionMap.get(userAlpha.hash);
                for(Integer o :  realizedProjections){
                    System.out.println("this is the name: " + (projectionList.get(0).name));
                    System.out.println("this is the tostring: " + Arrays.toString(projectionList.get(0).gpsCoord));
                    if(userAlpha.checkVision(projectionList.get(o).gpsCoord)){
                        userAlpha.inVision(projectionList.get(o));
                    }
                }
                userAlpha.displayProjections();
            }
        }
    }
}