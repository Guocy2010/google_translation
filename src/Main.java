import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


class TFile {
	
	public boolean readFile(String path) {
		try {
			String encoding = "UTF-8";
			File file = new File(path);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println(lineTxt);
					String[] record = lineTxt.split("\\t");
					String[] new_record = new String[record.length + 1];
					for (int i = 0; i < record.length; i++) {
						new_record[i] = record[i];
					}
					src_list.add(new_record);
				}
				read.close();
				return true;
			} else {
				System.out.println("can not find file: " + path);
			}
		} catch (Exception e) {
			System.out.println("error of reading the contents of file: " + path);
			e.printStackTrace();
		}
		return false;
	}
	
	public void writeFile(String path) {
		String encoding = "UTF-8";
		File file = new File(path);
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), encoding);
			
			for (int i = 0; i < src_list.size(); i++) {
				String[] t = src_list.get(i);
				for (int t_i = 0; t_i < t.length; t_i++) {
					if (t_i > 0) {
						write.write("\t");
					}
					write.write(t[t_i] + "");
				}
				write.write("\n");
			}
			
			write.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public  void print() {
		int number = 0;
		for (int i = 0; i < src_list.size(); i++) {
			String[] t = src_list.get(i);
			if (t[t.length - 1] != null && !t[t.length - 1].isEmpty()) {
				number++;
			}
		}
		System.out.print("\nfinished:" + number + "/" + src_list.size());
	}
	
	public  ArrayList<String[]> src_list = new ArrayList<String[]>();
	public 	int 				index = 0;
}

class TConfig {
	
	TConfig() {
		method = 0;
		src_lang = "en";
		dst_lang = "zh-CN";
		tm_start = 0;
	}
	
	public	int			method;
	public	String		src_lang;
	public	String		dst_lang;
	
	public  long		tm_start;
}

class TThread implements Runnable {
	
	public TThread(TFile file, TConfig config) {
		tf = file;
		tc = config;
		if (tc.method == 0) {
			tr.getTKK();
		}
	}
	
	public void run() {
		while (true) {
			String[] t = null;
			synchronized(tf) {
				if (tf.index < tf.src_list.size()) {
					t = tf.src_list.get(tf.index++);
					if (tf.index %10 == 0) {
						float speed = 1000 * tf.index/(System.currentTimeMillis() - tc.tm_start);
						System.out.print("processed:" + tf.index + "(" + String.format("%.2f/s", speed) + ")\r");
					}
				} else {
					break;
				}
			}
			if (tc.method == 0) {
				t[t.length - 1] = tr.translate(tc.src_lang, tc.dst_lang, t[t.length - 2]);
			} else if (tc.method == 1) {
				t[t.length - 1] = ms.search(t[t.length - 2]);
			}
			t[t.length - 1] = t[t.length - 1] == null ? "" : t[t.length - 1];
		}
	}
	
	public	Translation tr = new Translation();
	public	TFile		tf = null;
	public  MapSearch	ms = new MapSearch();
	public	TConfig		tc = null;
}

public class Main {
	
	public static void do_main(String src_file, String dst_file, int thread_num, TConfig config) {
		long startTime = System.currentTimeMillis();
		
		TFile 	tr_file 	= new TFile();
		
		if (tr_file.readFile(src_file)) {
			ArrayList<Thread>  tr_thread_list = new ArrayList<Thread>();
			
			for (int i = 0; i < thread_num; i++) {
				tr_thread_list.add(new Thread(new TThread(tr_file, config)));
			}
			
			for (int i = 0; i < tr_thread_list.size(); i++) {
				tr_thread_list.get(i).start();
			}

			try {
				for (int i = 0; i < tr_thread_list.size(); i++) {
					tr_thread_list.get(i).join();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			tr_file.writeFile(dst_file);
		}
		
		tr_file.print();
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("\nexecution time: "+(endTime - startTime)/1000 + "s");
	}
	
	public static void main(String[] args) {
		
		// print usage information
		if (args.length < 1) {
			System.out.println("Usage:");
			System.out.println("        java -jar google_translate.jar file [options]\n");
			System.out.println("Options:");
			System.out.println("        -t|--thread      number of threads used to process");
			System.out.println("        -m|--method      0: google translation; 1: google mapsearch");
			System.out.println("        -s|--src_lang    this represents the source language of google translation,");
			System.out.println("                         and is only used when -m is 0. default is en");
			System.out.println("        -d|--dst_lang    this represents the destination language of google translation,");
			System.out.println("                         and is only used when -m is 0. default is zh-CN");
			return;
		}
		
		String 		src_file	 	= args[0];
		int 		thread_count	= 1;
		TConfig		config			= new TConfig();
		
		config.tm_start = System.currentTimeMillis();
		
		// process arguments
		for (int i = 0; i < args.length; i++) {
			// -t|--thread
			if (args[i].compareToIgnoreCase("-t") == 0 || args[i].compareToIgnoreCase("--thread") == 0) {
				if (i + 1 < args.length) {
					thread_count = Integer.parseInt(args[i + 1]);
					if (thread_count < 1) {
						System.out.println("error: options -t|--thread should be given an positive integer");
						return;
					}
				} else {
					System.out.println("error: options -t|--thread should be given an positive integer");
					return;
				}
			}
			
			// -m|--method
			if (args[i].compareToIgnoreCase("-m") == 0 || args[i].compareToIgnoreCase("--method") == 0) {
				if (i + 1 < args.length) {
					config.method = Integer.parseInt(args[i + 1]);
					if (config.method != 0 && config.method != 1) {
						System.out.println("error: options -m|--method should be given 0 or 1");
						return;
					}
				} else {
					System.out.println("error: options -m|--method should be given 0 or 1");
					return;
				}
			}
			
			// -s|--src_lang
			if (args[i].compareToIgnoreCase("-s") == 0 || args[i].compareToIgnoreCase("--src_lang") == 0) {
				if (i + 1 < args.length) {
					config.src_lang = args[i + 1].trim();
					if (config.src_lang.isEmpty()) {
						System.out.println("error: options -s|--src_lang should be given a valid string value");
						return;
					}
				} else {
					System.out.println("error: options -s|--src_lang should be given a string value");
					return;
				}
			}
			
			// -d|--dst_lang
			if (args[i].compareToIgnoreCase("-d") == 0 || args[i].compareToIgnoreCase("--dst_lang") == 0) {
				if (i + 1 < args.length) {
					config.dst_lang = args[i + 1].trim();
					if (config.dst_lang.isEmpty()) {
						System.out.println("error: options -d|--dst_lang should be given a valid string value");
						return;
					}
				} else {
					System.out.println("error: options -d|--dst_lang should be given a string value");
					return;
				}
			}
		}
		
		do_main(src_file, src_file + ".out", thread_count, config);
	}
}
