package ufop.smd.gui.activity;

import java.util.ArrayList;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.gui.array_adapter.ArrayAdapterBoletim;
import ufop.smd.modelo.Boletimpesquisa;
import ufop.smd.modelo.Boletimtratamento;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class BoletimTratamentoListagem extends BoletimListagem {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Log.d("smd", "Listando os boletins de tratamento...");
		setContentView(R.layout.listagem_generica);
		inicializaComponentes();

	}

	@Override
	public boolean inicializaComponentes(){

		nomeObjeto = "Boletimtratamento";
		
		stopCursor(cursor);
		cursor = dao.consultar(nomeObjeto, null, null, null, null, null, "data");

		//Cria uma lista com os boletins
		ArrayList<Boletimtratamento> list = new ArrayList<Boletimtratamento>();
		while(cursor != null && !cursor.isAfterLast()){
			list.add(new Boletimtratamento(cursor));
			cursor.moveToNext();
		}

		ArrayAdapterBoletim arrayAdapter = new ArrayAdapterBoletim(this, R.layout.listagem_generica, list);
		setListAdapter(arrayAdapter);

		return true;
	}

	/**
	 * Abre outra activity com o boletim selecionado na listView
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Boletimtratamento bol = (Boletimtratamento) this.getListAdapter().getItem(position);

		Intent it = new Intent(this, BoletimTratamentoVisualizar.class);
		it.putExtra("boletim", bol);
		startActivityForResult(it, Constantes.TELA_BOLETIM_TRATAMENTO_VISUALIZAR);

	}

	@Override
	protected boolean cadastrar() {

		Intent it = new Intent(this, BoletimTratamentoVisualizar.class);
		startActivityForResult(it, Constantes.TELA_BOLETIM_TRATAMENTO_VISUALIZAR);

		return true;
	}

}
