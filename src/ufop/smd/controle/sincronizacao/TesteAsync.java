package ufop.smd.controle.sincronizacao;

import java.io.Serializable;

import ufop.smd.controle.Constantes;
import ufop.smd.persistencia.dao.AbstractDAO;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;

public class TesteAsync extends AsyncTask<Object, Object, Object> implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private transient Activity activity;
	private transient ProgressDialog progress;
	protected transient Context context;
	protected transient Cursor cursor;
	protected transient String tipoSincronizacao;
	protected transient AbstractDAO dao;
	protected transient String msg = "";

	public TesteAsync(Context context, Activity activity, Cursor cursor,
			String tipoSincronizacao) {
		super();
		this.context = context;
		this.cursor = cursor;
		this.tipoSincronizacao = tipoSincronizacao;
		this.activity = activity;
		this.dao = new AbstractDAO(context);
	}

	public TesteAsync(Context context, Activity activity, Cursor cursor) {
		super();
		this.context = context;
		this.cursor = cursor;
		this.tipoSincronizacao = Constantes.SINC_USUARIOS;
		this.activity = activity;
		this.dao = new AbstractDAO(context);
	}

	@Override
	protected void onPreExecute() {
		// Cria novo um ProgressDialogo e exibe
		Log.d("ufop.smd", "onPreExecute");
		super.onPreExecute();
		progress = new ProgressDialog(activity);
		progress.setMessage("Aguarde...");
		progress.show();
	}

	@Override
	protected Object doInBackground(Object... params) {
		try {
			Log.d("ufop.smd", "doInBackground");
			Thread.sleep(15000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		Log.d("ufop.smd", "onProgressUpdate");

		progress = new ProgressDialog(activity);
		progress.setMessage(values.toString());
		progress.show();
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

	protected void onPostExecute(Integer result) {
		Log.d("ufop.smd", "onPostExecute");
		// Cancela progressDialogo
		progress.dismiss();

	}

}
