package ufop.smd.gui.activity;

import java.io.Serializable;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.controle.sincronizacao.AsyncTaskSincronizacao;
import ufop.smd.controle.sincronizacao.AsyncTaskSincronizacaoUsuarios;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Sincronizacao extends ActivityPai implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient EditText etLog;
	private transient Button btSincTudo;
	private transient Button btSincBol;
	private transient AsyncTaskSincronizacao asyncTask;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sincronizacao);

		inicializaComponentes();

		if (savedInstanceState != null) {
			if(!asyncTask.isExecutando()){
				asyncTask = (AsyncTaskSincronizacao) savedInstanceState.getSerializable("1");
				asyncTask.setActivity(this, etLog);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("1", asyncTask);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean inicializaComponentes() {

		stopCursor(cursor);
		etLog = (EditText) findViewById(R.id.etLog);
		btSincTudo = (Button) findViewById(R.id.btSincTudo);
		btSincBol = (Button) findViewById(R.id.btSincBol);
		asyncTask = new AsyncTaskSincronizacao(this, this, etLog, cursor, Constantes.SINC_COMPLETO);
		addListeners();

		return false;
	}

	@Override
	public boolean addListeners() {

		btSincTudo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				executaAsyncTask(Constantes.SINC_COMPLETO);
			}
		});

		btSincBol.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				executaAsyncTask(Constantes.SINC_BOL);
			}
		});

		return false;
	}

	public void executaAsyncTask(String tipoSinc) {
		if(!asyncTask.isExecutando()){
			try{
				etLog.setText("");
				asyncTask.setTipoSincronizacao(tipoSinc);
				asyncTask.execute();
			} catch(java.lang.IllegalStateException ex){
				asyncTask = new AsyncTaskSincronizacao(this, this, etLog, cursor, tipoSinc);
				etLog.setText("");
				asyncTask.execute();
			}
		}
	}

}
