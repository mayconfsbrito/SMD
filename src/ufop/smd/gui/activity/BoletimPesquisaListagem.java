package ufop.smd.gui.activity;

import java.util.ArrayList;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.gui.array_adapter.ArrayAdapterBoletim;
import ufop.smd.modelo.Boletimpesquisa;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class BoletimPesquisaListagem extends BoletimListagem {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Log.d("smd", "Listando os boletins de pesquisa...");
		setContentView(R.layout.listagem_generica);
		inicializaComponentes();

	}

	@Override
	public boolean inicializaComponentes(){

		nomeObjeto = "Boletimpesquisa";
		
		stopCursor(cursor);
		cursor = dao.consultar(nomeObjeto, null, null, null, null, null, "data");

		//Cria uma lista com os boletins
		ArrayList<Boletimpesquisa> list = new ArrayList<Boletimpesquisa>();
		while(cursor != null && !cursor.isAfterLast()){
			list.add(new Boletimpesquisa(cursor));
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

		Boletimpesquisa bol = (Boletimpesquisa) this.getListAdapter().getItem(position);

		Intent it = new Intent(this, BoletimPesquisaVisualizar.class);
		it.putExtra("boletim", bol);
		startActivityForResult(it, Constantes.TELA_BOLETIM_PESQUISA_VISUALIZAR);

	}

	@Override
	protected boolean cadastrar() {

		Intent it = new Intent(this, BoletimPesquisaVisualizar.class);
		startActivityForResult(it, Constantes.TELA_BOLETIM_PESQUISA_VISUALIZAR);

		return true;
	}

}
