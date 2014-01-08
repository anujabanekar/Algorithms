import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class FileHandle {

	public  void createFile(String fname) {
		
		File f = new File(fname);
		if(f.exists())
			return;
		
		try {
			f.createNewFile();
		}
		catch (Exception e) {
			System.out.println("Error - " +e);
		}
		return;
	}
		

	public static void write(String str, String fname, boolean appendMode) {
	
		DataOutputStream out = null;	
		try{
			OutputStream outputStream = new FileOutputStream(fname, appendMode);
			out = new DataOutputStream(outputStream);
			out.write(str.getBytes());
			out.close();
			/*
			FileWriter fstream = new FileWriter(fname);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(str.getBytes());
			  out.close();*/
		}
		catch(Exception e){
			System.out.println("error"+e);
		}
		
	}

	
	// read Lines from a File
	public static List<String> read(String fname)  {
		
		List<String> strs = new ArrayList<String>();
		
		try{
			FileInputStream fis = new FileInputStream(fname);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			String str;
	
			while ( (str = br.readLine()) != null)   {
				strs.add(str);
			}
			
			br.close();
		} catch (Exception e){
			System.out.println("error"+e);
		}
		
		return strs;
	}

}
