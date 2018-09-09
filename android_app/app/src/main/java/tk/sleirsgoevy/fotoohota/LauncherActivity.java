package tk.sleirsgoevy.fotoohota;
import android.app.*;
import android.os.*;
import android.view.View.*;
import android.widget.*;
import android.view.*;
import android.content.*;

public class LauncherActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedState)
	{
		final LauncherActivity self = this;
		super.onCreate(savedState);
		setContentView(R.layout.launcher);
		((Button)findViewById(R.id.start)).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent();
				i.setClass(self, MainActivity.class);
				startActivityForResult(i, 0);
			}
		});
	}
	@Override
	protected void onActivityResult(int req, int res, Intent i)
	{
		if(res == 57)
			finish();
		else if(res == 179)
		{
			i = new Intent();
			i.setClass(this, DeadActivity.class);
			startActivityForResult(i, 0);
		}
		else if(res == 126)
		{
			i = new Intent();
			i.setClass(this, MainActivity.class);
			startActivityForResult(i, 0);
		}
	}
}
