package ufop.smd.gui.activity;

import ufop.smd.controle.Constantes;
import ufop.smd.controle.Login;
import ufop.smd.persistencia.dao.AbstractDAO;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class ActivityPaiList extends ListActivity {

	AbstractDAO dao = new AbstractDAO(this);
		Cursor cursor = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
	
    /**
     * Inicializa os componentes da Gui
     */
    public abstract boolean inicializaComponentes();
    
	/**
     * Adiciona os listeners da activity
     */
    public abstract boolean addListeners();
    
    /**
     * Cria o menu padrão da maioria das activitys
     */
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0, Constantes.MENU_LOGOFF, 0, "Logoff");
    	menu.add(1, Constantes.MENU_CONFIGURACAO, 1, "Configuração");
    	
    	return true;
    }
    
    /**
     * Implementa as opções do menu
     */
    public boolean onMenuItemSelected(int featureItem, MenuItem item){

    	switch(item.getItemId()){
    	
    		//Evento do menuItem de Logoff
    		case Constantes.MENU_LOGOFF:
    			logoff();
    			break;
    			
    		case Constantes.MENU_CONFIGURACAO:
    			configuracao();
    			break;
    			
    	}
    	
    	return true;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	
    	switch (resultCode){
    		case Constantes.LOGOFF:
    			logoff();
    			break;
    	}

    }
    
    /**
     * Realiza o logoff do usuário
     */
    protected void logoff(){
    	
    	Login.desconectaUsuario(this);
    	setResult(Constantes.LOGOFF);
    	finish();
    }
    
    /**
	 * Abre a activity de configuração da apk
	 */
	protected void configuracao(){
		
		Intent it = new Intent(this, Configuracao.class);
		startActivityForResult(it, Constantes.TELA_CONFIGURACAO);
	}
    
    /**
     * Exibe o toast com a mensagem enviada por parâmetro
     */
    public void toast(String str){
    	Toast.makeText(this, str, Constantes.TOAST_DURATION).show();
    }
	
    /**
     * Fecha o cursor da classe e o banco de dados
     * @param cursor
     */
    protected void stopCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			stopManagingCursor(cursor);
			cursor.close();
		}
	}
    
    
}
