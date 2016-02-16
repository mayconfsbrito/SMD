package ufop.smd.controle.sincronizacao;

import java.io.Serializable;

import ufop.smd.controle.Constantes;
import ufop.smd.persistencia.dao.AbstractDAO;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.EditText;

public abstract class AsyncTaskPai extends AsyncTask<Integer, String, Integer>
		implements Serializable {

	protected static final long serialVersionUID = 1L;

	protected transient boolean executando = false;
	protected transient ProgressDialog progress;
	protected transient Context context;
	protected transient Cursor cursor;
	protected transient String tipoSincronizacao;
	protected transient AbstractDAO dao;
	protected String msgProgress = "";
	protected String msg = "";
	protected transient Activity activity;

	public AsyncTaskPai(Context context, Activity activity, Cursor cursor,
			String tipoSincronizacao) {
		super();
		this.context = context;
		this.cursor = cursor;
		this.tipoSincronizacao = tipoSincronizacao;
		this.activity = activity;
		this.dao = new AbstractDAO(context);
	}

	public AsyncTaskPai(Context context, Activity activity, Cursor cursor) {
		super();
		this.context = context;
		this.cursor = cursor;
		this.tipoSincronizacao = Constantes.SINC_USUARIOS;
		this.activity = activity;
		this.dao = new AbstractDAO(context);
	}
	
	public AsyncTaskPai(Context context, Activity activity, EditText text, Cursor cursor, String tipoSincronizacao) {
		this(context, activity, cursor, tipoSincronizacao);
		this.tipoSincronizacao = tipoSincronizacao;
	}
	 
	@Override
	protected void onPreExecute() {
		// Cria novo um ProgressDialogo e exibe
		super.onPreExecute();
		executando = true;
		progress = new ProgressDialog(activity);
		progress.setMessage("Aguarde...");
		if(!progress.isShowing()){
			progress.show();
		}
	}

	@Override
	protected Integer doInBackground(Integer... paramss) {
		try {
			// Sincroniza
			sincronizar(this.tipoSincronizacao);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}

	@Override
	protected void onPostExecute(Integer result) {
		// Cancela progressDialogo
		executando = false;
		progress.dismiss();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		msgProgress = values[0];
		progress.setMessage(msgProgress);
		super.onProgressUpdate(values);
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		if (getStatus() == Status.RUNNING) {
			progress = new ProgressDialog(activity);
			progress.setMessage(msgProgress);
			progress.show();
		}
	}
	
	/**
	 * Fecha o cursor da classe e o banco de dados
	 * @param cursor
	 */
	protected void stopCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	/**
	 * Realiza a sincronização completa com o servidor
	 */
	public abstract boolean sincronizar(String tipoSincronizacao);

	public boolean isExecutando(){
		return executando;
	}
	
	public String getTipoSincronizacao(){
		return this.tipoSincronizacao;
	}
	
	public void setTipoSincronizacao(String tipoSinc){
		this.tipoSincronizacao = tipoSinc;
	}
	
}
