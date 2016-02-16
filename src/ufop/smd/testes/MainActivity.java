package ufop.smd.testes;

import java.io.Serializable;

import ufop.smd.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class MainActivity extends Activity{

	private TesteAsync testes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teste);

		EditText editText = (EditText) findViewById(R.id.editTeste);
		this.registerForContextMenu(editText);

		if (savedInstanceState == null) {
			Log.d("ufop.smd", "savedInstanceState == null");
			testes = new TesteAsync(this);
			testes.execute();
		} else {
			Log.d("ufop.smd", "savedInstanceState != null");
			testes = (TesteAsync) savedInstanceState.getSerializable("1");
			testes.setActivity(this);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("ufop.smd", "onSaveInstanceState");
		outState.putSerializable("1", testes);
		super.onSaveInstanceState(outState);
	}

	public class TesteAsync extends AsyncTask<Object, Object, Object> implements
			Serializable {

		private static final long serialVersionUID = 1L;
		private transient Activity activity;
		private transient ProgressDialog progressDialog;

		public TesteAsync(Activity activity) {
			super();
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			publishProgress("1");
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Object... params) {
			try {
				Thread.sleep(15000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			Log.d("ufop.smd", "onProgressUpdate");
			if (String.valueOf(values[0]).compareTo("1") == 0) {
				progressDialog = new ProgressDialog(activity);
				progressDialog.setMessage("teste");
				progressDialog.show();
			}
			super.onProgressUpdate(values);
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
			Log.d("ufop.smd", "setActivity");
			if (getStatus() == Status.RUNNING) {
				Log.d("ufop.smd", "getStatus() == Status.RUNNING");
				publishProgress("1");
			}
		}

	}
}
