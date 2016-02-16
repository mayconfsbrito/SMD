package ufop.smd.gui.activity;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity que controla as opções iniciais do sistema
 * 
 * @author maycon
 *
 */
public class Opcoes extends ActivityPai{
	
	//Componentes da Gui
	private Button btBolT;
	private Button btBolP;
	private Button btCroqui;
	private Button btSinc;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opcoes);
	
		inicializaComponentes();
	}
	
	@Override
	public boolean inicializaComponentes() {
		
		btBolT = (Button) findViewById(R.id.btBolTrat);
		btBolP = (Button) findViewById(R.id.btBolPes);
		btCroqui = (Button) findViewById(R.id.btCroqui);
		btSinc = (Button) findViewById(R.id.btSincronizar);
		
		addListeners();
		
		return true;
	}

	@Override
	public boolean addListeners() {
	
		btBolT.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				boletinsTratamento();
			}
			
		});
		
		btBolP.setOnClickListener(new OnClickListener(){
			
			public void onClick(View view){
				boletinsPesquisa();
			}
		});
		
		btCroqui.setOnClickListener(new OnClickListener(){
			
			public void onClick(View view){
				visualizarCroqui();
			}
			
		});
		
		btSinc.setOnClickListener(new OnClickListener(){
			
			public void onClick(View view){
				sincronizar();
			}
		});
		
		return false;
	}
	
    /**
     * Abre a activity para visualizaçõo de croqui
     */
    private void visualizarCroqui(){
    	
    	Intent it = new Intent(this, LocalidadeCroqui.class);
    	startActivityForResult(it, Constantes.TELA_LOCALIDADE_CROQUI);

    }
    
    /**
     * Abre a activity de boletins de tratamento
     */
    private void boletinsTratamento(){
    	
    	Intent it = new Intent(this, BoletimTratamentoListagem.class);
    	startActivityForResult(it, Constantes.TELA_BOLETIM_TRATAMENTO_LISTAGEM);
    	    	
    }
    
    /**
     * Abre a activity de boletins de pesquisa
     */
    private void boletinsPesquisa(){
    	
    	Intent it = new Intent(this, BoletimPesquisaListagem.class);
    	startActivityForResult(it, Constantes.TELA_BOLETIM_PESQUISA_LISTAGEM);
    	
    }
    
    /**
     * Abre a activity de sincronização
     */
    private void sincronizar(){
    	
    	Intent it = new Intent(this, Sincronizacao.class);
    	startActivityForResult(it, Constantes.TELA_SINCRONIZACAO);
    }

}
