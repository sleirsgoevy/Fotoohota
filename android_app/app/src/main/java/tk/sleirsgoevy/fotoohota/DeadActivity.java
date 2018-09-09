package tk.sleirsgoevy.fotoohota;
import android.app.*;
import android.os.*;

public class DeadActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		setContentView(R.layout.dead);
		setResult(57);
	}
}
