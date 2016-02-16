package ufop.smd.gui.activity;

import ufop.smd.controle.Constantes;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;

public abstract class BoletimListagem extends ActivityPaiList {

	protected Cursor cursor = null;
	protected String nomeObjeto = null;

	@Override
	public void onStart(){
		super.onStart();
		inicializaComponentes();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);

		menu.removeItem(Constantes.MENU_LOGOFF);
		menu.removeItem(Constantes.MENU_CONFIGURACAO);
		menu.add(1, Constantes.MENU_CADASTRAR, 0, "Cadastrar");
		menu.add(1, Constantes.MENU_CONFIGURACAO, 1, "Configuração");
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
	
	@Override
	public boolean addListeners() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public abstract boolean inicializaComponentes();
	
	/**
	 * Abre a activity de visualização de boletim vazia para o usuário cadastrar um novo boletim
	 * @return
	 */
	protected abstract boolean cadastrar();
	
}
