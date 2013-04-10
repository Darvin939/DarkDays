package darvin939.DarkDays.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class CipherUtil {
	public String read(String dir, String filename) {
		String out = "";
		try {
			File localFile = new File(dir, filename);
			Cipher localCipher = getCipher(2, "darkdayslangfile");
			DataInputStream localDataInputStream;
			if (localCipher != null)
				localDataInputStream = new DataInputStream(new CipherInputStream(new FileInputStream(localFile), localCipher));
			else {
				localDataInputStream = new DataInputStream(new FileInputStream(localFile));
			}
			out = localDataInputStream.readUTF();
			localDataInputStream.close();
		} catch (Exception e) {
		}
		return out;
	}

	public Map<String, String> readMap(String dir, String filename) {
		String str = read(dir, filename);
		Properties props = new Properties();
		if (!str.isEmpty())
			try {
				props.load(new StringReader(str.substring(1, str.length() - 1).replace(", ", "\n")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		Map<String, String> map = new HashMap<String, String>();
		for (Map.Entry<Object, Object> e : props.entrySet()) {
			map.put((String) e.getKey(), (String) e.getValue());
		}
		for (Entry<String, String> set : map.entrySet()) {
			set.setValue(set.getValue().replaceAll("COMMASPACE", ", "));
		}
		return map;
	}

	public void writeMap(String dir, String filename, Map<String, String> map) {
		for (Entry<String, String> set : map.entrySet()) {
			set.setValue(set.getValue().replaceAll(", ", "COMMASPACE"));
		}
		write(dir, filename, map.toString());

	}

	public void write(String dir, String filename, String data) {
		try {
			File path = new File(dir);
			if (!path.exists())
				path.mkdir();
			File localFile = new File(dir, filename);
			Cipher localCipher = getCipher(1, "darkdayslangfile");
			DataOutputStream localDataOutputStream;
			if (localCipher != null)
				localDataOutputStream = new DataOutputStream(new CipherOutputStream(new FileOutputStream(localFile), localCipher));
			else {
				localDataOutputStream = new DataOutputStream(new FileOutputStream(localFile));
			}
			localDataOutputStream.writeUTF(data);
			localDataOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Cipher getCipher(int paramInt, String paramString) throws Exception {
		Random localRandom = new Random(43287234L);
		byte[] arrayOfByte = new byte[8];
		localRandom.nextBytes(arrayOfByte);
		PBEParameterSpec localPBEParameterSpec = new PBEParameterSpec(arrayOfByte, 5);
		SecretKey localSecretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(paramString.toCharArray()));
		Cipher localCipher = Cipher.getInstance("PBEWithMD5AndDES");
		localCipher.init(paramInt, localSecretKey, localPBEParameterSpec);
		return localCipher;
	}
}
