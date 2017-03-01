/**
 *
 * @author Yatin Rehani
 * Date 10-28-2016
 * This file is used to Decrypt message, Get SHA-1 Hash, compare received Hash with calculated Hash
 * It also implements the methods to record temperature readings
 */

package sensorPackage;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Yatin Rehani
 * Date 10-28-2016
 * This class is used to Decrypt message, Get SHA-1 Hash, compare received Hash with calculated Hash
 * It also implements the methods to record temperature readings
 */
@WebService(serviceName = "SensorService")
public class SensorService {

    private List<Sensor> sensorList = new ArrayList<Sensor>();
    private Map<String, String> lastSensorTemperature = new HashMap<String, String>();

    BigInteger e1 = new BigInteger("65537");

    BigInteger n1 = new BigInteger("2688520255179015026237478731436571621031218154515572968727588377065598663770912513333018006654248650656250913110874836607777966867106290192618336660849980956399732967369976281500270286450313199586861977623503348237855579434471251977653662553");

    BigInteger e2 = new BigInteger("65537");

    BigInteger n2 = new BigInteger("3377327302978002291107433340277921174658072226617639935915850494211665206881371542569295544217959391533224838918040006450951267452102275224765075567534720584260948941230043473303755275736138134129921285428767162606432396231528764021925639519");

    // Decrypt message
    private static byte[] decrypt(String message, BigInteger e, BigInteger N) {
        return (new BigInteger(message)).modPow(e, N).toByteArray();
    }
    //Get SHA-1 Hash
    private static String getMessageDigest(String inputString) {
        byte[] digest = null;
        byte[] digestCopy = null;
        try {
            //Get the bytes from the source string and compute a SHA-1 digest of these bytes.
            digest = MessageDigest.getInstance("SHA1").digest(inputString.getBytes());
            digestCopy = new byte [digest.length+1];
            //Copy these bytes into a byte array that is one byte longer than needed. The resulting byte array has its extra byte set to 1
            digestCopy[0] = 1;
            for(int i=0;i<digest.length;i++)
            {
                digestCopy[i+1] = digest[i];
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SensorService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return javax.xml.bind.DatatypeConverter.printHexBinary(digestCopy);
    }
    
    //compare received Hash with calculated Hash
    //valid if true
    private static boolean validateSignature(String mesageDigest, String decryptHash)
    {
        System.out.println(mesageDigest+"  "+decryptHash);
        if(mesageDigest.equals(decryptHash))
            return true;
        else
            return false;
    }
    /**
     * Web service operation This method record a high temperature reading from
     * a Sensor. A valid signature needs to be passed along with other data from
     * the sensor to record this high temperature
     */
    @WebMethod(operationName = "highTemperature")
    public String highTemperature(@WebParam(name = "sensorId") String sensorId, @WebParam(name = "timeStamp") String timeStamp, @WebParam(name = "type") String type, @WebParam(name = "temperature") String temperature, @WebParam(name = "signature") String signature) {
        String messageDigest = getMessageDigest(sensorId+timeStamp+type+temperature);
        System.out.println("digest "+messageDigest);
        byte[] decryptHash = null;
        if(sensorId.equals("1"))
        {   
            System.out.println(sensorId);
            decryptHash = decrypt(signature,e1,n1);
        }
        else if(sensorId.equals("2"))
        {
            System.out.println(sensorId);
            decryptHash = decrypt(signature,e2,n2);
        }
        boolean isSignatureValid = validateSignature(messageDigest, new String(decryptHash));
        System.out.println(isSignatureValid);
        if (isSignatureValid) {
            Sensor sensor = new Sensor(sensorId, timeStamp, type, temperature);
            sensorList.add(sensor);
            lastSensorTemperature.put(sensorId, temperature + " " + type);
            return "High Temperature Report saved successfully";
        } else {
            return "invalid signature method";
        }
    }

    /**
     * Web service operation This method record a low temperature reading from a
     * Sensor. A valid signature needs to be passed along with other data from
     * the sensor to record this low temperature
     */
    @WebMethod(operationName = "lowTemperature")
    public String lowTemperature(@WebParam(name = "sensorId") String sensorId, @WebParam(name = "timeStamp") String timeStamp, @WebParam(name = "type") String type, @WebParam(name = "temperature") String temperature, @WebParam(name = "signature") String signature) {
        String messageDigest = getMessageDigest(sensorId+timeStamp+type+temperature);
        byte[] decryptHash = null;
        if(sensorId.equals("1"))
        {
            decryptHash = decrypt(signature,e1,n1);
        }
        else if(sensorId.equals("2"))
        {
            decryptHash = decrypt(signature,e2,n2);
        }
        boolean isSignatureValid = validateSignature(messageDigest, new String(decryptHash));
        
        if (isSignatureValid) {
            Sensor sensor = new Sensor(sensorId, timeStamp, type, temperature);
            sensorList.add(sensor);
            lastSensorTemperature.put(sensorId, temperature + " " + type);
            return "Low Temperature Report saved successfully";
        } else {
            return "invalid signature method";
        }
    }

    /**
     * Web service operation This method display all the recorded temperatures
     * from all Sensors. No signature is needed to be passed for this method
     */
    @WebMethod(operationName = "getTemperatures")
    public String getTemperatures() {
        String temp = "Temperatures recorded are : ";

        for (Sensor sensor : sensorList) {
            temp = temp + sensor.getTemperature() + ", ";
        }

        return temp;
    }

    /**
     * Web service operation This method displays particular sensor’s last
     * recorded temperature. The sensor id of the sensor whose last temperature
     * is needed is passed as argument. No signature is needed to be passed for
     * this method
     */
    @WebMethod(operationName = "getLastTemperature")
    public String getLastTemperature(@WebParam(name = "sensorId") String sensorId) {
        //TODO write your implementation code here:
        return "Last Recorded temperature on " + sensorId + " is " + lastSensorTemperature.get(sensorId);
    }
}
