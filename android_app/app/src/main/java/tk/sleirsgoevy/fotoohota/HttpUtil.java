package tk.sleirsgoevy.fotoohota;
import java.net.*;
import java.io.*;
import android.hardware.*;

class HttpUtil
{
	public static void sendPicture(String kind, byte[] pic)
	{
		final String k = kind;
		final byte[] p = pic;
		new Thread()
		{
			public void run()
			{
				while(true)
				{
					try
					{
						HttpURLConnection url = (HttpURLConnection)(new URL("http://"+Config.server_ip+":8080/"+k)).openConnection();
						url.setRequestMethod("POST");
						url.setUseCaches(false);
						url.setDoOutput(true);
						url.getOutputStream().write(p);
						url.getOutputStream().close();
						url.getInputStream().read();
						break;
					}
					catch(IOException e){}
				}
			}
		}.start();
	}
	
	public static char checkState()
	{
		while(true)
		{
			try
			{
				HttpURLConnection url = (HttpURLConnection)(new URL("http://"+Config.server_ip+":8080/check")).openConnection();
				return (char)url.getInputStream().read();
			}
			catch(IOException e){}
		}
	}
}
