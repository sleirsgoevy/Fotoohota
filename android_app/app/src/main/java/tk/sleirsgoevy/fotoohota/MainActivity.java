package tk.sleirsgoevy.fotoohota;

import android.app.*;
import android.os.*;
import android.hardware.*;
import android.view.*;
import java.io.*;
import android.widget.*;

public class MainActivity extends Activity 
{
	private Camera back;
	private int state = 0;
	private static boolean isAlive;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		isAlive = true;
		setResult(57);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		final SurfaceHolder sh = ((SurfaceView)findViewById(R.id.camera)).getHolder();
		sh.addCallback(new SurfaceHolder.Callback()
		{
			@Override
			public void surfaceCreated(SurfaceHolder p1)
			{
				back = Camera.open((state < 2)?1:0);
				startPreview(p1);
			}
			private void startPreview(SurfaceHolder p1)
			{
				try
				{
					if(state < 2)
						state = 1;
					else
						back.setPreviewDisplay(p1);
				}
				catch(IOException e)
				{
					throw new RuntimeException(e);
				}
				back.startPreview();
				if(state < 2)
				{
					back.takePicture(null, null, new Camera.PictureCallback()
					{
						public void onPictureTaken(byte[] pic, Camera cam)
						{
							HttpUtil.sendPicture("register", pic);
							state = 2;
							surfaceDestroyed(sh);
							surfaceCreated(sh);
						}
					});
				}
			}
			@Override
			public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
			{
				back.stopPreview();
				startPreview(p1);
			}
			@Override
			public void surfaceDestroyed(SurfaceHolder p1)
			{
				back.stopPreview();
				back.release();
				back = null;
			}
		});
		((ImageButton)findViewById(R.id.shoot)).setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				back.takePicture(null, null, null, new Camera.PictureCallback()
				{
					public void onPictureTaken(byte[] pic, Camera cam)
					{
						if(state == 2)
							HttpUtil.sendPicture("shoot", pic);
						back.startPreview();
					}
				});
			}
		});
		new Thread()
		{
			@Override
			public void run()
			{
				while(isAlive)
				{
					char sstate = HttpUtil.checkState();
					if(sstate == 'd')
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								setResult(179);
								finish();
							}
						});
					}
					try
					{
						this.sleep(1000);
					}
					catch(InterruptedException e){}
				}
			}
		}.start();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		isAlive = false;
	}
}
