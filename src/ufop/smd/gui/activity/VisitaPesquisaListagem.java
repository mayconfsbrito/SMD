package ufop.smd.gui.activity;

import java.util.ArrayList;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.gui.array_adapter.ArrayAdapterVisita;
import ufop.smd.modelo.Visitapesquisa;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class VisitaPesquisaListagem extends VisitaListagem {

	private Cursor cursor = null;
	private Integer idBoletim;

	@Override
	public boolean inicializaComponentes(){

		stopCursor(cursor);
		idBoletim = getIntent().getExtras().getInt("idBoletim");
		cursor = dao.consultar("Visitapesquisa", null, "idBoletimPesquisa=?", new String[]{idBoletim.toString()}, null, null, "hora");

		//Cria uma lista com os boletins
		ArrayList<Visitapesquisa> list = new ArrayList<Visitapesquisa>();
		while(cursor != null && !cursor.isAfterLast()){
			list.add(new Visitapesquisa(cursor));
			cursor.moveToNext();
		}

		ArrayAdapterVisita arrayAdapter = new ArrayAdapterVisita(this, R.layout.listagem_generica, list);
		setListAdapter(arrayAdapter);

		return true;
	}

	/**
	 * Abre outra activity com o objeto selecionado na listView
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Visitapesquisa vis = (Visitapesquisa) this.getListAdapter().getItem(position);

		Intent it = new Intent(this, VisitaPesquisaVisualizar.class);
		Log.d("smd", "Parcelando em listagem de visitas (consulta).");
		it.putExtra("idBoletim", getIntent().getExtras().getInt("idBoletim"));
		it.putExtra("visita", vis);
		startActivityForResult(it, Constantes.TELA_BOLETIM_PESQUISA_VISUALIZAR);

	}

	/**
	 * Abre a activity de visualização vazia
	 * @return
	 */
	protected boolean cadastrar(){

		if(!verificaUltimaVisita(idBoletim)){

			//Verifica o número de visitas cadastradas
			idBoletim = getIntent().getExtras().getInt("idBoletim");
			stopCursor(cursor);
			cursor = dao.consultar("Visitapesquisa", null, "idBoletimPesquisa=?", new String[]{idBoletim.toString()}, null, null, "hora");
			if(cursor.getCount() < Constantes.MAXIMO_VISITAS_POR_BOLETIM){
			
				Intent it = new Intent(this, VisitaPesquisaVisualizar.class);
				Log.d("smd", "Parcelando em listagem de visitas (cadastro).");
				it.putExtra("idBoletim", getIntent().getExtras().getInt("idBoletim"));
				startActivityForResult(it, Constantes.TELA_VISITA_PESQUISA_VISUALIZAR);
	
				return true;
		
			} else {
				toast("Não é permitido mais de " + Constantes.MAXIMO_VISITAS_POR_BOLETIM +" visitas por boletim.");
			}
			
		} else {
			toast("A última visita do boletim já foi cadastrada.");
			
		}

		return false;
	}

	/**
	 * Verifica se a última visita deste boletim já foi cadastrada
	 * @param obj - Boletim
	 * @return true - ultima visita encontrada, false - ultima visita não encontrada
	 */
	public boolean verificaUltimaVisita(Integer idBoletim){

		//Percorre todas as visitas deste boletim verificando se existe alguma ultima visita cadastrada
		stopCursor(cursor);
		cursor = dao.consultar("Visitapesquisa", null, "idBoletimPesquisa=? AND ultimaVisitaBoletim=1", new String[]{idBoletim.toString()});
		if(cursor != null && !cursor.isAfterLast()){
			return true;
		} 

		return false;
	}

}
