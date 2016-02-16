package ufop.smd.gui.activity;

import java.util.ArrayList;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.gui.array_adapter.ArrayAdapterVisita;
import ufop.smd.modelo.Visitatratamento;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public abstract class VisitaListagem extends ActivityPaiList {

	private Cursor cursor = null;
	private Integer idBoletim;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Log.d("smd", "Listando as visitas...");
		setContentView(R.layout.listagem_generica);
		inicializaComponentes();

	}

	@Override
	public void onStart(){
		super.onStart();
		inicializaComponentes();

	}

	@Override
	public abstract boolean inicializaComponentes();
	
	@Override
	public boolean addListeners() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);

		menu.removeItem(Constantes.MENU_LOGOFF);
		menu.removeItem(Constantes.MENU_CONFIGURACAO);
		menu.add(0, Constantes.MENU_CADASTRAR, 0, "Cadastrar");
		menu.add(1, Constantes.MENU_CONFIGURACAO, 1, "Configurações");
		menu.add(1, Constantes.MENU_LOGOFF, 2, "Logoff");

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureItem, MenuItem item){

		switch(item.getItemId()){

		//Evento do menuItem de Logoff
		case Constantes.MENU_LOGOFF:
			logoff();
			break;

		case Constantes.MENU_CADASTRAR:
			cadastrar();
			break;
			
		case Constantes.MENU_CONFIGURACAO:
			configuracao();
			break;

		}

		return true;
	}

	/**
	 * Abre a activity de visualizaçõo vazia
	 * @return
	 */
	protected abstract boolean cadastrar();
	
	/**
	 * Verifica se a última visita deste boletim já foi cadastrada
	 * @param obj - Boletim
	 * @return true - ultima visita encontrada, false - ultima visita não encontrada
	 */
	public abstract boolean verificaUltimaVisita(Integer idBoletim);

}
